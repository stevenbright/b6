package b6.website.util;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Tests for {@link Id2Str}.
 *
 * @author Alexander Shabanov
 */
public final class Id2StrTest {

  @Test
  public void shouldGetEmptyStringFromLong() {
    assertEquals(0, Id2Str.toLong(""));
  }

  @Test
  public void shouldGetZeroFromEmptyString() {
    assertEquals("", Id2Str.fromLong(0));
  }

  @Test(expected = IllegalArgumentException.class)
  public void shouldRejectNegativeId() {
    Id2Str.fromLong(-1);
  }

  @Test
  public void shouldConvertToStringAndBackToLong() {
    // Given:
    final long num = 123L;

    // When:
    final String str = Id2Str.fromLong(num);
    final long retNum = Id2Str.toLong(str);

    // Then:
    assertEquals(num, retNum);
  }
}
