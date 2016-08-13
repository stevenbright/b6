package com.truward.bdb;

import b6.catalog.persistence.model.generated.B6DB;
import com.google.protobuf.ByteString;
import com.google.protobuf.StringValue;
import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.DatabaseEntry;
import com.truward.bdb.map.BdbMapDao;
import com.truward.bdb.map.MapDaoConfig;
import com.truward.bdb.mapper.BdbEntryMapper;
import com.truward.bdb.protobuf.ProtobufBdbMapDaoSupport;
import com.truward.bdb.protobuf.key.KeyUtil;
import com.truward.bdb.testSupport.BdbEnvironmentTestSupport;
import org.junit.Before;
import org.junit.Test;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;

/**
 * @author Alexander Shabanov
 */
public final class KeyUtilTest extends BdbEnvironmentTestSupport {
  private BdbMapDao<ByteString, StringValue> mapDao;

  @Before
  public void init() {
    openTestEnvironment();

    final DatabaseConfig dbConfig = new DatabaseConfig();
    dbConfig.setAllowCreate(true);

    mapDao = new ProtobufBdbMapDaoSupport<>(new MapDaoConfig<>(getEnvironment().openDatabase(null, "test", dbConfig),
        new BdbEntryMapper<StringValue>() {
          @Nonnull
          @Override
          public StringValue map(@Nonnull DatabaseEntry key, @Nonnull DatabaseEntry value) throws IOException {
            return StringValue.getDefaultInstance().getParserForType()
                .parseFrom(value.getData(), value.getOffset(), value.getSize());
          }
        }));
  }

  @Test
  public void shouldCompareKeysInTheSameWayAsBDB() {
    mapDao.put(null, key("213"), val("a"));
    mapDao.put(null, key("2"), val("b"));
    mapDao.put(null, key("11"), val("c"));
    mapDao.put(null, key("3"), val("d"));
    mapDao.put(null, key("21"), val("e"));
    mapDao.put(null, key("22"), val("f"));
    mapDao.put(null, key("1"), val("g"));
    mapDao.put(null, key("33"), val("h"));
    mapDao.put(null, key("123"), val("i"));

    final List<ByteString> keys = mapDao.getEntries(null, 0, Integer.MAX_VALUE).stream().map(Map.Entry::getKey)
        .collect(Collectors.toList());

    final List<ByteString> sortedKeys = new ArrayList<>(keys);
    Collections.sort(sortedKeys, KeyUtil::compare);

    assertEquals(asStringList(keys), asStringList(sortedKeys));
  }

  //
  // Private
  //

  private static ByteString key(String val) {
    return ByteString.copyFrom(val.getBytes(StandardCharsets.UTF_8));
  }

  private static StringValue val(String val) {
    return StringValue.newBuilder().setValue(val).build();
  }

  private static List<String> asStringList(List<ByteString> keys) {
    return keys.stream().map(k -> new String(k.toByteArray(), StandardCharsets.UTF_8)).collect(Collectors.toList());
  }
}
