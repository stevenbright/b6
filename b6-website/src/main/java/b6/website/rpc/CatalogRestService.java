package b6.website.rpc;

import b6.catalog.model.Catalog;

/**
 * Exposed REST service for catalog.
 *
 * @author Alexander Shabanov
 */
public interface CatalogRestService {

  Catalog.GetCatalogItemReply getCatalogItem(Catalog.GetCatalogItemRequest request);
}
