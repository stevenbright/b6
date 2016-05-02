package b6.model.catalog;

import lombok.*;

/**
 * @author Alexander Shabanov
 */
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@ToString
@EqualsAndHashCode
public final class CatalogItem {
  private final long id;
  private final String title;
}
