package b6.persistence.support;

import b6.persistence.CatalogDao;
import b6.persistence.model.CatalogItemSortType;
import b6.persistence.model.generated.B6db;
import b6.persistence.util.IdUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Nonnull;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Alexander Shabanov
 */
@Transactional(propagation = Propagation.REQUIRED)
public final class DefaultCatalogDao implements CatalogDao {
  private final Logger log = LoggerFactory.getLogger(getClass());
  private final JdbcOperations db;

  public DefaultCatalogDao(JdbcOperations jdbcOperations) {
    this.db = Objects.requireNonNull(jdbcOperations, "jdbcOperations");
  }

  @Nonnull
  @Override
  public B6db.CatalogItem getCatalogItemById(@Nonnull String idStr) {
    final long id = IdUtil.toLong(idStr);

    final B6db.Item item = db.queryForObject(
        "SELECT i.title, e.name AS type_name FROM item AS i\n" +
            "INNER JOIN entity_type AS e ON e.id=i.type_id\n" +
            "WHERE i.id=?",
        (rs, i) -> getItem(rs), id);
    final B6db.CatalogItem.Builder result = B6db.CatalogItem.newBuilder().setId(idStr).setItem(item);

    final B6db.Extensions.Builder extBuilder = B6db.Extensions.newBuilder();
    if (CatalogDao.BOOK_TYPE.equals(item.getType())) {
      final List<B6db.DownloadItem> downloadItems = db.query(
          "SELECT d.file_size, i.title AS origin_name, d.download_id, \'\' AS descriptor_text\n" +
              "FROM item_download AS d\n" +
              "INNER JOIN item AS i ON i.id=d.origin_id\n" +
              "WHERE d.item_id=?",
          (rs, i) -> getDownloadItem(rs), id);

      extBuilder.setBook(B6db.BookExtension.newBuilder()
          .addAllDownloadItems(downloadItems));
      result.setExtensions(extBuilder);
    } else if (log.isDebugEnabled()) {
      log.debug("Unknown item type={}", item.getType());
    }

    return result.build();
  }

  @Nonnull
  @Override
  public List<B6db.CatalogItem> queryCatalogItems(
      @Nonnull String relatedItemId,
      @Nonnull String startItemId,
      @Nonnull String titleFilter,
      @Nonnull String typeFilter,
      @Nonnull CatalogItemSortType sortType,
      int limit) {
    if (!log.isTraceEnabled()) {
      return queryCatalogItemsNoOpt(
          IdUtil.toLong(relatedItemId),
          IdUtil.toLong(startItemId),
          titleFilter,
          typeFilter,
          sortType,
          limit);
    }

//    final long relItemId = IdUtil.toLong(relatedItemId);
//    final long stItemId = IdUtil.toLong(startItemId);

//    final B6db.Item item = db.queryForObject(
//        "SELECT i.title, e.name AS type_name FROM item AS i\n" +
//            "INNER JOIN entity_type AS e ON e.id=i.type_id\n" +
//            "WHERE i.id=?",
//        (rs, i) -> getItem(rs), id);

    throw new UnsupportedOperationException();
  }

  @Nonnull
  @Override
  public List<B6db.Relation> getRelations(@Nonnull String toIdStr) {
    final long toId = IdUtil.toLong(toIdStr);
    return db.query("SELECT r.lhs, r.rhs, e.name AS type_name FROM item_relation AS r\n" +
            "INNER JOIN entity_type AS e ON e.id=r.type_id\n" +
            "WHERE rhs=?",
        (rs, i) -> getRelation(rs), toId);
  }

  @Override
  public void removeRelations(@Nonnull String idStr) {
    final long id = IdUtil.toLong(idStr);
    db.update("DELETE item_relation WHERE lhs=? OR rhs=?", id, id);
  }

  @Override
  public void saveRelations(@Nonnull List<B6db.Relation> relations) {
    db.execute("INSERT INTO item_relation (lhs, rhs, type_id) VALUES (?, ?, ?)",
        (PreparedStatement ps) -> {
          for (final B6db.Relation relation : relations) {
            ps.setLong(1, IdUtil.toLong(relation.getFromId()));
            ps.setLong(2, IdUtil.toLong(relation.getToId()));
            ps.setLong(3, getEntityTypeIdFromName(relation.getType()));
            ps.addBatch();
          }
          return ps.executeBatch();
        });
  }

  @Nonnull
  @Override
  public String persistCatalogItem(@Nonnull B6db.CatalogItem item) {
    long id = IdUtil.toLong(item.getId());

    // See changes in commit e949c307d2447ecfe210c5e734a4aef5ee0cd479 -
    // e.g. in b6-website/src/main/java/b6/website/service/support/DefaultCatalogService.java
    //final List<Long> ids = db.queryForList("SELECT seq_item.nextval FROM system_range(1, ?)", Long.class, newIdCount);

    final long typeId = db.queryForObject("SELECT id FROM entity_type WHERE name=?",
        Long.class, item.getItem().getType());

    if (id > 0) {
      // update operation
      db.update("UPDATE item SET type_id=?, title=? WHERE id=?", typeId, item.getItem().getTitle(), id);

      // TODO: make it optional (e.g. - include distinguishable fragments)
      db.update("DELETE item_download WHERE item_id=?", id); // delete all download items
    } else {
      // insert operation
      id = db.queryForObject("SELECT seq_item.nextval", Long.class);
      db.update("INSERT INTO item (id, type_id, title) VALUES (?, ?, ?)", id, typeId, item.getItem().getTitle());
    }

    if (item.hasExtensions()) {
      if (item.getExtensions().hasBook()) {
        // book
        final B6db.BookExtension bookExt = item.getExtensions().getBook();
        final List<B6db.DownloadItem> downloadItems = bookExt.getDownloadItemsList();
        long finalId = id;
        final int[] ins = db.execute("INSERT INTO item_download\n" +
                "(item_id, file_size, origin_id, download_id)\n" +
                "VALUES (?, ?, ?, ?)",
            (PreparedStatement ps) -> {

              //noinspection ForLoopReplaceableByForEach
              for (int i = 0; i < downloadItems.size(); ++i) {
                final B6db.DownloadItem downloadItem = downloadItems.get(i);
                ps.setLong(1, finalId);
                ps.setLong(2, downloadItem.getFileSize());
                ps.setLong(3, getItemIdFromName(downloadItem.getOriginName()));
                ps.setString(4, downloadItem.getDownloadId());
                ps.addBatch();
              }

              return ps.executeBatch();
            });

        log.debug("Book extension update, inserted={}", ins);
      } else if (log.isDebugEnabled()) {
        log.debug("Unrecognized extension in item={}", item);
      }
    }

    return IdUtil.fromLong(id);
  }

  //
  // Private
  //

  @Nonnull
  private List<B6db.CatalogItem> queryCatalogItemsNoOpt(
      long relatedItemId,
      long startItemId,
      @Nonnull String titleFilter,
      @Nonnull String typeFilter,
      @Nonnull CatalogItemSortType sortType,
      int limit) {
    // brain dead implementation of querying (no SQL builders)
    final List<Long> ids;
    if (relatedItemId > 0) {
      ids = db.queryForList("SELECT rhs FROM item_relation WHERE lhs=?", Long.class, relatedItemId);
    } else {
      ids = db.queryForList("SELECT id FROM item", Long.class);
    }

    final List<B6db.CatalogItem> allItems = ids.stream()
        .map((id) -> getCatalogItemById(IdUtil.fromLong(id)))
        .collect(Collectors.toList());
    final Optional<B6db.CatalogItem> cursorItem = allItems.stream()
        .filter(item -> IdUtil.toLong(item.getId()) == startItemId).findFirst();

    final List<B6db.CatalogItem> resultItems = allItems.stream()
        .filter(item -> {
          if ((StringUtils.hasLength(typeFilter) && !item.getItem().getType().equals(typeFilter)) ||
              (StringUtils.hasLength(titleFilter) && !item.getItem().getTitle().startsWith(titleFilter))) {
            return false;
          }

          if (cursorItem.isPresent()) {
            switch (sortType) {
              case TITLE_ASCENDING:
                return cursorItem.get().getItem().getTitle().compareTo(item.getItem().getTitle()) < 0;

              case TITLE_DESCENDING:
                return cursorItem.get().getItem().getTitle().compareTo(item.getItem().getTitle()) > 0;

              default:
                return cursorItem.get().getId().compareTo(item.getId()) < 0; // start w/ IDs greater than given one
            }
          }

          return true;
        })
        .sorted((o1, o2) -> {
          switch (sortType) {
            case TITLE_ASCENDING:
              return o1.getItem().getTitle().compareTo(o2.getItem().getTitle());
            case TITLE_DESCENDING:
              return o2.getItem().getTitle().compareTo(o1.getItem().getTitle());
            default:
              return o1.getId().compareTo(o2.getId());
          }
        })
        .limit(limit)
        .collect(Collectors.toList());

    log.trace("Items = {}", resultItems);

    return resultItems;
  }

  private long getItemIdFromName(@Nonnull String itemName) {
    // TODO: optimize - use cache (leasing architecture)
    return db.queryForObject("SELECT id FROM item WHERE title=?", Long.class, itemName);
  }

  private long getEntityTypeIdFromName(@Nonnull String entityTypeName) {
    // TODO: optimize - use cache (leasing architecture)
    return db.queryForObject("SELECT id FROM entity_type WHERE name=?", Long.class, entityTypeName);
  }

  private static B6db.Relation getRelation(@Nonnull ResultSet rs) throws SQLException {
    return B6db.Relation.newBuilder()
        .setFromId(IdUtil.fromLong(rs.getLong("lhs")))
        .setToId(IdUtil.fromLong(rs.getLong("rhs")))
        .setType(rs.getString("type_name"))
        .build();
  }

  private static B6db.Item getItem(@Nonnull ResultSet rs) throws SQLException {
    return B6db.Item.newBuilder()
        .setTitle(rs.getString("title"))
        .setType(rs.getString("type_name"))
        .build();
  }

  private static B6db.DownloadItem getDownloadItem(@Nonnull ResultSet rs) throws SQLException {
    return B6db.DownloadItem.newBuilder()
        .setFileSize(rs.getInt("file_size"))
        .setOriginName(rs.getString("origin_name"))
        .setDownloadId(rs.getString("download_id"))
        .setDescriptorText(rs.getString("descriptor_text"))
        .build();
  }
}
