package com.truward.bdb;

import com.google.protobuf.MessageLite;
import com.sleepycat.je.DatabaseEntry;
import com.truward.bdb.map.BdbMapDaoSupport;
import com.truward.bdb.map.MapDaoConfig;

import javax.annotation.Nonnull;

/**
 * @author Alexander Shabanov
 */
public class ProtobufBdbMapDaoSupport<T extends MessageLite> extends BdbMapDaoSupport<T> {

  public ProtobufBdbMapDaoSupport(@Nonnull MapDaoConfig<T> config) {
    super(config);
  }

  @Nonnull
  @Override
  protected final DatabaseEntry toDatabaseEntry(@Nonnull T value) {
    return new DatabaseEntry(value.toByteArray());
  }
}
