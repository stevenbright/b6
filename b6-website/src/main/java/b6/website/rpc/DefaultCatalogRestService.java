package b6.website.rpc;

import b6.rpc.model.catalog.Catalog;

/**
 * @author Alexander Shabanov
 */
public final class DefaultCatalogRestService implements CatalogRestService {

  @Override
  public Catalog.GetItemReply getItem(Catalog.GetItemRequest request) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Catalog.GetItemsReply getItems(Catalog.GetItemsRequest request) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Catalog.SetFavoriteReply setFavorite(Catalog.SetFavoriteRequest request) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Catalog.GetFavoriteItemsReply getFavoriteItems(Catalog.GetFavoriteItemsRequest request) {
    throw new UnsupportedOperationException();
  }
}
