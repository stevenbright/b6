package b6.website.rpc;

import b6.rpc.model.catalog.Catalog;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * @author Alexander Shabanov
 */
public class DefaultCatalogRestServiceTest {

  private final CatalogRestService catalogService = new DefaultCatalogRestService();

  @Ignore
  @Test
  public void shouldGetItems() {
    final Catalog.GetItemsReply reply = catalogService.getItems(Catalog.GetItemsRequest.newBuilder().build());
    assertTrue(reply.getItemsCount() > 0);
  }
}
