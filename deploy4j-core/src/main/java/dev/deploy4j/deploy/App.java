package dev.deploy4j.deploy;

import dev.deploy4j.deploy.app.Boot;
import dev.deploy4j.deploy.app.PrepareAssets;
import dev.deploy4j.deploy.configuration.Role;
import dev.deploy4j.deploy.healthcheck.Barrier;
import dev.deploy4j.deploy.host.commands.AppHostCommands;
import dev.deploy4j.deploy.host.commands.AppHostCommandsFactory;
import dev.deploy4j.deploy.host.commands.AuditorHostCommands;
import dev.deploy4j.deploy.host.ssh.SshHosts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class App extends Base {

  private static final Logger log = LoggerFactory.getLogger(App.class);
  private final LockManager lockManager;
  private final AuditorHostCommands audit;
  private final AppHostCommandsFactory apps;

  public App(SshHosts sshHosts, LockManager lockManager, AuditorHostCommands audit, AppHostCommandsFactory apps) {
    super(sshHosts);
    this.lockManager = lockManager;
    this.audit = audit;
    this.apps = apps;
  }

  /**
   * Boot app on servers (or reboot app if already running)
   */
  public void boot(Commander commander) {

    lockManager.withLock(commander, () -> {

      // "Get most recent version available as an image..."
      usingVersion(commander, versionOrLatest(commander), (version) -> {

        log.info("Start container with version " + version + " using a "+commander.config().readinessDelay() + "s readiness delay (or reboot if already running)..." );

        on(commander.hosts(), host -> {

          for( Role role : commander.rolesOn(host.hostName()) ) {

            new PrepareAssets(host.hostName(), role, host, apps).run();

          }

        });

        Barrier barrier = new Barrier();

        on(commander.hosts(), host -> {

          for( Role role : commander.rolesOn(host.hostName()) ) {

            Boot appBoot = new Boot(host.hostName(), role, host, version, barrier, commander, audit, apps);
            appBoot.run();

          }

        });

        on(commander.hosts(), host -> {

          host.execute(audit.record("Tagging " + commander.config().absoluteImage() + " as the latest image"));
          host.execute(apps.app(null, null)
            .tagLatestImage());

        });

      });

    });

  }

  /**
   * Start existing app container on servers
   */
  public void start(Commander commander) {

    lockManager.withLock(commander, () -> {

      on(commander.hosts(), host -> {

        for (Role role : commander.rolesOn(host.hostName())) {

          host.execute(audit.record("Started app version " + commander.config().version()));
          host.execute(apps.app(role, host.hostName()).start());

        }

      });

    });

  }

  /**
   * Stop app container on servers
   */
  public void stop(Commander commander) {

    lockManager.withLock(commander, () -> {

      on(commander.hosts(), host -> {

        for (Role role : commander.rolesOn(host.hostName())) {

          host.execute(audit.record("Stopped app"));
          host.execute(apps.app(role, host.hostName()).stop());

        }

      });

    });

  }

  /**
   * Show details about app containers
   */
  public void details(Commander commander) {

    on(commander.hosts(), host -> {

      for (Role role : commander.rolesOn(host.hostName())) {

        System.out.println(host.capture(apps.app(role, host.hostName()).info()));

      }

    });

  }

  /**
   * Execute a custom command on servers within the app container (use --help to show options)
   *
   * @param interactive Execute command over ssh for an interactive shell (use for console/bash)
   * @param reuse       Reuse currently running container instead of starting a new one
   * @param env         Set environment variables for the command
   */
  public void exec(Commander commander, boolean interactive, boolean reuse, Map<String, String> env, String cmd) {

    // TODO: all the interactive stuff. we are reusing

    System.out.println("Get most recent version available as an image...");

    usingVersion(commander, versionOrLatest(commander), (version) -> {

      System.out.println("Launching command with version " + version + " from existing container...");

      on(commander.hosts(), host -> {

        for (Role role : commander.rolesOn(host.hostName())) {

          host.execute(audit.record("Executed cmd '" + cmd + "' on app version " + version));
          System.out.println(host.capture(apps.app(role, host.hostName()).executeInExistingContainer(cmd, env)));

        }

      });

    });

  }

  /**
   * Show app containers on servers
   */
  public void containers(Commander commander) {

    on(commander.hosts(), host -> {

      System.out.println(host.capture(apps.app(null, host.hostName()).listContainers()));

    });

  }

  /**
   * Detect app stale containers
   */
  public void staleContainers(Commander commander) {
    staleContainers(commander, false);
  }

  public void staleContainers(Commander commander, boolean stop) {

    withLockIfStopping(commander, stop, () -> {

      on(commander.hosts(), host -> {

        List<Role> roles = commander.rolesOn(host.hostName());

        for (Role role : roles) {

          AppHostCommands app = apps.app(role, host.hostName());
          List<String> versions = new java.util.ArrayList<>(Stream.of(host.capture(app.listVersions()).split("\n")).toList());
          versions.remove(host.capture(app.currentRunningVersion()).trim());

          for (String version : versions) {
            if (stop) {
              System.out.println( "Stopping stale container for role #{role} with version #{version}" );
              host.execute(app.stop(version));
            } else {
              System.out.println(  "Detected stale container for role #{role} with version #{version} (use `kamal app stale_containers --stop` to stop)" );
            }
          }

        }

      });

    });

  }

  /**
   * Show app images on servers
   */
  public void images(Commander commander) {

    on(commander.hosts(), host -> {

      System.out.println(host.capture(apps.app(null, host.hostName()).listImages()));

    });

  }

  /**
   * Show log lines from app on servers (use --help to show options)
   *
   * @param since       Show logs since timestamp (e.g. 2013-01-02T13:23:37Z) or relative (e.g. 42m for 42 minutes)
   * @param lines       Number of log lines to pull from each server
   * @param grep        Show lines with grep match only (use this to fetch specific requests by id)
   * @param grepOptions Additional options supplied to grep
   * @param follow      Follow logs on primary server (or specific host set by --hosts)
   */
  public void logs(
    Commander commander,
    String since,
    Integer lines,
    String grep,
    String grepOptions,
    boolean follow
  ) {

//    // TODO: follow
//    if (lines != null || (since != null || grep != null)) {
//
//    } else {
//      lines = 100;
//    }

    on(commander.hosts(), host -> {

      for (Role role : commander.rolesOn(host.hostName())) {

        System.out.println(host.capture(apps.app(role, host.hostName()).logs(null, since, lines != null ? lines.toString() : null, grep, grepOptions)));

      }

    });

  }

  /**
   * Remove app containers and images from servers
   */
  public void remove(Commander commander) {
    lockManager.withLock(commander, () -> {
      stop(commander);
      removeContainers(commander);
      removeImages(commander);
    });
  }

  /**
   * Remove app container with given version from servers
   */
  public void removeContainer(Commander commander, String version) {

    lockManager.withLock(commander, () -> {

      on(commander.hosts(), host -> {

        for (Role role : commander.rolesOn(host.hostName())) {

          host.execute(audit.record("Removed app container with version " + version));
          host.execute(apps.app(role, host.hostName()).removeContainer(version));

        }

      });

    });

  }

  /**
   * Remove all app containers from servers
   */
  public void removeContainers(Commander commander) {

    lockManager.withLock(commander, () -> {

      on(commander.hosts(), host -> {

        for (Role role : commander.rolesOn(host.hostName())) {

          host.execute(audit.record("Removed all app containers"));
          host.execute(apps.app(role, host.hostName()).removeContainers());

        }

      });

    });

  }

  /**
   * Remove all app images from servers
   */
  public void removeImages(Commander commander) {

    lockManager.withLock(commander, () -> {

      on(commander.hosts(), host -> {

        for (Role role : commander.rolesOn(host.hostName())) {

          host.execute(audit.record("Removed all app images"));
          host.execute(apps.app(role, host.hostName()).removeImages());

        }

      });

    });

  }

  /**
   * Show app version currently running on servers
   */
  public void version(Commander commander) {

    on(commander.hosts(), host -> {

      Role role = commander.rolesOn(host.hostName()).getFirst();

      System.out.println(host.capture(apps.app(role, host.hostName()).currentRunningVersion()));


    });

  }

  // private

  private void usingVersion(Commander commander, String newVersion, Consumer<String> block) {
    if (newVersion != null) {
      String oldVersion = commander.config().version();
      try {
        commander.config().version(newVersion);
        block.accept(newVersion);
      } finally {
        commander.config().version(oldVersion);
      }
    } else {
      block.accept( commander.config().version() );
    }
  }

  // TODO: current running version

  private String versionOrLatest(Commander commander) {
    return commander.config().version() != null ? commander.config().version() : commander.config().latestTag();
  }

  private void withLockIfStopping(Commander commander, boolean stop, Runnable block) {
    if(stop) {
      lockManager.withLock(commander, block);
    }else {
      block.run();
    }
  }

}
