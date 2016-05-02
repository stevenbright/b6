package b6.model.content;

import lombok.*;

/**
 * An overview to the catalog entity.
 */
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@ToString
@EqualsAndHashCode
public final class Overview {
  private final long id;
  private final Content content;
}
