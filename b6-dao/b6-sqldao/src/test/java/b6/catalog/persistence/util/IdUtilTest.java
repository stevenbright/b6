package b6.catalog.persistence.util;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Tests for {@link IdUtil}.
 *
 * @author Alexander Shabanov
 */
public final class IdUtilTest {

  @Test
  public void shouldGetEmptyStringFromLong() {
    assertEquals(0, IdUtil.toLong(""));
  }

  @Test
  public void shouldGetZeroFromEmptyString() {
    assertEquals("", IdUtil.fromLong(0));
  }

  @Test(expected = IllegalArgumentException.class)
  public void shouldRejectNegativeId() {
    IdUtil.fromLong(-1);
  }

  @Test
  public void shouldConvertToStringAndBackToLong() {
    // Given:
    final long num = 123L;

    // When:
    final String str = IdUtil.fromLong(num);
    final long retNum = IdUtil.toLong(str);

    // Then:
    assertEquals(num, retNum);
  }
}
