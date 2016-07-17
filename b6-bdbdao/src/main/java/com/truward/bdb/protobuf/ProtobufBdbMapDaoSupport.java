package com.truward.bdb.protobuf;

import com.google.protobuf.ByteString;
import com.google.protobuf.MessageLite;
import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseEntry;
import com.sleepycat.je.LockMode;
import com.truward.bdb.map.BdbMapDaoSupport;
import com.truward.bdb.map.MapDaoConfig;
import com.truward.bdb.mapper.BdbEntryMapper;

import javax.annotation.Nonnull;
import java.io.IOException;

/**
 * @author Alexander Shabanov
 */
public class ProtobufBdbMapDaoSupport<TValue extends MessageLite> extends BdbMapDaoSupport<ByteString, TValue>
    implements ProtobufKeyValueSerializer<TValue> {
  private final Database database;
  private final BdbEntryMapper<TValue> mapper;
  private final LockMode lockMode;

  public ProtobufBdbMapDaoSupport(@Nonnull MapDaoConfig<TValue> config) {
    this.database = config.getDatabase();
    this.lockMode = config.getLockMode();
    this.mapper = config.getMapper();
  }

  @Nonnull
  @Override
  public Database getDatabase() {
    return database;
  }

  @Nonnull
  @Override
  protected TValue getValue(@Nonnull DatabaseEntry key, @Nonnull DatabaseEntry value) throws IOException {
    return mapper.map(key, value);
  }

  @Nonnull
  @Override
  protected LockMode getDefaultLockMode() {
    return lockMode;
  }
}
