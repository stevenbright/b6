package b6.model.catalog;

import lombok.*;

/**
 * @author Alexander Shabanov
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@ToString
@EqualsAndHashCode
public class Named {

  private final long id;
  private final String title;
}
