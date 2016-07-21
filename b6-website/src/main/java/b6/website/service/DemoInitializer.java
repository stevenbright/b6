package b6.website.service;

import b6.persistence.CatalogDao;
import b6.persistence.model.generated.B6DB;
import com.google.protobuf.ByteString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.PostConstruct;
import java.util.Arrays;

/**
 * @author Alexander Shabanov
 */
public class DemoInitializer {
  private final Logger log = LoggerFactory.getLogger(getClass());

  private final CatalogDao catalogDao;

  public DemoInitializer(@Nonnull CatalogDao catalogDao) {
    this.catalogDao = catalogDao;
  }

  @PostConstruct
  public void init() {
    log.info("Initializing demo data...");

    final ByteString gEn = insertNamed(CatalogDao.LANGUAGE_TYPE, "en");
    final ByteString gRu = insertNamed(CatalogDao.LANGUAGE_TYPE, "ru");

    final ByteString gSciFi = insertNamed(CatalogDao.GENRE_TYPE, "scifi");
    final ByteString gNovel = insertNamed(CatalogDao.GENRE_TYPE, "novel");
    final ByteString gShort = insertNamed(CatalogDao.GENRE_TYPE, "short");

    insertNamed(CatalogDao.PERSON_TYPE, "Jack London");
    insertNamed(CatalogDao.PERSON_TYPE, "Edgar Poe");
    insertNamed(CatalogDao.PERSON_TYPE, "Stephen King");
    insertNamed(CatalogDao.PERSON_TYPE, "Joe Hill");
    insertNamed(CatalogDao.PERSON_TYPE, "Arkady Strugatsky");
    insertNamed(CatalogDao.PERSON_TYPE, "Boris Strugatsky");
    insertNamed(CatalogDao.PERSON_TYPE, "Victor Pelevin");
    insertNamed(CatalogDao.PERSON_TYPE, "Jason Ciaramella");

    insertNamed(CatalogDao.ORIGIN_TYPE, "librus");
    insertNamed(CatalogDao.ORIGIN_TYPE, "wiki");

    catalogDao.insert(null, named(CatalogDao.BOOK_TYPE, "Hermit and Sixfinger")
        .setBook(B6DB.BookExtension.newBuilder()
            .addDownloadItems(B6DB.DownloadItem.newBuilder()
                .setDownloadId("123532")
                .setFileSize(12234)
                .setDescriptorText("FB2")
                .build())
            .build())
        .build());

    log.debug("Inserted ids={}", Arrays.asList(gEn, gRu, gSciFi, gNovel, gShort));
  }

  private static B6DB.CatalogItemExtension.Builder named(String type, String name) {
    return B6DB.CatalogItemExtension.newBuilder()
        .setItem(B6DB.CatalogItem.newBuilder().setType(type).setTitle(name).build());
  }

  private ByteString insertNamed(String type, String name) {
    return catalogDao.insert(null, named(type, name).build());
  }
}
