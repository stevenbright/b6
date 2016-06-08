package b6.website.rpc;

import b6.model.catalog.CatalogItem;
import b6.rpc.model.catalog.Catalog;
import b6.website.service.CatalogService;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Alexander Shabanov
 */
public final class DefaultCatalogRestServiceTest {

  private CatalogRestService catalogRpcService;
  private CatalogService catalogServiceMock;

  @Before
  public void init() {
    catalogServiceMock = mock(CatalogService.class);
    catalogRpcService = new DefaultCatalogRestService(catalogServiceMock);
  }

  @Ignore
  @Test
  public void shouldGetItems() {
    // Given:
    final long id = 1;
    when(catalogServiceMock.getItem(id)).thenReturn(CatalogItem.builder().build());

    // When:
    final Catalog.GetItemsReply reply = catalogRpcService.getItems(Catalog.GetItemsRequest.newBuilder().build());

    // Then:
    assertTrue(reply.getItemsCount() > 0);
  }
}
