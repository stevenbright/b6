package b6.website.util;

/**
 * Utility class for converting IDs to string and vice versa.
 *
 * @author Alexander Shabanov
 */
public final class Id2Str {
  public static final String PREFIX = "A";

  private Id2Str() {}

  public static String fromLong(long id) {
    @SuppressWarnings("StringBufferReplaceableByString")
    final StringBuilder builder = new StringBuilder(20);
    builder.append(PREFIX).append(id);
    return builder.toString();
  }

  public static long toLong(String id) {
    if (!id.startsWith(PREFIX)) {
      throw new IllegalArgumentException("Invalid id=" + id);
    }

    return Long.parseLong(id.substring(PREFIX.length()));
  }
}
