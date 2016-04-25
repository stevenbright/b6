package b6.website;

import com.truward.brikar.server.launcher.StandardLauncher;

/**
 * Entry point.
 */
public final class Launcher {

  public static void main(String[] args) throws Exception {
    try (StandardLauncher launcher = new StandardLauncher("classpath:/b6Website/")) {
      launcher
          .setSpringSecurityEnabled(true)
          .setSessionsEnabled(true)
          .setStaticHandlerEnabled(true)
          .start();
    }
  }
}
