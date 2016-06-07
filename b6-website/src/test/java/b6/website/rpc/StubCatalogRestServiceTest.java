package b6.website.rpc;

import b6.rpc.model.catalog.Catalog;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author Alexander Shabanov
 */
public final class StubCatalogRestServiceTest {
  private CatalogRestService catalogService;

  @Before
  public void init() {
    catalogService = new StubCatalogRestService();
  }

  @Test
  public void shouldGetItem() {
    final Catalog.GetItemReply reply = catalogService.getItem(Catalog.GetItemRequest.newBuilder()
        .setId("A1001").build());
    assertTrue(reply.hasItem());
  }

  @Test
  public void shouldGetItems() {
    final Catalog.GetItemsReply reply = catalogService.getItems(Catalog.GetItemsRequest.newBuilder().build());
    assertTrue(reply.getItemsCount() > 0);
  }

  @Test
  public void shouldGetBooks() {
    final Catalog.GetItemsReply reply = catalogService.getItems(Catalog.GetItemsRequest.newBuilder()
        .setTypeFilter("book")
        .build());
    assertTrue(reply.getItemsCount() > 0);
  }

  @Test
  public void shouldSetFavorite() {
    // Given:
    final String itemId = "A1001";
    // Check fav status - should be false
    final Catalog.GetItemReply firstItemReply = catalogService.getItem(Catalog.GetItemRequest.newBuilder()
        .setId(itemId).build());
    assertTrue(firstItemReply.hasItem());
    assertFalse(firstItemReply.getItem().getIsFavorite());
    assertEquals(0, catalogService.getFavoriteItems(Catalog.GetFavoriteItemsRequest.newBuilder().build())
        .getItemsCount());

    // When:
    final Catalog.SetFavoriteReply setFavoriteReply = catalogService.setFavorite(Catalog.SetFavoriteRequest
        .newBuilder().setItemId("A1001").setIsFavorite(true).build());

    // Then:
    assertTrue(setFavoriteReply.getIsFavorite());

    final Catalog.GetItemReply secondItemReply = catalogService.getItem(Catalog.GetItemRequest.newBuilder()
        .setId("A1001").build());
    assertTrue(secondItemReply.hasItem());
    assertTrue(secondItemReply.getItem().getIsFavorite());

    assertEquals(1, catalogService.getFavoriteItems(Catalog.GetFavoriteItemsRequest.newBuilder().build())
        .getItemsCount());
  }
}
