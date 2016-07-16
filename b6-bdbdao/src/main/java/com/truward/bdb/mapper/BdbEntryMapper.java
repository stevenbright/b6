package com.truward.bdb.mapper;

import com.sleepycat.je.DatabaseEntry;

import javax.annotation.Nonnull;
import java.io.IOException;

/**
 * An interface for mapping returned byte array, associated with certain ID to
 * an object. Implementations of this interface perform the actual work of mapping each row to a result object.
 *
 * @author Alexander Shabanov
 */
public interface BdbEntryMapper<T> {

  @Nonnull
  T map(@Nonnull DatabaseEntry key, @Nonnull DatabaseEntry value) throws IOException;
}
