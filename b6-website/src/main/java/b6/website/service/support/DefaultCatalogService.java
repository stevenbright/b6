package b6.website.service.support;

import b6.model.catalog.CatalogItem;
import b6.website.model.catalog.SortType;
import b6.website.service.CatalogService;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.util.StringUtils;

import javax.annotation.Nonnull;
import java.util.*;

import static b6.website.model.Ids.isValidId;

/**
 * @author Alexander Shabanov
 */
public class DefaultCatalogService extends AbstractService implements CatalogService {

  private final JdbcOperations db;

  public DefaultCatalogService(JdbcOperations jdbcOperations) {
    this.db = Objects.requireNonNull(jdbcOperations, "jdbcOperations");
  }

  @Nonnull
  public CatalogItem getItem(long id) {
    return db.queryForObject(
        "SELECT i.id, et.name AS type_name, i.title FROM item AS i\n" +
            "INNER JOIN entity_type AS et ON i.type_id=et.id WHERE i.id=?",
        CATALOG_ITEM_ROW_MAPPER, id);
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

    return db.query(qb.toString(), CATALOG_ITEM_ROW_MAPPER, params.toArray(new Object[params.size()]));
  }

  @Nonnull
  @Override
  public List<Long> persistItems(@Nonnull List<CatalogItem> catalogItems) {
    // get next N IDs
    final int newIdCount = (int) catalogItems.stream().filter(i -> !isValidId(i.getId())).count();
    final List<Long> ids = db.queryForList("SELECT seq_item.nextval FROM system_range(1, ?)", Long.class, newIdCount);

    // map IDs
    final List<Long> result = new ArrayList<>(catalogItems.size());
    final TypeIdAccessor typeIdAccessor = new TypeIdAccessor();
    for (int pos = 0, i = 0; i < catalogItems.size(); ++i) {
      final CatalogItem item = catalogItems.get(i);
      final Long id;
      final Long typeId = typeIdAccessor.getTypeId(item.getType());
      if (isValidId(item.getId())) {
        // update
        id = item.getId();
        db.update("UPDATE item SET type_id=?, title=? WHERE id=?", typeId, item.getTitle(), id);
      } else {
        // insert
        id = ids.get(pos++);
        db.update("INSERT INTO item (id, type_id, title) VALUES (?, ?, ?)", id, typeId, item.getTitle());
      }
      result.add(id);
    }

    return result;
  }

  //
  // Private
  //

  private final RowMapper<CatalogItem> CATALOG_ITEM_ROW_MAPPER = (rs, rowNum) -> CatalogItem.builder()
      .id(rs.getLong("id"))
      .type(rs.getString("type_name"))
      .title(rs.getString("title"))
      .build();

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
