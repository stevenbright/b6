package b6.website.service;

import b6.model.catalog.CatalogItem;
import b6.website.model.catalog.SortType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

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

  private static final List<CatalogItem> EMPTY_ITEMS = Collections.emptyList();

  @Test(expected = EmptyResultDataAccessException.class)
  public void shouldNotGetNonExistingItem() {
    catalogService.getItem(1654651483464836483L);
  }

  @Test
  public void shouldNotGetMissingItems() {
    assertEquals(EMPTY_ITEMS, catalogService.getItems(0L, 0L, "", "", SortType.DEFAULT, 10));
    assertEquals(EMPTY_ITEMS, catalogService.getItems(0L, 0L, "", "", SortType.TITLE_ASCENDING, 10));
    assertEquals(EMPTY_ITEMS, catalogService.getItems(0L, 0L, "", "", SortType.TITLE_DESCENDING, 10));
  }

  @Test
  public void shouldPersistItem() {
    final Long id = catalogService.persistItem(CatalogItem.builder().title("ru").type("language").build());
    assertNotNull("id is null", id);
    assertEquals(CatalogItem.builder().title("ru").type("language").id(id).build(), catalogService.getItem(id));
  }

  @Test
  public void shouldQueryItems() {
    final long[] ids = {
        catalogService.persistItem(CatalogItem.builder().title("ru").type("language").build()),
        catalogService.persistItem(CatalogItem.builder().title("sci-fi").type("genre").build()),
        catalogService.persistItem(CatalogItem.builder().title("en").type("language").build()),
        catalogService.persistItem(CatalogItem.builder().title("novel").type("genre").build()),
    };

    // query using sort types
    assertEquals(Arrays.asList(ids[0], ids[1], ids[2], ids[3]), toIds(catalogService
        .getItems(0L, 0L, "", "", SortType.DEFAULT, 10)));
    assertEquals(Arrays.asList(ids[2], ids[3], ids[0], ids[1]), toIds(catalogService
        .getItems(0L, 0L, "", "", SortType.TITLE_ASCENDING, 10)));
    assertEquals(Arrays.asList(ids[1], ids[0], ids[3], ids[2]), toIds(catalogService
        .getItems(0L, 0L, "", "", SortType.TITLE_DESCENDING, 10)));

    // query using type filtering
    assertEquals(Arrays.asList(ids[0], ids[2]), toIds(catalogService
        .getItems(0L, 0L, "", "language", SortType.DEFAULT, 10)));
    assertEquals(Arrays.asList(ids[1], ids[3]), toIds(catalogService
        .getItems(0L, 0L, "", "genre", SortType.DEFAULT, 10)));
    assertEquals(Arrays.asList(ids[2], ids[0]), toIds(catalogService
        .getItems(0L, 0L, "", "language", SortType.TITLE_ASCENDING, 10)));
  }

  //
  // Private
  //

  private static List<Long> toIds(List<CatalogItem> items) {
    return items.stream().map(CatalogItem::getId).collect(Collectors.toList());
  }
}
