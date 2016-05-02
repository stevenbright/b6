package b6.model.content;

import lombok.*;

/**
 * Represents certain content, available to the user
 */
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@ToString
@EqualsAndHashCode
public final class Content {
  private final String text;
  private final ContentType type;
}
