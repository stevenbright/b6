package b6.persistence.testSupport;

import b6.persistence.util.DirectoryCleanupTask;
import com.sleepycat.je.CacheMode;
import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;
import org.junit.After;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author Alexander Shabanov
 */
public abstract class BdbEnvironmentTestSupport {
  protected final Logger log = LoggerFactory.getLogger(getClass());

  private final List<Runnable> cleanupTasks = new ArrayList<>();

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

  public static DatabaseConfig dbConfig() {
    return new DatabaseConfig()
        .setTransactional(true)
        .setAllowCreate(true)
        .setSortedDuplicates(false)
        .setDeferredWrite(false)
        .setCacheMode(CacheMode.DEFAULT);
  }

  public Environment openTestEnvironment() {
    final String envDir = System.getProperty("java.io.tmpdir");
    final File envHome = new File(envDir, "BDB-" + System.currentTimeMillis() + "-" +
        ThreadLocalRandom.current().nextInt(Integer.MAX_VALUE));
    if (!envHome.mkdir()) {
      throw new IllegalStateException("Can't create temp env");
    }

    final EnvironmentConfig environmentConfig = new EnvironmentConfig();
    environmentConfig.setAllowCreate(true);
    environmentConfig.setTransactional(true);

    final Environment environment = new Environment(envHome, environmentConfig);
    log.info("Created environment at {}", envHome.getAbsolutePath());

    cleanupTasks.add(new DirectoryCleanupTask(envHome));

    return environment;
  }
}
