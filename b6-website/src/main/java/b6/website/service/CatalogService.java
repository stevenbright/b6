package b6.website.service;

import b6.model.catalog.CatalogItem;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * @author Alexander Shabanov
 */
public interface CatalogService {

  @Nonnull
  List<CatalogItem> getCatalogItems(Long startId, int limit);
}
