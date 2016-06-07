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
public final class DownloadItem {
  private final int fileSize;
  private final Named origin;
  private final String downloadId;
}
