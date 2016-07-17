package com.truward.bdb.exception;

/**
 * Base class for BDB DAO exceptions.
 *
 * @author Alexander Shabanov
 */
public abstract class BdbDaoException extends RuntimeException {
  public BdbDaoException() {
  }

  public BdbDaoException(String message) {
    super(message);
  }

  public BdbDaoException(String message, Throwable cause) {
    super(message, cause);
  }

  public BdbDaoException(Throwable cause) {
    super(cause);
  }

  public BdbDaoException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
