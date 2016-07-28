package b6.persistence.support;

import b6.persistence.CatalogDao;
import b6.persistence.model.SortType;
import b6.persistence.model.generated.B6DB;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.protobuf.ByteString;
import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseEntry;
import com.sleepycat.je.Environment;
import com.sleepycat.je.OperationStatus;
import com.sleepycat.je.Transaction;
import com.truward.bdb.BdbDatabaseConfigurer;
import com.truward.bdb.map.BdbMapDao;
import com.truward.bdb.map.BdbMapDaoSupport;
import com.truward.bdb.map.MapDaoConfig;
import com.truward.bdb.mapper.BdbEntryMapper;
import com.truward.bdb.protobuf.ProtobufBdbMapDaoSupport;
import com.truward.bdb.protobuf.ProtobufKeyValueSerializer;
import com.truward.bdb.protobuf.ProtobufMappers;
import com.truward.bdb.protobuf.key.KeyUtil;

import javax.annotation.Nonnull;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Alexander Shabanov
 */
public final class DefaultCatalogDao implements CatalogDao, Closeable {

  public static final String CATALOG_ITEM_DATABASE_NAME = "CatalogItem";
  public static final String RELATION_DATABASE_NAME = "Relation";

  private static final BdbEntryMapper<B6DB.Relation> RELATION_MAPPER = ProtobufMappers.of(B6DB.Relation.getDefaultInstance());

  private final CatalogItemExtensionDao itemDao;
  private final BdbMapDao<ByteString, B6DB.Relation> relationDao;
  private final Database catalogDb;
  private final Database relDb;

  public DefaultCatalogDao(@Nonnull Environment environment, @Nonnull BdbDatabaseConfigurer dbConfigurer) {
    Objects.requireNonNull(environment, "environment");

    this.catalogDb = environment.openDatabase(null, CATALOG_ITEM_DATABASE_NAME, dbConfigurer.createDefaultConfig()
        .setSortedDuplicates(false));
    this.itemDao = new CatalogItemExtensionDao(this.catalogDb);

    this.relDb = environment.openDatabase(null, RELATION_DATABASE_NAME,
        dbConfigurer.createDefaultConfig().setSortedDuplicates(false));
    relationDao = new ProtobufBdbMapDaoSupport<>(new MapDaoConfig<>(relDb, RELATION_MAPPER));
  }

  @Override
  public void close() throws IOException {
    catalogDb.close();
    relDb.close();
  }

  @Override
  public ByteString insert(Transaction tx, B6DB.CatalogItemExtension item) {
    final ByteString key = KeyUtil.randomKey();
    itemDao.put(tx, key, item);
    return key;
  }

  @Override
  public void update(Transaction tx, ByteString id, B6DB.CatalogItemExtension item) {
    itemDao.put(tx, id, item);
  }

  @Override
  public B6DB.CatalogItemResult getById(Transaction tx, ByteString id) {
    final B6DB.CatalogItemExtension item = itemDao.get(null, id);
    return getCatalogItemResult(id, item);
  }

  @Nonnull
  @Override
  public List<B6DB.CatalogItemResult> getCatalogItems(Transaction tx,
                                                         @Nonnull ByteString relatedItemId,
                                                         @Nonnull ByteString startItemId,
                                                         @Nonnull String titleFilter,
                                                         @Nonnull String typeFilter,
                                                         @Nonnull SortType sortType,
                                                         int limit) {
    final Set<ByteString> toRelations;
    if (!relatedItemId.isEmpty()) {
      toRelations = getRelationsFrom(tx, relatedItemId, 0, Integer.MAX_VALUE).stream().map(B6DB.Relation::getToId)
          .collect(Collectors.toSet());
    } else {
      toRelations = ImmutableSet.of();
    }

    // brute force query
    final List<B6DB.CatalogItemResult> items = itemDao.query(tx, (cur, lm) -> {
      final DatabaseEntry k = new DatabaseEntry();
      final DatabaseEntry v = new DatabaseEntry();
      final List<B6DB.CatalogItemResult> result = new ArrayList<>();

      for (OperationStatus s = cur.getFirst(k, v, lm); s == OperationStatus.SUCCESS; s = cur.getNext(k, v, lm)) {
        final B6DB.CatalogItemExtension itemExtension = getCatalogItem(v);
        final B6DB.CatalogItem item = itemExtension.getItem();
        if (!titleFilter.isEmpty() && !item.getTitle().startsWith(titleFilter)) {
          continue;
        }

        if (!typeFilter.isEmpty() && !item.getType().equals(typeFilter)) {
          continue;
        }

        if (!relatedItemId.isEmpty() &&
            !toRelations.contains(ByteString.copyFrom(k.getData(), k.getOffset(), k.getSize()))) {
          continue;
        }

        result.add(getCatalogItemResult(ByteString.copyFrom(k.getData(), k.getOffset(), k.getSize()), itemExtension));
      }

      return result;
    });

    Optional<B6DB.CatalogItemResult> startItem = startItemId.isEmpty() ? Optional.empty() :
        Optional.of(getById(tx, startItemId));
    Stream<B6DB.CatalogItemResult> itemStream = items.stream();
    switch (sortType) {
      case TITLE_ASCENDING:
        itemStream = itemStream.sorted((l, r) -> {
          int result = l.getItem().getTitle().compareTo(r.getItem().getTitle());
          if (result == 0) {
            // compare by IDs to introduce unique ordering
            result = KeyUtil.compare(l.getId(), r.getId());
          }
          return result;
        });

        if (startItem.isPresent()) {
          // skip first items
          itemStream = itemStream.filter(i -> {
            final int titleComparison = i.getItem().getTitle().compareTo(startItem.get().getItem().getTitle());
            return !((titleComparison < 0) || ((titleComparison == 0) &&
                (KeyUtil.compare(i.getId(), startItem.get().getId()) <= 0)));
          });
        }
        break;

      case TITLE_DESCENDING:
        itemStream = itemStream.sorted((l, r) -> {
          int result = r.getItem().getTitle().compareTo(l.getItem().getTitle());
          if (result == 0) {
            // compare by IDs to introduce unique ordering
            result = KeyUtil.compare(r.getId(), l.getId());
          }
          return result;
        });

        if (startItem.isPresent()) {
          // skip first items
          itemStream = itemStream.filter(i -> {
            final int titleComparison = i.getItem().getTitle().compareTo(startItem.get().getItem().getTitle());
            return !((titleComparison > 0) || ((titleComparison == 0) &&
                (KeyUtil.compare(i.getId(), startItem.get().getId()) >= 0)));
          });
        }
        break;

      case DEFAULT:
        // skip first items
        if (startItem.isPresent()) {
          itemStream = itemStream.filter(i -> KeyUtil.compare(i.getId(), startItem.get().getId()) > 0);
        }
        break;

      default:
        throw new UnsupportedOperationException("Unsupported sortType=" + sortType);
    }

    return itemStream.limit(limit).collect(Collectors.toList());
  }

  @Override
  public void insertRelation(Transaction tx, B6DB.Relation relation) {
    // TODO: check relation existence
    final boolean exists = relationDao.query(tx, (cur, lm) -> {
      final DatabaseEntry k = new DatabaseEntry();
      final DatabaseEntry v = new DatabaseEntry();
      for (OperationStatus s = cur.getFirst(k, v, lm); s == OperationStatus.SUCCESS; s = cur.getNext(k, v, lm)) {
        final B6DB.Relation r = RELATION_MAPPER.map(k, v);
        if (r.equals(relation)) {
          return true;
        }
      }
      return false;
    });

    if (exists) {
      return;
    }

    // insert
    final ByteString key = KeyUtil.randomKey();
    relationDao.put(null, key, relation);
  }

  @Override
  public List<B6DB.Relation> getRelationsFrom(Transaction tx, ByteString fromId, int offset, int limit) {
    return queryRelations(tx, offset, limit, (r) -> r.getFromId().equals(fromId));
  }

  @Override
  public List<B6DB.Relation> getRelationsTo(Transaction tx, ByteString toId, int offset, int limit) {
    return queryRelations(tx, offset, limit, (r) -> r.getToId().equals(toId));
  }

  //
  // Private
  //

  private static B6DB.CatalogItemResult getCatalogItemResult(ByteString id, B6DB.CatalogItemExtension item) {
    final B6DB.CatalogItemResult.Builder b = B6DB.CatalogItemResult.newBuilder().setId(id).setItem(item.getItem());
    if (item.hasBook()) {
      b.setBook(item.getBook());
    }

    return b.build();
  }

  private List<B6DB.Relation> queryRelations(Transaction tx, int offset, int limit, RelationFilter filter) {
    return relationDao.query(tx, (cur, lm) -> {
      final DatabaseEntry k = new DatabaseEntry();
      final DatabaseEntry v = new DatabaseEntry();
      final List<B6DB.Relation> result = new ArrayList<>();
      int pos = 0;
      for (OperationStatus s = cur.getFirst(k, v, lm);
           s == OperationStatus.SUCCESS && result.size() < limit;
           s = cur.getNext(k, v, lm)) {
        final B6DB.Relation relation = RELATION_MAPPER.map(k, v);
        if (filter.matches(relation)) {
          if (pos >= offset) {
            result.add(relation);
          }
          ++pos;
        }
      }

      return ImmutableList.copyOf(result);
    });
  }

  private interface RelationFilter {
    boolean matches(B6DB.Relation relation);
  }

  private static B6DB.CatalogItemExtension getCatalogItem(@Nonnull DatabaseEntry v) throws IOException {
    try (final ByteArrayInputStream is = new ByteArrayInputStream(v.getData(), v.getOffset(), v.getSize())) {
      final B6DB.CatalogItemExtension.Builder rb = B6DB.CatalogItemExtension.newBuilder()
          .setItem(B6DB.CatalogItem.parseDelimitedFrom(is));
      if (BOOK_TYPE.equals(rb.getItem().getType())) {
        rb.setBook(B6DB.BookExtension.parseDelimitedFrom(is));
      }
      return rb.build();
    }
  }

  private static final class CatalogItemExtensionDao extends BdbMapDaoSupport<ByteString, B6DB.CatalogItemExtension>
      implements ProtobufKeyValueSerializer<B6DB.CatalogItemExtension> {
    private final Database database;

    CatalogItemExtensionDao(@Nonnull Database database) {
      this.database = database;
    }

    @Nonnull
    @Override
    public Database getDatabase() {
      return database;
    }

    @Nonnull
    @Override
    protected B6DB.CatalogItemExtension getValue(@Nonnull DatabaseEntry key, @Nonnull DatabaseEntry v) throws IOException {
      return getCatalogItem(v);
    }

    @Nonnull
    @Override
    public DatabaseEntry getDatabaseEntryFromValue(@Nonnull B6DB.CatalogItemExtension value) throws IOException {
      int estimatedOutputSize = 10 + value.getItem().getSerializedSize();
      if (BOOK_TYPE.equals(value.getItem().getType())) {
        estimatedOutputSize += value.getBook().getSerializedSize();
      }

      final byte[] buffer;
      try (final ByteArrayOutputStream os = new ByteArrayOutputStream(estimatedOutputSize)) {
        value.getItem().writeDelimitedTo(os);
        if (BOOK_TYPE.equals(value.getItem().getType())) {
          value.getBook().writeDelimitedTo(os);
        }

        buffer = os.toByteArray();
      }

      return new DatabaseEntry(buffer);
    }
  }
}
