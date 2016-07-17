package com.truward.bdb.map;

import com.google.protobuf.ByteString;
import com.sleepycat.je.Database;
import com.sleepycat.je.Transaction;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * @author Alexander Shabanov
 */
public interface BdbMapDao<TKey, TValue> {

  @Nonnull
  Database getDatabase();

  @Nullable
  TValue get(@Nullable Transaction tx, @Nonnull TKey key, @Nonnull Supplier<TValue> defaultValueSupplier);

  @Nonnull
  TValue get(@Nullable Transaction tx, @Nonnull TKey key);

  @Nonnull
  List<TValue> getAsList(@Nullable Transaction tx, @Nonnull TKey key);

  @Nonnull
  List<Map.Entry<ByteString, TValue>> getEntries(@Nullable Transaction tx, int offset, int limit);

  void put(@Nullable Transaction tx, @Nonnull TKey key, @Nonnull TValue value);

  void delete(@Nullable Transaction tx, @Nonnull TKey key);

  @Nonnull
  <TMappedValue> TMappedValue query(@Nullable Transaction tx, @Nonnull BdbCursorMapper<TMappedValue> cursorMapper);
}
