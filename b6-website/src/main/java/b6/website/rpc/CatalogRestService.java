package b6.website.rpc;

import b6.catalog.model.Catalog;

/**
 * Exposed REST service for catalog.
 *
 * @author Alexander Shabanov
 */
public interface CatalogRestService {

  Catalog.GetItemReply getItem(Catalog.GetItemRequest request);

  Catalog.GetItemsReply getItems(Catalog.GetItemsRequest request);
}
