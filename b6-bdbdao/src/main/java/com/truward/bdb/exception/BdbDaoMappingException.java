package com.truward.bdb.exception;

/**
 * An exception, thrown if a value has no
 *
 * @author Alexander Shabanov
 */
public class BdbDaoMappingException extends BdbDaoException {
  public BdbDaoMappingException() {
  }

  public BdbDaoMappingException(String message) {
    super(message);
  }

  public BdbDaoMappingException(String message, Throwable cause) {
    super(message, cause);
  }

  public BdbDaoMappingException(Throwable cause) {
    super(cause);
  }

  public BdbDaoMappingException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
