package b6.website.util;

import com.google.protobuf.ByteString;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Tests for {@link Id2Str}.
 *
 * @author Alexander Shabanov
 */
public final class Id2StrTest {

  @Test
  public void shouldGetEmptyByteString() {
    assertEquals(ByteString.EMPTY, Id2Str.toByteString(""));
  }

  @Test
  public void shouldGetEmptyString() {
    assertEquals("", Id2Str.fromByteString(ByteString.EMPTY));
  }

  @Test(expected = IllegalArgumentException.class)
  public void shouldRejectInvalidInput() {
    Id2Str.toByteString("XWGAEWS");
  }

  @Test
  public void shouldConvertToStringAndBackToLong() {
    // Given:
    final ByteString num = ByteString.copyFrom(new byte[] { 1, 2, 3, 4 });

    // When:
    final String str = Id2Str.fromByteString(num);
    final ByteString retNum = Id2Str.toByteString(str);

    // Then:
    assertEquals(num, retNum);
  }
}
