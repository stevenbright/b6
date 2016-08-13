package com.truward.bdb.testSupport;

import b6.catalog.persistence.util.DirectoryCleanupTask;
import com.sleepycat.je.CacheMode;
import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;
import com.truward.bdb.BdbDatabaseConfigurer;
import com.truward.bdb.transaction.BdbTransactionMixin;
import org.junit.After;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author Alexander Shabanov
 */
public abstract class BdbEnvironmentTestSupport implements BdbTransactionMixin {
  protected final Logger log = LoggerFactory.getLogger(getClass());

  private final List<Runnable> cleanupTasks = new ArrayList<>();

  private Environment environment;

  @Nonnull
  @Override
  public Environment getEnvironment() {
    if (environment == null) {
      throw new AssertionError("Environment has not been opened yet");
    }
    return environment;
  }

  @After
  public void cleanup() {
    for (final Iterator<Runnable> it = cleanupTasks.iterator(); it.hasNext();) {
      try {
        it.next().run();
        log.info("Successful cleanup");
      } catch (Exception e) {
        log.warn("Error while cleanup", e);
      }

      it.remove();
    }
  }

  public static BdbDatabaseConfigurer getDatabaseConfigurer() {
    return () -> new DatabaseConfig()
        //.setTransactional(true)
        .setTemporary(true)
        .setAllowCreate(true)
        .setCacheMode(CacheMode.DEFAULT);
  }

  public void openTestEnvironment() {
    final String envDir = System.getProperty("java.io.tmpdir");
    final File envHome = new File(envDir, "BDB-" + System.currentTimeMillis() + "-" +
        ThreadLocalRandom.current().nextInt(Integer.MAX_VALUE));
    if (!envHome.mkdir()) {
      throw new IllegalStateException("Can't create temp env");
    }

    final EnvironmentConfig environmentConfig = new EnvironmentConfig();
    environmentConfig.setAllowCreate(true);
    environmentConfig.setTransactional(true);

    this.environment = new Environment(envHome, environmentConfig);
    log.info("Created environment at {}", envHome.getAbsolutePath());

    cleanupTasks.add(new DirectoryCleanupTask(envHome));
  }
}
