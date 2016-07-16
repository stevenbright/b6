package com.truward.bdb.map;

import com.google.common.collect.ImmutableList;
import com.google.protobuf.ByteString;
import com.sleepycat.je.*;
import com.truward.bdb.mapper.BdbEntryMapper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * @author Alexander Shabanov
 */
public abstract class BdbMapDaoSupport<T> implements BdbMapDao<T> {
  private final Database database;
  private final BdbEntryMapper<T> mapper;
  private final LockMode lockMode;

  public BdbMapDaoSupport(@Nonnull MapDaoConfig<T> config) {
    this.database = config.getDatabase();
    this.mapper = config.getMapper();
    this.lockMode = config.getLockMode();
  }

  @Nonnull
  @Override
  public Database getDatabase() {
    return database;
  }

  @Nullable
  @Override
  public T get(@Nullable Transaction tx, @Nonnull ByteString key, @Nonnull Supplier<T> defaultValueSupplier) {
    final DatabaseEntry keyEntry = new DatabaseEntry(key.toByteArray());
    final DatabaseEntry valueEntry = new DatabaseEntry();
    final OperationStatus status = database.get(tx, keyEntry, valueEntry, lockMode);
    if (status == OperationStatus.SUCCESS) {
      try {
        return mapper.map(keyEntry, valueEntry);
      } catch (IOException e) {
        throw new IllegalStateException("Mapping error", e); // TODO: another exception
      }
    }

    return defaultValueSupplier.get();
  }

  @Nonnull
  @Override
  public T get(@Nullable Transaction tx, @Nonnull ByteString key) {
    final T result = get(tx, key, () -> {
      throw new RuntimeException("There is no value corresponding to the given key"); // TODO: another exception
    });

    if (result == null) {
      // should not happen
      throw new IllegalStateException("Contract violation: null returned but never expected");
    }

    return result;
  }

  @Nonnull
  @Override
  public List<T> getAsList(@Nullable Transaction tx, @Nonnull ByteString key) {
    return query(tx, ((cursor, lockMode) -> {
      final List<T> result = new ArrayList<>();

      final DatabaseEntry outKey = new DatabaseEntry();
      final DatabaseEntry outValue = new DatabaseEntry();
      OperationStatus status = cursor.getSearchKey(new DatabaseEntry(key.toByteArray()), outValue, lockMode);
      for (; status == OperationStatus.SUCCESS; status = cursor.getNextDup(outKey, outValue, lockMode)) {
        final T value = mapper.map(outKey, outValue);
        result.add(value);
      }

      return ImmutableList.copyOf(result);
    }));
  }

  @Nonnull
  @Override
  public List<Map.Entry<ByteString, T>> getEntries(@Nullable Transaction tx, int offset, int limit) {
    return query(tx, (cursor, lockMode) -> {
      final DatabaseEntry outKey = new DatabaseEntry();
      final DatabaseEntry outValue = new DatabaseEntry();

      if (OperationStatus.SUCCESS != cursor.getFirst(outKey, outValue, lockMode)) {
        return ImmutableList.<Map.Entry<ByteString, T>>of();
      }

      final List<Map.Entry<ByteString, T>> result = new ArrayList<>();
      int pos = 0;
      do {
        if (pos < offset) {
          continue;
        }

        if ((offset - pos) >= limit) {
          break;
        }

        final ByteString key = ByteString.copyFrom(outKey.getData(), outKey.getOffset(), outKey.getSize());
        final T value = mapper.map(outKey, outValue);
        result.add(new AbstractMap.SimpleImmutableEntry<>(key, value));
        ++pos;
      } while (OperationStatus.SUCCESS == cursor.getNext(outKey, outValue, lockMode));
      return ImmutableList.copyOf(result);
    });
  }

  @Nonnull
  @Override
  public <V> V query(@Nullable Transaction tx, @Nonnull BdbCursorMapper<V> cursorMapper) {
    try (final Cursor cursor = database.openCursor(tx, getDefaultCursorConfig())) {
      return cursorMapper.doWithCursor(cursor, getCursorLockMode());
    } catch (IOException e) {
      throw new IllegalStateException("Mapping error", e); // TODO: another exception if value has not been properly mapped
    }
  }

  @Override
  public void put(@Nullable Transaction tx, @Nonnull ByteString key, @Nonnull T value) {
    try {
      final DatabaseEntry keyEntry = new DatabaseEntry(key.toByteArray());
      final DatabaseEntry valueEntry = toDatabaseEntry(value);

      final OperationStatus status = database.put(tx, keyEntry, valueEntry);
      if (status != OperationStatus.SUCCESS) {
        throw new IllegalStateException("Unexpected put result=" + status);
      }
    } catch (IOException e) {
      throw new IllegalStateException("Mapping error", e); // TODO: another exception if value has not been properly mapped
    }
  }

  @Override
  public void delete(@Nullable Transaction tx, @Nonnull ByteString key) {
    final DatabaseEntry keyEntry = new DatabaseEntry(key.toByteArray());
    getDatabase().delete(tx, keyEntry);
  }

  //
  // Protected
  //

  @Nonnull
  protected abstract DatabaseEntry toDatabaseEntry(@Nonnull T value) throws IOException;

  @Nullable
  protected CursorConfig getDefaultCursorConfig() {
    return null;
  }

  @Nonnull
  protected LockMode getCursorLockMode() {
    final LockMode lockMode = this.lockMode;
    return (lockMode == LockMode.READ_COMMITTED ? LockMode.DEFAULT : lockMode);
  }
}
