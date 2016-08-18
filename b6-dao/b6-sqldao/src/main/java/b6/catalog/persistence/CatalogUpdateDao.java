package b6.catalog.persistence;

import b6.catalog.persistence.model.generated.B6db;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * @author Alexander Shabanov
 */
public interface CatalogUpdateDao {

  @Nonnull
  String persistCatalogItem(@Nonnull B6db.CatalogItem item);

  void removeRelations(@Nonnull String id);

  void saveRelations(@Nonnull List<B6db.Relation> relations);
}
