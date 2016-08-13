package b6.catalog.persistence.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

/**
 * @author Alexander Shabanov
 */
public final class DirectoryCleanupTask implements Runnable {

  private final Logger log = LoggerFactory.getLogger(getClass());
  private final File envHome;

  public DirectoryCleanupTask(@Nonnull File envHome) {
    this.envHome = envHome;
  }

  @Override
  public void run() {
    try {
      deleteRecursively(envHome);
      log.info("Cleanup completed, {} deleted", envHome.getAbsolutePath());
    } catch (IOException e) {
      log.error("Error while doing final cleanup", e);
    }
  }

  private static void deleteRecursively(File envHome) throws IOException {
    final Path envHomePath = envHome.toPath();
    Files.walkFileTree(envHomePath, new SimpleFileVisitor<Path>() {
      @Override
      public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
        Files.delete(file);
        return FileVisitResult.CONTINUE;
      }

      @Override
      public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
        Files.delete(dir);
        return FileVisitResult.CONTINUE;
      }
    });
    Files.deleteIfExists(envHomePath);
  }
}