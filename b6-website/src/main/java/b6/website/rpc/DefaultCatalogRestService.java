package b6.website.rpc;

import b6.catalog.model.Catalog;

/**
 * @author Alexander Shabanov
 */
public final class DefaultCatalogRestService implements CatalogRestService {

  @Override
  public Catalog.GetCatalogItemReply getCatalogItem(Catalog.GetCatalogItemRequest request) {
    final Catalog.GetCatalogItemReply.Builder replyBuilder = Catalog.GetCatalogItemReply.newBuilder();
    final String id = request.getId();
    if ("A12".equals(id)) {
      replyBuilder.setItem(Catalog.CatalogItem.newBuilder()
          .setId(id)
          .setTitle("name")
          .setType("book")
          .build());
    }

    return replyBuilder.build();
  }
}
