package b6.website.util;

import javax.annotation.Nonnull;

/**
 * Utility class for converting IDs to string and vice versa.
 *
 * @author Alexander Shabanov
 */
public final class Id2Str {
  public static final String PREFIX = "A";

  private Id2Str() {}

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
    builder.append(PREFIX).append(id);
    return builder.toString();
  }

  public static long toLong(@Nonnull String id) {
    if (id.isEmpty()) {
      return 0;
    }

    if (!id.startsWith(PREFIX)) {
      throw new IllegalArgumentException("Invalid id=" + id);
    }

    return Long.parseLong(id.substring(PREFIX.length()));
  }
}
