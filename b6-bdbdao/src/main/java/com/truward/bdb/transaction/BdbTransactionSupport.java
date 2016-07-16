package com.truward.bdb.transaction;

import com.sleepycat.je.Environment;
import com.sleepycat.je.Transaction;

import javax.annotation.Nonnull;
import java.util.Objects;

/**
 * @author Alexander Shabanov
 */
public abstract class BdbTransactionSupport {
  private final Environment environment;

  protected BdbTransactionSupport(@Nonnull Environment environment) {
    this.environment = Objects.requireNonNull(environment, "environment");
  }

  @Nonnull
  public final Environment getEnvironment() {
    return environment;
  }

  public <T> T withTransaction(@Nonnull BdbTransactionOperationCallback<T> callback) {
    final Transaction tx = environment.beginTransaction(null, null);
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

  public void withTransactionVoid(@Nonnull BdbTransactionOperationVoidCallback callback) {
    final Transaction tx = environment.beginTransaction(null, null);
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
