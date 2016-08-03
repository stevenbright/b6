package com.truward.bdb.transaction;

import com.sleepycat.je.Environment;

import javax.annotation.Nonnull;
import java.util.Objects;

/**
 * @author Alexander Shabanov
 */
public abstract class BdbTransactionSupport implements BdbTransactionMixin {
  private final Environment environment;

  protected BdbTransactionSupport(@Nonnull Environment environment) {
    this.environment = Objects.requireNonNull(environment, "environment");
  }

  @Nonnull
  public final Environment getEnvironment() {
    return environment;
  }
}
