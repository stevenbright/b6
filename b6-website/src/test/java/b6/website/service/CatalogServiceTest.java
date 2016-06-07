package b6.website.service;

import b6.website.model.catalog.SortType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

import static org.junit.Assert.assertTrue;

/**
 * Tests for {@link CatalogService}.
 *
 * @author Alexander Shabanov
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/spring/CatalogServiceTest-context.xml")
@Transactional
public final class CatalogServiceTest {

  @Resource CatalogService catalogService;

  @Test(expected = EmptyResultDataAccessException.class)
  public void shouldNotGetNonExistingItem() {
    catalogService.getItem(1654651483464836483L);
    assertTrue(true); // TODO: m
  }

  @Test
  public void shouldNotGetMissingItems() {
    assertTrue(catalogService.getItems(0L, 0L, "", "", SortType.DEFAULT, 10).isEmpty());
    assertTrue(catalogService.getItems(0L, 0L, "", "", SortType.TITLE_ASCENDING, 10).isEmpty());
    assertTrue(catalogService.getItems(0L, 0L, "", "", SortType.TITLE_DESCENDING, 10).isEmpty());
  }
}
