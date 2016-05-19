package b6.model.catalog;

import lombok.*;

import java.util.List;

/**
 * @author Alexander Shabanov
 */
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@ToString
@EqualsAndHashCode
public final class Book implements CatalogItemExtension {
  private final List<Named> authors; // TODO: persons?
  private final List<Named> genres;
}
