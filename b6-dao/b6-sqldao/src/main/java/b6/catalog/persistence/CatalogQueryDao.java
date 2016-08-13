package b6.catalog.persistence;

import b6.catalog.persistence.model.CatalogItemSortType;
import b6.catalog.persistence.model.generated.B6db;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * @author Alexander Shabanov
 */
public interface CatalogQueryDao {

  @Nonnull
  B6db.CatalogItem getCatalogItemById(@Nonnull String id);

  @Nonnull
  List<B6db.CatalogItem> queryCatalogItems(
      @Nonnull String relatedItemId,
      @Nonnull String startItemId,
      @Nonnull String titleFilter,
      @Nonnull String typeFilter,
      @Nonnull CatalogItemSortType sortType,
      int limit
  );

  @Nonnull
  List<B6db.Relation> getRelations(@Nonnull String to);
}
