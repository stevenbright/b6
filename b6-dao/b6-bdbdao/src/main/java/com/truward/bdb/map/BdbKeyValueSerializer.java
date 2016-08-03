package com.truward.bdb.map;

import com.sleepycat.je.DatabaseEntry;

import javax.annotation.Nonnull;
import java.io.IOException;

/**
 * Represents an interface, that encapsulates transformation logic for keys and values.
 *
 * @author Alexander Shabanov
 */
public interface BdbKeyValueSerializer<TKey, TValue> {

  @Nonnull
  DatabaseEntry getDatabaseEntryFromKey(@Nonnull TKey value) throws IOException;

  @Nonnull
  DatabaseEntry getDatabaseEntryFromValue(@Nonnull TValue value) throws IOException;
}
