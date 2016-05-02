package b6.model.comment;

import b6.model.content.Content;
import lombok.*;

/**
 * @author Alexander Shabanov
 */
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@ToString
@EqualsAndHashCode
public final class Comment {
  private final Long id;
  private final long sourceId;
  private final long sourceTypeId;
  private final long authorId;
  private final Content content;
}
