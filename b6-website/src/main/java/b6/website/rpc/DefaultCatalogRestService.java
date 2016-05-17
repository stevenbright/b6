package b6.website.rpc;

import b6.catalog.model.Catalog;

/**
 * @author Alexander Shabanov
 */
public final class DefaultCatalogRestService implements CatalogRestService {

  @Override
  public Catalog.GetItemReply getItem(Catalog.GetItemRequest request) {
    final Catalog.GetItemReply.Builder replyBuilder = Catalog.GetItemReply.newBuilder();
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
