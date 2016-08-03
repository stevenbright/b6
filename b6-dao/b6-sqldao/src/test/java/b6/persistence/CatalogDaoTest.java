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
    final B6db.CatalogItem catItem = B6db.CatalogItem.newBuilder()
        .setItem(B6db.Item.newBuilder().setTitle("en").setType(CatalogDao.LANGUAGE_TYPE))
        .build();
    final String id = dao.persistCatalogItem(catItem);
    final B6db.CatalogItem actualItem = dao.getCatalogItemById(id);
    assertEquals(B6db.CatalogItem.newBuilder(catItem).setId(id).build(), actualItem);

    // update-and-get
    final B6db.CatalogItem newCatItem = B6db.CatalogItem.newBuilder()
        .setId(id)
        .setItem(B6db.Item.newBuilder().setTitle("fantasy").setType(CatalogDao.GENRE_TYPE))
        .build();
    assertEquals(id, dao.persistCatalogItem(newCatItem));
    assertEquals(newCatItem, dao.getCatalogItemById(id));
  }
}
