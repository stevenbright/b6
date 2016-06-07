package b6.website.model;

/**
 * Utility class for working with IDs.
 *
 * @author Alexander Shabanov
 */
public final class Ids {
  private Ids() {}

  public static final long INVALID_ID = 0L;

  public static boolean isValidId(long id) {
    return id > 0L;
  }
}
