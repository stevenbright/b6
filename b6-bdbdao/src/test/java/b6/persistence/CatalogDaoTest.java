package b6.persistence;

import b6.persistence.model.generated.B6DB;
import b6.persistence.support.DefaultCatalogDao;
import b6.persistence.testSupport.BdbEnvironmentTestSupport;
import com.google.protobuf.ByteString;
import com.sleepycat.je.Environment;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author Alexander Shabanov
 */
public final class CatalogDaoTest extends BdbEnvironmentTestSupport {

  private CatalogDao catalogDao;

  @Before
  public void initCatalogDao() {
    final Environment env = openTestEnvironment();
    catalogDao = new DefaultCatalogDao(env, dbConfig());
  }

  @Test
  public void shouldInsertAndLookupCatalogElement() {
    // Given:
    final B6DB.CatalogItemExtension item = B6DB.CatalogItemExtension.newBuilder()
        .setItem(B6DB.CatalogItem.newBuilder()
            .setTitle("en")
            .setType(CatalogDao.LANGUAGE_TYPE)
            .build())
        .build();

    // When:
    final ByteString id = catalogDao.insert(null, item);

    // Then:
    final B6DB.CatalogItemExtension actual = catalogDao.getById(null, id);
    assertEquals(item, actual);
  }
}
