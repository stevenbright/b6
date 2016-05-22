package b6.website.rpc;

import b6.catalog.model.Catalog;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * @author Alexander Shabanov
 */
public final class StubCatalogRestServiceTest {
  private final CatalogRestService catalogService = new StubCatalogRestService();

  @Test
  public void shouldGetItems() {
    final Catalog.GetItemsReply reply = catalogService.getItems(Catalog.GetItemsRequest.newBuilder().build());
    assertTrue(reply.getItemsCount() > 0);
  }
}
