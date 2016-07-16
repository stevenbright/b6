package com.truward.bdb;

import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.Environment;
import com.truward.bdb.transaction.BdbTransactionSupport;

import javax.annotation.Nonnull;
import java.util.Objects;

/**
 * @author Alexander Shabanov
 */
public abstract class BdbDatabaseAccessSupport extends BdbTransactionSupport {
  private final DatabaseConfig databaseConfig;

  protected BdbDatabaseAccessSupport(@Nonnull Environment environment, @Nonnull DatabaseConfig databaseConfig) {
    super(environment);
    this.databaseConfig = Objects.requireNonNull(databaseConfig, "databaseConfig");
  }

  @Nonnull
  public DatabaseConfig getDatabaseConfig() {
    return databaseConfig;
  }

  @Nonnull
  public Database openDatabase(@Nonnull String name) {
    return getEnvironment().openDatabase(null, name, getDatabaseConfig());
  }
}
