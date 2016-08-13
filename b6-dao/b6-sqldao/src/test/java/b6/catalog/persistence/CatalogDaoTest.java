package b6.catalog.persistence;

import b6.catalog.persistence.model.generated.B6db;
import b6.catalog.persistence.model.CatalogItemSortType;
import b6.catalog.persistence.model.generated.B6db;
import b6.catalog.persistence.util.IdUtil;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * @author Alexander Shabanov
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/spring/CatalogDaoTest-context.xml")
@Transactional
public class CatalogDaoTest {
  @Resource CatalogQueryDao queryDao;

  @Resource CatalogUpdateDao updateDao;

  @Test
  public void shouldQueryNoItems() {
    assertEquals(Collections.emptyList(), queryDao.queryCatalogItems("", IdUtil.fromLong(89137465876123L),
        "", "", CatalogItemSortType.DEFAULT, 10));
  }

  @Test(expected = EmptyResultDataAccessException.class)
  public void shouldNotGetNonExistentItem() {
    queryDao.getCatalogItemById(IdUtil.fromLong(92384572873506987L));
  }

  @Test
  public void shouldSaveAndGetItemById() {
    // insert-and-get
    final B6db.CatalogItem catItem = saveItem("en", CatalogTypes.LANGUAGE_TYPE);
    final String id = catItem.getId();
    final B6db.CatalogItem actualItem = queryDao.getCatalogItemById(id);
    assertEquals(catItem, actualItem);

    // update-and-get
    final B6db.CatalogItem newCatItem = B6db.CatalogItem.newBuilder()
        .setId(id)
        .setItem(B6db.Item.newBuilder().setTitle("fantasy").setType(CatalogTypes.GENRE_TYPE))
        .build();
    assertEquals(id, updateDao.persistCatalogItem(newCatItem));
    Assert.assertEquals(newCatItem, queryDao.getCatalogItemById(id));
  }

  @Test
  public void shouldSaveRelations() {
    assertEquals(Collections.emptyList(), queryDao.getRelations(IdUtil.fromLong(254652315L)));

    final B6db.CatalogItem o1 = saveItem("fb2-134", CatalogTypes.ORIGIN_TYPE);
    final B6db.CatalogItem p1 = saveItem("John London", CatalogTypes.PERSON_TYPE);
    final B6db.CatalogItem p2 = saveItem("Alex Grin", CatalogTypes.PERSON_TYPE);

    updateDao.saveRelations(Arrays.asList(
        relation(o1.getId(), p1.getId(), CatalogTypes.ORIGIN_TYPE),
        relation(o1.getId(), p2.getId(), CatalogTypes.ORIGIN_TYPE)
    ));

    assertEquals(Collections.singletonList(relation(o1.getId(), p1.getId(), CatalogTypes.ORIGIN_TYPE)),
        queryDao.getRelations(p1.getId()));

    updateDao.removeRelations(p1.getId());
    assertEquals(Collections.emptyList(), queryDao.getRelations(p1.getId()));
  }

  @Test
  public void shouldQueryUsers() {
    final DemoItems items = new DemoItems();

    List<B6db.CatalogItem> catItems = queryDao.queryCatalogItems("", "", "", "", CatalogItemSortType.DEFAULT, 10);

    assertEquals(10, catItems.size());

    catItems = queryDao.queryCatalogItems("", "", "", CatalogTypes.GENRE_TYPE, CatalogItemSortType.TITLE_ASCENDING, 10);
    assertEquals(7, catItems.size());
  }

  //
  // Private
  //

  class DemoItems {
    final B6db.CatalogItem ciEn = saveItem("en", CatalogTypes.LANGUAGE_TYPE);
    final B6db.CatalogItem ciRu = saveItem("ru", CatalogTypes.LANGUAGE_TYPE);

    final B6db.CatalogItem ciJack = saveItem("Jack London", CatalogTypes.PERSON_TYPE);
    final B6db.CatalogItem ciEdgar = saveItem("Edgar Poe", CatalogTypes.PERSON_TYPE);
    final B6db.CatalogItem ciStephen = saveItem("Stephen King", CatalogTypes.PERSON_TYPE);
    final B6db.CatalogItem ciJoe = saveItem("Joe Hill", CatalogTypes.PERSON_TYPE);
    final B6db.CatalogItem ciArkady = saveItem("Arkady Strugatsky", CatalogTypes.PERSON_TYPE);
    final B6db.CatalogItem ciBoris = saveItem("Boris Strugatsky", CatalogTypes.PERSON_TYPE);
    final B6db.CatalogItem ciVictor = saveItem("Victor Pelevin", CatalogTypes.PERSON_TYPE);
    final B6db.CatalogItem ciJason = saveItem("Jason Ciaramella", CatalogTypes.PERSON_TYPE);

    final B6db.CatalogItem ciSciFi = saveItem("sci_fi", CatalogTypes.GENRE_TYPE);
    final B6db.CatalogItem ciFantasy = saveItem("fantasy", CatalogTypes.GENRE_TYPE);
    final B6db.CatalogItem ciEssay = saveItem("essay", CatalogTypes.GENRE_TYPE);
    final B6db.CatalogItem ciNovel = saveItem("novel", CatalogTypes.GENRE_TYPE);
    final B6db.CatalogItem ciComics = saveItem("comics", CatalogTypes.GENRE_TYPE);
    final B6db.CatalogItem ciWestern = saveItem("western", CatalogTypes.GENRE_TYPE);
    final B6db.CatalogItem ciHorror = saveItem("horror", CatalogTypes.GENRE_TYPE);

    final B6db.CatalogItem ciEnClassic = saveItem("EnglishClassicBooks", CatalogTypes.ORIGIN_TYPE);
    final B6db.CatalogItem ciEnModern = saveItem("EnglishModernBooks", CatalogTypes.ORIGIN_TYPE);
    final B6db.CatalogItem ciEnMisc = saveItem("EnglishMisc", CatalogTypes.ORIGIN_TYPE);
    final B6db.CatalogItem ciRuBooks = saveItem("RussianBooks", CatalogTypes.ORIGIN_TYPE);

    final B6db.CatalogItem ciSerNoon = saveItem("Noon: 22nd Century", CatalogTypes.SERIES_TYPE);
    final B6db.CatalogItem ciSerTowr = saveItem("The Dark Tower", CatalogTypes.SERIES_TYPE);

    final String farRainbow;

    public DemoItems() {
      farRainbow = updateDao.persistCatalogItem(B6db.CatalogItem.newBuilder()
          .setItem(B6db.Item.newBuilder().setTitle("Far Rainbow").setType(CatalogTypes.BOOK_TYPE))
          .setExtensions(B6db.Extensions.newBuilder().setBook(B6db.BookExtension.newBuilder()
              .setSeriesId(ciSerNoon.getId())
              .setSeriesPos(3)
              .addDownloadItems(B6db.DownloadItem.newBuilder()
                  .setDateAdded(getEpochSecondFromDate(2007, 10, 23))
                  .setOriginName(ciRuBooks.getItem().getTitle())
                  .setDownloadId("100-77091"))))
          .build());
      updateDao.saveRelations(Arrays.asList(
          relation(ciArkady.getId(), farRainbow, CatalogTypes.AUTHOR_TYPE),
          relation(ciBoris.getId(), farRainbow, CatalogTypes.AUTHOR_TYPE),
          relation(ciSciFi.getId(), farRainbow, CatalogTypes.GENRE_TYPE),
          relation(ciNovel.getId(), farRainbow, CatalogTypes.GENRE_TYPE),
          relation(ciEn.getId(), farRainbow, CatalogTypes.LANGUAGE_TYPE)
      ));
    }
  }

  private static long getEpochSecondFromDate(int year, int month, int day) {
    return LocalDate.of(year, month, day).atStartOfDay().toInstant(ZoneOffset.UTC).getEpochSecond();
  }

  private B6db.CatalogItem saveItem(String title, String type) {
    final B6db.CatalogItem.Builder itemBuilder = B6db.CatalogItem.newBuilder()
        .setItem(B6db.Item.newBuilder().setTitle(title).setType(type));
    final String id = updateDao.persistCatalogItem(itemBuilder.build());
    return itemBuilder.setId(id).build();
  }

  private B6db.Relation relation(String fromId, String toId, String type) {
    return B6db.Relation.newBuilder()
        .setFromId(fromId)
        .setToId(toId)
        .setType(type)
        .build();
  }
}
