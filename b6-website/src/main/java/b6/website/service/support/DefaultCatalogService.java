package b6.website.service.support;

import b6.model.catalog.*;
import b6.website.model.catalog.SortType;
import b6.website.service.CatalogService;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Nonnull;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

import static b6.website.model.Ids.isValidId;

/**
 * @author Alexander Shabanov
 */
@Transactional(propagation = Propagation.REQUIRED)
public final class DefaultCatalogService extends AbstractService implements CatalogService {

  private final JdbcOperations db;

  public DefaultCatalogService(JdbcOperations jdbcOperations) {
    this.db = Objects.requireNonNull(jdbcOperations, "jdbcOperations");
  }

  @Nonnull
  public CatalogItem getItem(long id) {
    return withExtension(db.queryForObject(
        "SELECT i.id, et.name AS type_name, i.title FROM item AS i\n" +
            "INNER JOIN entity_type AS et ON i.type_id=et.id WHERE i.id=?",
        CATALOG_ITEM_ROW_MAPPER, id));
  }

  @Override
  @Nonnull
  public List<CatalogItem> getItems(long relatedItemId,
                                    long startItemId,
                                    @Nonnull String titleFilter,
                                    @Nonnull String typeFilter,
                                    @Nonnull SortType sortType,
                                    int limit) {
    final StringBuilder qb = new StringBuilder(256);
    final List<Object> params = new ArrayList<>(10);

    qb.append("SELECT i.id, et.name AS type_name, i.title FROM item AS i\n" +
        "INNER JOIN entity_type AS et ON i.type_id=et.id\n");

    // add related item id and WHERE clause
    if (isValidId(relatedItemId)) {
      qb.append("INNER JOIN item_relation AS ir ON ir.rhs=i.id\n");
    }
    qb.append("WHERE\n");
    if (isValidId(relatedItemId)) {
      qb.append("ir.lhs=?\n");
      params.add(relatedItemId);
    } else {
      qb.append("1=1\n");
    }

    // apply type filter
    if (StringUtils.hasLength(typeFilter)) {
      qb.append("AND et.name=?\n");
      params.add(typeFilter);
    }

    // apply name filter
    if (StringUtils.hasLength(titleFilter)) {
      // TODO: mask check
      // TODO: this requires full scan and indexes can't be built for this type
      // TODO: (continued) of queries, need to consider actually having separate query for this
      qb.append("AND i.title LIKE ?\n");
      params.add(titleFilter + "%");
    }

    final CatalogItem catalogItem;

    // apply sorting
    switch (sortType) {
      case TITLE_ASCENDING:
        if (isValidId(relatedItemId)) {
          catalogItem = getItem(relatedItemId);
          qb.append("AND i.title>? OR (i.title=? AND i.id>?)\n");
          params.add(catalogItem.getTitle());
          params.add(catalogItem.getTitle());
          params.add(relatedItemId);
        }
        qb.append("ORDER BY title ASC, id ASC\n");
        break;

      case TITLE_DESCENDING:
        if (isValidId(relatedItemId)) {
          catalogItem = getItem(relatedItemId);
          qb.append("AND i.title<? OR (i.title=? AND i.id<?)\n");
          params.add(catalogItem.getTitle());
          params.add(catalogItem.getTitle());
          params.add(relatedItemId);
        }
        qb.append("ORDER BY title DESC, id DESC\n");
        break;

      default:
        if (isValidId(startItemId)) {
          qb.append("AND i.id>?\n");
          params.add(startItemId);
        }
        qb.append("ORDER BY id\n");
    }

    qb.append("LIMIT ?");
    params.add(limit);

    return db.query(qb.toString(), CATALOG_ITEM_ROW_MAPPER, params.toArray(new Object[params.size()])).stream()
        .map(this::withExtension)
        .collect(Collectors.toList());
  }

  @Nonnull
  @Override
  public List<Long> persistItems(@Nonnull List<CatalogItem> catalogItems) {
    if (catalogItems.isEmpty()) {
      return Collections.emptyList();
    }

    // get next N IDs
    final int newIdCount = (int) catalogItems.stream().filter(i -> !isValidId(i.getId())).count();
    final List<Long> ids = db.queryForList("SELECT seq_item.nextval FROM system_range(1, ?)", Long.class, newIdCount);

    final List<Long> result = Arrays.asList(new Long[catalogItems.size()]);

    final TypeIdAccessor typeIdAccessor = new TypeIdAccessor();

    final int[] ins = db.execute("INSERT INTO item (id, type_id, title) VALUES (?, ?, ?)", (PreparedStatement ps) -> {
      for (int pos = 0, i = 0; i < catalogItems.size(); ++i) {
        final CatalogItem item = catalogItems.get(i);
        if (!isValidId(item.getId())) { // insert scenario
          final Long id = ids.get(pos++);
          result.set(i, id);
          ps.setLong(1, id);
          ps.setLong(2, typeIdAccessor.getTypeId(item.getType()));
          ps.setString(3, item.getType());
          ps.addBatch();
        }
      }

      return ps.executeBatch();
    });

    final int[] upd = db.execute("UPDATE item SET type_id=?, title=? WHERE id=?", (PreparedStatement ps) -> {
      for (int i = 0; i < catalogItems.size(); ++i) {
        final CatalogItem item = catalogItems.get(i);
        if (isValidId(item.getId())) { // update scenario
          final Long id = item.getId();
          result.set(i, id);
          ps.setLong(1, typeIdAccessor.getTypeId(item.getType()));
          ps.setString(2, item.getType());
          ps.setLong(3, id);
          ps.addBatch();

          // TODO: separate statement? - reinsert links on update
          db.update("DELETE item_relation WHERE lhs=?", id);
          db.update("DELETE item_download WHERE item_id=?", id);
        }
      }

      return ps.executeBatch();
    });

    // insert item extension
    final int[] ext = db.execute("UPDATE item SET type_id=?, title=? WHERE id=?", (PreparedStatement ps) -> {
      for (int i = 0; i < catalogItems.size(); ++i) {
        final CatalogItem item = catalogItems.get(i);
        if (isValidId(item.getId())) { // update scenario
          final Long id = item.getId();
          result.set(i, id);
          ps.setLong(1, typeIdAccessor.getTypeId(item.getType()));
          ps.setString(2, item.getType());
          ps.setLong(3, id);
          ps.addBatch();

          // TODO: separate statement? - reinsert links on update
          db.update("DELETE item_relation WHERE lhs=?", id);
          db.update("DELETE item_download WHERE item_id=?", id);
        }
      }

      return ps.executeBatch();
    });

    log.debug("Items inserted={}, updated={}", ins, upd);

    // map IDs
    for (int pos = 0, i = 0; i < catalogItems.size(); ++i) {
      final CatalogItem item = catalogItems.get(i);
      final Long id;
      final Long typeId = typeIdAccessor.getTypeId(item.getType());
      if (isValidId(item.getId())) {
        // update
        id = item.getId();
        db.update("UPDATE item SET type_id=?, title=? WHERE id=?", typeId, item.getTitle(), id);

        // reinsert links on update
        db.update("DELETE item_relation WHERE lhs=?", id);
        db.update("DELETE item_download WHERE item_id=?", id);
      } else {
        // insert
        id = ids.get(pos++);
        db.update("INSERT INTO item (id, type_id, title) VALUES (?, ?, ?)", id, typeId, item.getTitle());
      }

      // insert extension item
      if (item.getExtension() instanceof Book) {
        final Book book = (Book) item.getExtension();
        addRelations(typeIdAccessor, id, book.getGenres(), "genre");
        addRelations(typeIdAccessor, id, book.getLanguages(), "language");
        addRelations(typeIdAccessor, id, book.getAuthors(), "author");
        addDownloadItems(id, book.getDownloadItems());
      } else if (item.getExtension() != null) {
        throw new IllegalStateException("Unrecognized extension=" + item.getExtension());
      }

      result.add(id);
    }

    return result;
  }

  //
  // Private
  //

  @Nonnull
  private CatalogItem withExtension(@Nonnull CatalogItem item) {
    final CatalogItemExtension extension;
    if (item.getType().equals("book")) {
      // TODO: return unchanged item if no author/language/genre assigned
      extension = Book.builder()
          .authors(getRelatedNamed(item.getId(), "author"))
          .languages(getRelatedNamed(item.getId(), "language"))
          .genres(getRelatedNamed(item.getId(), "genre"))
          .build();
    } else {
      extension = null;
    }

    if (extension == null) {
      return item; // unchanged
    }

    return CatalogItem.builder()
        .id(item.getId())
        .title(item.getTitle())
        .type(item.getType())
        .extension(extension)
        // TODO: Download items
        .build();
  }

  @Nonnull
  private List<Named> getRelatedNamed(long id, @Nonnull String typeName) {
    return db.query("SELECT i.id, i.title FROM item AS i\n" +
        "INNER JOIN item_relation AS ir ON i.id=ir.rhs\n" +
        "INNER JOIN entity_type AS et ON et.id=ir.type_id\n" +
        "WHERE ir.lhs=? AND et.name=?\n" +
        "ORDER BY i.id", NAMED_ITEM_ROW_MAPPER, id, typeName);
  }

  private void addDownloadItems(long id, @Nonnull List<DownloadItem> downloadItems) {
    for (final DownloadItem downloadItem : downloadItems) {
      insertDownloadItem(id, downloadItem);
    }
  }

  private void insertDownloadItem(long id, @Nonnull DownloadItem item) {
    db.update("INSERT INTO item_download (item_id, file_size, origin_id, download_id) VALUES (?, ?, ?, ?)", id,
        item.getFileSize(), item.getOrigin().getId(), item.getDownloadId());
  }

  private void addRelations(@Nonnull TypeIdAccessor typeIdAccessor,
                            long itemId,
                            @Nonnull List<Named> toList,
                            @Nonnull String typeName) {
    for (final Named to : toList) {
      final long toId = to.getId();
      addRelation(itemId, toId, typeIdAccessor.getTypeId(typeName));
    }
  }

  private void addRelation(long from, long to, long relationType) {
    db.update("INSERT INTO item_relation (lhs, rhs, type_id) VALUES (?, ?, ?)", from, to, relationType);
  }

  private final RowMapper<CatalogItem> CATALOG_ITEM_ROW_MAPPER = (rs, rowNum) -> CatalogItem.builder()
      .id(rs.getLong("id"))
      .type(rs.getString("type_name"))
      .title(rs.getString("title"))
      .build();

  private final RowMapper<Named> NAMED_ITEM_ROW_MAPPER = (rs, rowNum) ->
      new Named(rs.getLong("id"), rs.getString("title"));

  private final class TypeIdAccessor {
    final Map<String, Long> cachedIds = new HashMap<>();

    Long getTypeId(String typeName) {
      Long result = cachedIds.get(typeName);
      if (result == null) {
        result = db.queryForObject("SELECT id FROM entity_type WHERE name=?", Long.class, typeName);
        cachedIds.put(typeName, result);
      }

      return result;
    }
  }
}
