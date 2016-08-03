package com.truward.bdb.map;

import com.sleepycat.je.Cursor;
import com.sleepycat.je.LockMode;

import javax.annotation.Nonnull;
import java.io.IOException;

/**
 * @author Alexander Shabanov
 */
public interface BdbCursorMapper<T> {

  @Nonnull
  T doWithCursor(@Nonnull Cursor cursor, @Nonnull LockMode lockMode) throws IOException;
}
