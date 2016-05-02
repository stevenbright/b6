package b6.website.service.support;

import b6.model.catalog.CatalogItem;
import b6.website.service.CatalogService;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.List;

/**
 * @author Alexander Shabanov
 */
public class DefaultCatalogService extends AbstractService implements CatalogService {

  @Nonnull
  @Override
  public List<CatalogItem> getCatalogItems(Long startId, int limit) {
    return Arrays.asList(
        CatalogItem.builder()
            .id(1).title("title")
            .build(),
        CatalogItem.builder()
            .id(2).title("dd")
            .build()
    );
  }
}
