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
public interface BdbMapDao<T> {

  @Nonnull
  Database getDatabase();

  @Nullable
  T get(@Nullable Transaction tx, @Nonnull ByteString key, @Nonnull Supplier<T> defaultValueSupplier);

  @Nonnull
  T get(@Nullable Transaction tx, @Nonnull ByteString key);

  @Nonnull
  List<T> getAsList(@Nullable Transaction tx, @Nonnull ByteString key);

  @Nonnull
  List<Map.Entry<ByteString, T>> getEntries(@Nullable Transaction tx, int offset, int limit);

  void put(@Nullable Transaction tx, @Nonnull ByteString key, @Nonnull T value);

  void delete(@Nullable Transaction tx, @Nonnull ByteString key);

  @Nonnull
  <V> V query(@Nullable Transaction tx, @Nonnull BdbCursorMapper<V> cursorMapper);
}
