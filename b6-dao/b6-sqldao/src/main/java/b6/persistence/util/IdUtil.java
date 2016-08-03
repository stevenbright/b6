package b6.persistence.util;

import javax.annotation.Nonnull;

/**
 * Utility class for converting IDs to string and vice versa.
 *
 * @author Alexander Shabanov
 */
public final class IdUtil {
  public static final String PREFIX = "k";

  private IdUtil() {}

  @Nonnull
  public static String fromLong(long id) {
    if (id < 0) {
      throw new IllegalArgumentException("id<0");
    }

    if (id == 0) {
      return "";
    }

    @SuppressWarnings("StringBufferReplaceableByString")
    final StringBuilder builder = new StringBuilder(20);
    builder.append(PREFIX).append(Long.toHexString(id));
    return builder.toString();
  }

  public static long toLong(@Nonnull String id) {
    if (id.isEmpty()) {
      return 0;
    }

    if (!id.startsWith(PREFIX)) {
      throw new IllegalArgumentException("Invalid id=" + id);
    }

    return Long.parseLong(id.substring(PREFIX.length()), 16);
  }
}
