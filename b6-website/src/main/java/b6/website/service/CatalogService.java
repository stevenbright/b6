package b6.website.service;

import b6.model.catalog.CatalogItem;
import b6.website.model.catalog.SortType;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;

/**
 * @author Alexander Shabanov
 */
public interface CatalogService {

  @Nonnull
  CatalogItem getItem(long id);

  @Nonnull
  List<CatalogItem> getItems(long relatedItemId,
                             long startItemId,
                             @Nonnull String titleFilter,
                             @Nonnull String typeFilter,
                             @Nonnull SortType sortType,
                             int limit);

  @Nonnull
  List<Long> persistItems(@Nonnull List<CatalogItem> catalogItems);

  @Nonnull
  default Long persistItem(@Nonnull CatalogItem catalogItem) {
    return persistItems(Collections.singletonList(catalogItem)).get(0);
  }
}
