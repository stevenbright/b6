package com.truward.bdb.transaction;

import com.sleepycat.je.Environment;
import com.sleepycat.je.Transaction;

import javax.annotation.Nonnull;

/**
 * @author Alexander Shabanov
 */
public interface BdbTransactionMixin {

  @Nonnull
  Environment getEnvironment();

  default <T> T withTransaction(@Nonnull BdbTransactionOperationCallback<T> callback) {
    final Transaction tx = getEnvironment().beginTransaction(null, null);
    boolean succeed = false;
    try {
      final T result = callback.call(tx);
      succeed = true;
      return result;
    } finally {
      if (succeed) {
        tx.commit();
      } else {
        tx.abort();
      }
    }
  }

  default void withTransactionVoid(@Nonnull BdbTransactionOperationVoidCallback callback) {
    final Transaction tx = getEnvironment().beginTransaction(null, null);
    boolean succeed = false;
    try {
      callback.call(tx);
      succeed = true;
    } finally {
      if (succeed) {
        tx.commit();
      } else {
        tx.abort();
      }
    }
  }
}
