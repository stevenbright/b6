package com.truward.bdb.protobuf;

import com.google.protobuf.ByteString;
import com.google.protobuf.MessageLite;
import com.sleepycat.je.DatabaseEntry;
import com.truward.bdb.map.BdbKeyValueSerializer;

import javax.annotation.Nonnull;
import java.io.IOException;

/**
 * @author Alexander Shabanov
 */
public interface ProtobufKeyValueSerializer<TValue extends MessageLite> extends BdbKeyValueSerializer<ByteString, TValue> {

  @Nonnull
  default DatabaseEntry getDatabaseEntryFromKey(@Nonnull ByteString key) throws IOException {
    return new DatabaseEntry(key.toByteArray());
  }

  @Nonnull
  default DatabaseEntry getDatabaseEntryFromValue(@Nonnull TValue value) throws IOException {
    return new DatabaseEntry(value.toByteArray());
  }
}
