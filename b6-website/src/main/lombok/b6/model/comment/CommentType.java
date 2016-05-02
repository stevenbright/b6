package b6.model.comment;

import lombok.*;

/**
 * @author Alexander Shabanov
 */
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@ToString
@EqualsAndHashCode
public final class CommentType {
  private final long id;
  private final String name;
}
