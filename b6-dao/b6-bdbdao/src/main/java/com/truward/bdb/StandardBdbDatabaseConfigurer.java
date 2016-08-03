package com.truward.bdb;

import com.sleepycat.je.CacheMode;
import com.sleepycat.je.DatabaseConfig;

/**
 * @author Alexander Shabanov
 */
public final class StandardBdbDatabaseConfigurer implements BdbDatabaseConfigurer {

  @Override
  public DatabaseConfig createDefaultConfig() {
    return new DatabaseConfig()
        .setTransactional(true)
        .setAllowCreate(true)
        .setCacheMode(CacheMode.DEFAULT);
  }
}
