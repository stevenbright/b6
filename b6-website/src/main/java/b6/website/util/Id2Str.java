package b6.website.util;

import com.google.protobuf.ByteString;

import javax.annotation.Nonnull;
import javax.xml.bind.DatatypeConverter;

/**
 * Utility class for converting IDs to string and vice versa.
 *
 * @author Alexander Shabanov
 */
public final class Id2Str {
  public static final String PREFIX = "B";

  private Id2Str() {}

  @Nonnull
  public static String fromByteString(ByteString byteString) {
    @SuppressWarnings("StringBufferReplaceableByString")
    final StringBuilder builder = new StringBuilder(PREFIX.length() + byteString.size() * 2);
    builder.append(PREFIX);
    if (!byteString.isEmpty()) {
      // TODO: more optimal way of converting byte string
      builder.append(DatatypeConverter.printHexBinary(byteString.toByteArray()));
    }
    return builder.toString();
  }

  public static ByteString toByteString(@Nonnull String id) {
    if (id.isEmpty()) {
      return ByteString.EMPTY;
    }

    if (!id.startsWith(PREFIX)) {
      throw new IllegalArgumentException("Invalid id=" + id);
    }

    return ByteString.copyFrom(DatatypeConverter.parseHexBinary(id.substring(PREFIX.length())));
  }
}
