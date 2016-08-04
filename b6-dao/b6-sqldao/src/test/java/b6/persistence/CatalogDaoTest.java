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

import java.util.Arrays;
import java.util.Collections;

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

  //
  // Private
  //

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
