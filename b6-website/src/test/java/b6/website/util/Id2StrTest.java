package b6.website.util;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author Alexander Shabanov
 */
public final class Id2StrTest {

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
