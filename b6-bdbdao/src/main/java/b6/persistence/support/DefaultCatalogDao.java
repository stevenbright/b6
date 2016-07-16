package b6.persistence.support;

import b6.persistence.CatalogDao;
import b6.persistence.model.generated.B6DB;
import com.google.protobuf.ByteString;
import com.sleepycat.je.*;
import com.truward.bdb.key.KeyUtil;
import com.truward.bdb.map.BdbMapDaoSupport;
import com.truward.bdb.map.MapDaoConfig;
import com.truward.bdb.mapper.BdbEntryMapper;

import javax.annotation.Nonnull;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @author Alexander Shabanov
 */
public class DefaultCatalogDao implements CatalogDao {
  public static final String CATALOG_ITEM_DATABASE_NAME = "CatalogItem";

  private final CatalogItemExtensionDao dao;

  public DefaultCatalogDao(@Nonnull Environment environment, @Nonnull DatabaseConfig databaseConfig) {
    dao = new CatalogItemExtensionDao(environment.openDatabase(null, CATALOG_ITEM_DATABASE_NAME, databaseConfig));
  }

  @Override
  public ByteString insert(Transaction tx, B6DB.CatalogItemExtension item) {
    final ByteString key = KeyUtil.randomKey();
    dao.put(tx, key, item);
    return key;
  }

  @Override
  public void update(Transaction tx, ByteString id, B6DB.CatalogItemExtension item) {
    dao.put(tx, id, item);
  }

  @Override
  public B6DB.CatalogItemExtension getById(Transaction tx, ByteString id) {
    return dao.get(null, id);
  }

  //
  // Private
  //

  private static final class CatalogItemExtensionDao extends BdbMapDaoSupport<B6DB.CatalogItemExtension> {

    public CatalogItemExtensionDao(@Nonnull Database database) {
      super(new MapDaoConfig<>(database, new BdbEntryMapper<B6DB.CatalogItemExtension>() {
        @Nonnull
        @Override
        public B6DB.CatalogItemExtension map(@Nonnull DatabaseEntry key, @Nonnull DatabaseEntry v) throws IOException {
          try (final ByteArrayInputStream is = new ByteArrayInputStream(v.getData(), v.getOffset(), v.getSize())) {
            final B6DB.CatalogItemExtension.Builder rb = B6DB.CatalogItemExtension.newBuilder()
                .setItem(B6DB.CatalogItem.parseDelimitedFrom(is));
            if (BOOK_TYPE.equals(rb.getItem().getType())) {
              rb.setBook(B6DB.BookExtension.parseDelimitedFrom(is));
            }
            return rb.build();
          }
        }
      }));
    }

    @Nonnull
    @Override
    protected DatabaseEntry toDatabaseEntry(@Nonnull B6DB.CatalogItemExtension value) throws IOException {
      int estimatedOutputSize = 10 + value.getItem().getSerializedSize();
      if (BOOK_TYPE.equals(value.getItem().getType())) {
        estimatedOutputSize += value.getBook().getSerializedSize();
      }

      final byte[] buffer;
      try (final ByteArrayOutputStream os = new ByteArrayOutputStream(estimatedOutputSize)) {
        value.getItem().writeDelimitedTo(os);
        if (value.getBook() != null) {
          value.getBook().writeDelimitedTo(os);
        }

        buffer = os.toByteArray();
      }

      return new DatabaseEntry(buffer);
    }
  }
}
