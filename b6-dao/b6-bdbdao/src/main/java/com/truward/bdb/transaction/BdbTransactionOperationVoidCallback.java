package com.truward.bdb.transaction;

import com.sleepycat.je.Transaction;

import javax.annotation.Nonnull;

/**
 * @author Alexander Shabanov
 */
public interface BdbTransactionOperationVoidCallback {
  void call(@Nonnull Transaction tx);
}
