package com.truward.bdb.map;

import com.sleepycat.je.Database;
import com.sleepycat.je.LockMode;
import com.truward.bdb.mapper.BdbEntryMapper;

import javax.annotation.Nonnull;
import java.util.Objects;

/**
 * @author Alexander Shabanov
 */
public final class MapDaoConfig<T> {
  private final Database database;
  private final BdbEntryMapper<T> mapper;
  private final LockMode lockMode;

  public MapDaoConfig(@Nonnull Database database, @Nonnull BdbEntryMapper<T> mapper, @Nonnull LockMode lockMode) {
    this.database = Objects.requireNonNull(database, "database");
    this.mapper = Objects.requireNonNull(mapper, "mapper");
    this.lockMode = Objects.requireNonNull(lockMode, "lockMode");
  }

  public MapDaoConfig(@Nonnull Database database, @Nonnull BdbEntryMapper<T> mapper) {
    this(database, mapper, LockMode.DEFAULT);
  }

  @Nonnull
  public Database getDatabase() {
    return database;
  }

  @Nonnull
  public BdbEntryMapper<T> getMapper() {
    return mapper;
  }

  @Nonnull
  public LockMode getLockMode() {
    return lockMode;
  }
}
