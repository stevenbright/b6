package b6.website.rpc;

import b6.catalog.model.Catalog;

/**
 * @author Alexander Shabanov
 */
public final class DefaultCatalogRestService implements CatalogRestService {

  @Override
  public Catalog.GetCatalogItemReply getCatalogItem(Catalog.GetCatalogItemRequest request) {
    final Catalog.GetCatalogItemReply.Builder replyBuilder = Catalog.GetCatalogItemReply.newBuilder();
    final long id = request.getId();
    if (id == 12) {
      replyBuilder.setItem(Catalog.CatalogItem.newBuilder()
          .setId(12)
          .setName("name")
          .setType("book")
          .build());
    }

    return replyBuilder.build();
  }
}
