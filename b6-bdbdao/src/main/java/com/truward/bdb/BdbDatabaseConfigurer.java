package com.truward.bdb;

import com.sleepycat.je.DatabaseConfig;

/**
 * Provides common configuration for BDB databases
 *
 * @author Alexander Shabanov
 */
public interface BdbDatabaseConfigurer {
  DatabaseConfig createDefaultConfig();
}
