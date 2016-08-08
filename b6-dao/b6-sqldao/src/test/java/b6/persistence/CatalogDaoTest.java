package b6.persistence;

import b6.persistence.model.CatalogItemSortType;
import b6.persistence.model.generated.B6db;
import b6.persistence.util.IdUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalField;
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
  @Resource
  CatalogDao dao;

  @Test
  public void shouldQueryNoItems() {
    assertEquals(Collections.emptyList(), dao.queryCatalogItems("", IdUtil.fromLong(89137465876123L),
        "", "", CatalogItemSortType.DEFAULT, 10));
  }

  @Test(expected = EmptyResultDataAccessException.class)
  public void shouldNotGetNonExistentItem() {
    dao.getCatalogItemById(IdUtil.fromLong(92384572873506987L));
  }

  @Test
  public void shouldSaveAndGetItemById() {
    // insert-and-get
    final B6db.CatalogItem catItem = saveItem("en", CatalogDao.LANGUAGE_TYPE);
    final String id = catItem.getId();
    final B6db.CatalogItem actualItem = dao.getCatalogItemById(id);
    assertEquals(catItem, actualItem);

    // update-and-get
    final B6db.CatalogItem newCatItem = B6db.CatalogItem.newBuilder()
        .setId(id)
        .setItem(B6db.Item.newBuilder().setTitle("fantasy").setType(CatalogDao.GENRE_TYPE))
        .build();
    assertEquals(id, dao.persistCatalogItem(newCatItem));
    assertEquals(newCatItem, dao.getCatalogItemById(id));
  }

  @Test
  public void shouldSaveRelations() {
    assertEquals(Collections.emptyList(), dao.getRelations(IdUtil.fromLong(254652315L)));

    final B6db.CatalogItem o1 = saveItem("fb2-134", CatalogDao.ORIGIN_TYPE);
    final B6db.CatalogItem p1 = saveItem("John London", CatalogDao.PERSON_TYPE);
    final B6db.CatalogItem p2 = saveItem("Alex Grin", CatalogDao.PERSON_TYPE);

    dao.saveRelations(Arrays.asList(
        relation(o1.getId(), p1.getId(), CatalogDao.ORIGIN_TYPE),
        relation(o1.getId(), p2.getId(), CatalogDao.ORIGIN_TYPE)
    ));

    assertEquals(Collections.singletonList(relation(o1.getId(), p1.getId(), CatalogDao.ORIGIN_TYPE)),
        dao.getRelations(p1.getId()));

    dao.removeRelations(p1.getId());
    assertEquals(Collections.emptyList(), dao.getRelations(p1.getId()));
  }

  @Test
  public void shouldQueryUsers() {
    final DemoItems items = new DemoItems();

    final List<B6db.CatalogItem> catItems = dao.queryCatalogItems("", "", "", "", CatalogItemSortType.DEFAULT, 10);

    assertEquals(10, catItems.size());
  }

  //
  // Private
  //

  class DemoItems {
    final B6db.CatalogItem ciEn = saveItem("en", CatalogDao.LANGUAGE_TYPE);
    final B6db.CatalogItem ciRu = saveItem("ru", CatalogDao.LANGUAGE_TYPE);

    final B6db.CatalogItem ciJack = saveItem("Jack London", CatalogDao.PERSON_TYPE);
    final B6db.CatalogItem ciEdgar = saveItem("Edgar Poe", CatalogDao.PERSON_TYPE);
    final B6db.CatalogItem ciStephen = saveItem("Stephen King", CatalogDao.PERSON_TYPE);
    final B6db.CatalogItem ciJoe = saveItem("Joe Hill", CatalogDao.PERSON_TYPE);
    final B6db.CatalogItem ciArkady = saveItem("Arkady Strugatsky", CatalogDao.PERSON_TYPE);
    final B6db.CatalogItem ciBoris = saveItem("Boris Strugatsky", CatalogDao.PERSON_TYPE);
    final B6db.CatalogItem ciVictor = saveItem("Victor Pelevin", CatalogDao.PERSON_TYPE);
    final B6db.CatalogItem ciJason = saveItem("Jason Ciaramella", CatalogDao.PERSON_TYPE);

    final B6db.CatalogItem ciSciFi = saveItem("sci_fi", CatalogDao.GENRE_TYPE);
    final B6db.CatalogItem ciFantasy = saveItem("fantasy", CatalogDao.GENRE_TYPE);
    final B6db.CatalogItem ciEssay = saveItem("essay", CatalogDao.GENRE_TYPE);
    final B6db.CatalogItem ciNovel = saveItem("novel", CatalogDao.GENRE_TYPE);
    final B6db.CatalogItem ciComics = saveItem("comics", CatalogDao.GENRE_TYPE);
    final B6db.CatalogItem ciWestern = saveItem("western", CatalogDao.GENRE_TYPE);
    final B6db.CatalogItem ciHorror = saveItem("horror", CatalogDao.GENRE_TYPE);

    final B6db.CatalogItem ciEnClassic = saveItem("EnglishClassicBooks", CatalogDao.ORIGIN_TYPE);
    final B6db.CatalogItem ciEnModern = saveItem("EnglishModernBooks", CatalogDao.ORIGIN_TYPE);
    final B6db.CatalogItem ciEnMisc = saveItem("EnglishMisc", CatalogDao.ORIGIN_TYPE);
    final B6db.CatalogItem ciRuBooks = saveItem("RussianBooks", CatalogDao.ORIGIN_TYPE);

    final B6db.CatalogItem ciSerNoon = saveItem("Noon: 22nd Century", CatalogDao.SERIES_TYPE);
    final B6db.CatalogItem ciSerTowr = saveItem("The Dark Tower", CatalogDao.SERIES_TYPE);

    final String farRainbow;

    public DemoItems() {
      farRainbow = dao.persistCatalogItem(B6db.CatalogItem.newBuilder()
          .setItem(B6db.Item.newBuilder().setTitle("Far Rainbow").setType(CatalogDao.BOOK_TYPE))
          .setExtensions(B6db.Extensions.newBuilder().setBook(B6db.BookExtension.newBuilder()
              .setSeriesId(ciSerNoon.getId())
              .setSeriesPos(3)
              .addDownloadItems(B6db.DownloadItem.newBuilder()
                  .setDateAdded(getEpochSecondFromDate(2007, 10, 23))
                  .setOriginName(ciRuBooks.getItem().getTitle())
                  .setDownloadId("100-77091"))))
          .build());
      dao.saveRelations(Arrays.asList(
          relation(ciArkady.getId(), farRainbow, CatalogDao.AUTHOR_TYPE),
          relation(ciBoris.getId(), farRainbow, CatalogDao.AUTHOR_TYPE),
          relation(ciSciFi.getId(), farRainbow, CatalogDao.GENRE_TYPE),
          relation(ciNovel.getId(), farRainbow, CatalogDao.GENRE_TYPE),
          relation(ciEn.getId(), farRainbow, CatalogDao.LANGUAGE_TYPE)
      ));
    }
  }

  private static long getEpochSecondFromDate(int year, int month, int day) {
    return LocalDate.of(year, month, day).atStartOfDay().toInstant(ZoneOffset.UTC).getEpochSecond();
  }

  private B6db.CatalogItem saveItem(String title, String type) {
    final B6db.CatalogItem.Builder itemBuilder = B6db.CatalogItem.newBuilder()
        .setItem(B6db.Item.newBuilder().setTitle(title).setType(type));
    final String id = dao.persistCatalogItem(itemBuilder.build());
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
