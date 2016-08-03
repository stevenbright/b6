package b6.persistence;

import b6.persistence.model.CatalogItemSortType;
import b6.persistence.model.generated.B6db;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * @author Alexander Shabanov
 */
public interface CatalogDao {
  String BOOK_TYPE          = "book";
  String LANGUAGE_TYPE      = "language";
  String GENRE_TYPE         = "genre";
  String PERSON_TYPE        = "person";
  String ORIGIN_TYPE        = "origin";

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

  //
  // Updates (might use separate data source)
  //

  @Nonnull
  String persistCatalogItem(@Nonnull B6db.CatalogItem item);
}
