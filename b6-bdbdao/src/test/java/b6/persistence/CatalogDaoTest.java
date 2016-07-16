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

  private final B6DB.CatalogItemExtension itemLang = B6DB.CatalogItemExtension.newBuilder()
      .setItem(B6DB.CatalogItem.newBuilder()
          .setTitle("en")
          .setType(CatalogDao.LANGUAGE_TYPE)
          .build())
      .build();

  @Before
  public void initCatalogDao() {
    final Environment env = openTestEnvironment();
    catalogDao = new DefaultCatalogDao(env, dbConfig());
  }

  @Test
  public void shouldInsertAndLookupCatalogElement() {
    // Given:
    final B6DB.CatalogItemExtension item = itemLang;

    // When:
    final ByteString id = catalogDao.insert(null, item);

    // Then:
    final B6DB.CatalogItemExtension actual = catalogDao.getById(null, id);
    assertEquals(item, actual);
  }

  @Test
  public void shouldInsertAndUpdateToBook() {
    // Given:
    final B6DB.CatalogItemExtension item1 = itemLang;
    final B6DB.CatalogItemExtension item2 = B6DB.CatalogItemExtension.newBuilder()
        .setItem(B6DB.CatalogItem.newBuilder()
            .setTitle("The Catcher")
            .setType(CatalogDao.BOOK_TYPE)
            .build())
        .setBook(B6DB.BookExtension.newBuilder()
            .addDownloadItems(B6DB.DownloadItem.newBuilder()
                .setFileSize(1000)
                .setDescriptorText("Text")
                .setDownloadId("adsad"))
            .build())
        .build();

    // When:
    final ByteString id = catalogDao.insert(null, item1);
    catalogDao.update(null, id, item2);

    // Then:
    final B6DB.CatalogItemExtension actual = catalogDao.getById(null, id);
    assertEquals(item2, actual);
  }
}
