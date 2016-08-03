package com.truward.bdb.map;

import com.google.common.collect.ImmutableList;
import com.google.protobuf.ByteString;
import com.sleepycat.je.Cursor;
import com.sleepycat.je.CursorConfig;
import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseEntry;
import com.sleepycat.je.LockMode;
import com.sleepycat.je.OperationStatus;
import com.sleepycat.je.Transaction;
import com.truward.bdb.exception.BdbDaoMappingException;
import com.truward.bdb.exception.BdbDaoStatusException;

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
public abstract class BdbMapDaoSupport<TKey, TValue> implements BdbMapDao<TKey, TValue>,
    BdbKeyValueSerializer<TKey, TValue> {
  
  @Nonnull
  public abstract Database getDatabase();
  
  @Nullable
  @Override
  public TValue get(@Nullable Transaction tx, @Nonnull TKey key, @Nonnull Supplier<TValue> defaultValueSupplier) {
    try {
      final DatabaseEntry keyEntry = getDatabaseEntryFromKey(key);
      final DatabaseEntry valueEntry = new DatabaseEntry();
      final OperationStatus status = getDatabase().get(tx, keyEntry, valueEntry, getDefaultLockMode());
      if (status == OperationStatus.SUCCESS) {
          return getValue(keyEntry, valueEntry);
      }
    } catch (IOException e) {
      throw new BdbDaoMappingException(e);
    }

    return defaultValueSupplier.get();
  }

  @Nonnull
  @Override
  public TValue get(@Nullable Transaction tx, @Nonnull TKey key) {
    final TValue result = get(tx, key, () -> {
      throw new RuntimeException("There is no value corresponding to the given key"); // TODO: another exception
    });

    if (result == null) {
      // should not happen
      throw new BdbDaoMappingException("null returned but never expected");
    }

    return result;
  }

  @Nonnull
  @Override
  public List<TValue> getAsList(@Nullable Transaction tx, @Nonnull TKey key) {
    return query(tx, ((cursor, lockMode) -> {
      final List<TValue> result = new ArrayList<>();

      final DatabaseEntry outKey = new DatabaseEntry();
      final DatabaseEntry outValue = new DatabaseEntry();
      OperationStatus status = cursor.getSearchKey(getDatabaseEntryFromKey(key), outValue, lockMode);
      for (; status == OperationStatus.SUCCESS; status = cursor.getNextDup(outKey, outValue, lockMode)) {
        final TValue value = getValue(outKey, outValue);
        result.add(value);
      }

      return ImmutableList.copyOf(result);
    }));
  }

  @Nonnull
  @Override
  public List<Map.Entry<ByteString, TValue>> getEntries(@Nullable Transaction tx, int offset, int limit) {
    return query(tx, (cursor, lockMode) -> {
      final DatabaseEntry outKey = new DatabaseEntry();
      final DatabaseEntry outValue = new DatabaseEntry();

      if (OperationStatus.SUCCESS != cursor.getFirst(outKey, outValue, lockMode)) {
        return ImmutableList.<Map.Entry<ByteString, TValue>>of();
      }

      final List<Map.Entry<ByteString, TValue>> result = new ArrayList<>();
      int pos = 0;
      do {
        if (pos < offset) {
          continue;
        }

        if ((offset - pos) >= limit) {
          break;
        }

        final ByteString key = ByteString.copyFrom(outKey.getData(), outKey.getOffset(), outKey.getSize());
        final TValue value = getValue(outKey, outValue);
        result.add(new AbstractMap.SimpleImmutableEntry<>(key, value));
        ++pos;
      } while (OperationStatus.SUCCESS == cursor.getNext(outKey, outValue, lockMode));
      return ImmutableList.copyOf(result);
    });
  }

  @Nonnull
  @Override
  public <TMappedValue> TMappedValue query(@Nullable Transaction tx, @Nonnull BdbCursorMapper<TMappedValue> cursorMapper) {
    try (final Cursor cursor = getDatabase().openCursor(tx, getDefaultCursorConfig())) {
      return cursorMapper.doWithCursor(cursor, getCursorLockMode());
    } catch (IOException e) {
      throw new BdbDaoMappingException(e);
    }
  }

  @Override
  public void put(@Nullable Transaction tx, @Nonnull TKey key, @Nonnull TValue value) {
    try {
      final DatabaseEntry keyEntry = getDatabaseEntryFromKey(key);
      final DatabaseEntry valueEntry = getDatabaseEntryFromValue(value);

      final OperationStatus status = getDatabase().put(tx, keyEntry, valueEntry);
      if (status != OperationStatus.SUCCESS) {
        throw new BdbDaoStatusException("put", status);
      }
    } catch (IOException e) {
      throw new BdbDaoMappingException(e);
    }
  }

  @Override
  public void delete(@Nullable Transaction tx, @Nonnull TKey key) {
    final DatabaseEntry keyEntry;
    try {
      keyEntry = getDatabaseEntryFromKey(key);
      getDatabase().delete(tx, keyEntry);
    } catch (IOException e) {
      throw new BdbDaoMappingException(e);
    }
  }

  //
  // Protected
  //

  @Nonnull
  protected abstract TValue getValue(@Nonnull DatabaseEntry key, @Nonnull DatabaseEntry value) throws IOException;


  @Nullable
  protected CursorConfig getDefaultCursorConfig() {
    return null;
  }

  @Nonnull
  protected LockMode getDefaultLockMode() {
    return LockMode.DEFAULT;
  }

  @Nonnull
  protected LockMode getCursorLockMode() {
    final LockMode lockMode = getDefaultLockMode();
    return (lockMode == LockMode.READ_COMMITTED ? LockMode.DEFAULT : lockMode);
  }
}
