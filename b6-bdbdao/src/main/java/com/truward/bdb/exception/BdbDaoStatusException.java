package com.truward.bdb.exception;

import com.sleepycat.je.OperationStatus;

/**
 * @author Alexander Shabanov
 */
public class BdbDaoStatusException extends BdbDaoException {

  public BdbDaoStatusException(String operation, OperationStatus status) {
    super("Error while executing " + operation + ", status=" + status);
  }
}
