package com.truward.bdb.protobuf;

import com.google.protobuf.MessageLite;
import com.google.protobuf.Parser;
import com.sleepycat.je.DatabaseEntry;
import com.truward.bdb.mapper.BdbEntryMapper;

import javax.annotation.Nonnull;
import java.io.IOException;

/**
 * @author Alexander Shabanov
 */
public final class ProtobufMappers {

  public static <T extends MessageLite> BdbEntryMapper<T> of(@Nonnull T defaultInstance) {
    return new ProtobufEntryMapper<>(defaultInstance);
  }

  private static final class ProtobufEntryMapper<T extends MessageLite> implements BdbEntryMapper<T> {
    private final Parser<? extends MessageLite> parser;

    public ProtobufEntryMapper(T defaultInstance) {
      this.parser = defaultInstance.getParserForType();
    }

    @SuppressWarnings("unchecked")
    @Nonnull
    @Override
    public T map(@Nonnull DatabaseEntry key, @Nonnull DatabaseEntry value) throws IOException {
      final MessageLite message = parser.parseFrom(value.getData(), value.getOffset(), value.getSize());
      return (T) message;
    }
  }

  private ProtobufMappers() {} // hidden
}
