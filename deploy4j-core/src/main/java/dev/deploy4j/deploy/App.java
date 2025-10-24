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
  public void boot(DeployContext deployContext) {

    lockManager.withLock(deployContext, () -> {

      log.info( "Get most recent version available as an image..." );

      usingVersion(deployContext, versionOrLatest(deployContext), (version) -> {

        log.debug("Start container with version " + version + " using a " + deployContext.config().readinessDelay() + "s readiness delay (or reboot if already running)..." );

        on(deployContext.hosts(), host -> {

          for( Role role : deployContext.rolesOn(host.hostName()) ) {

            new PrepareAssets(host.hostName(), role, host, apps).run();

          }

        });

        Barrier barrier = new Barrier();

        on(deployContext.hosts(), host -> {

          for( Role role : deployContext.rolesOn(host.hostName()) ) {

            Boot appBoot = new Boot(host.hostName(), role, host, version, barrier, deployContext, audit, apps);
            appBoot.run();

          }

        });

        on(deployContext.hosts(), host -> {

          host.execute(audit.record("Tagging " + deployContext.config().absoluteImage() + " as the latest image"));
          host.execute(apps.app(null, null)
            .tagLatestImage());

        });

      });

    });

  }

  /**
   * Start existing app container on servers
   */
  public void start(DeployContext deployContext) {

    lockManager.withLock(deployContext, () -> {

      on(deployContext.hosts(), host -> {

        for (Role role : deployContext.rolesOn(host.hostName())) {

          host.execute(audit.record("Started app version " + deployContext.config().version()));
          host.execute(apps.app(role, host.hostName()).start());

        }

      });

    });

  }

  /**
   * Stop app container on servers
   */
  public void stop(DeployContext deployContext) {

    lockManager.withLock(deployContext, () -> {

      on(deployContext.hosts(), host -> {

        for (Role role : deployContext.rolesOn(host.hostName())) {

          host.execute(audit.record("Stopped app"));
          host.execute(apps.app(role, host.hostName()).stop());

        }

      });

    });

  }

  /**
   * Show details about app containers
   */
  public void details(DeployContext deployContext) {

    on(deployContext.hosts(), host -> {

      for (Role role : deployContext.rolesOn(host.hostName())) {

        log.info(host.capture(apps.app(role, host.hostName()).info()));

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
  public void exec(DeployContext deployContext, boolean interactive, boolean reuse, Map<String, String> env, String cmd) {

    // TODO: all the interactive stuff. we are reusing

    log.info("Get most recent version available as an image...");

    usingVersion(deployContext, versionOrLatest(deployContext), (version) -> {

      log.info("Launching command with version " + version + " from existing container...");

      on(deployContext.hosts(), host -> {

        for (Role role : deployContext.rolesOn(host.hostName())) {

          host.execute(audit.record("Executed cmd '" + cmd + "' on app version " + version));
          log.info(host.capture(apps.app(role, host.hostName()).executeInExistingContainer(cmd, env)));

        }

      });

    });

  }

  /**
   * Show app containers on servers
   */
  public void containers(DeployContext deployContext) {

    on(deployContext.hosts(), host -> {

      log.info(host.capture(apps.app(null, host.hostName()).listContainers()));

    });

  }

  /**
   * Detect app stale containers
   */
  public void staleContainers(DeployContext deployContext) {
    staleContainers(deployContext, false);
  }

  public void staleContainers(DeployContext deployContext, boolean stop) {

    withLockIfStopping(deployContext, stop, () -> {

      on(deployContext.hosts(), host -> {

        List<Role> roles = deployContext.rolesOn(host.hostName());

        for (Role role : roles) {

          AppHostCommands app = apps.app(role, host.hostName());
          List<String> versions = new java.util.ArrayList<>(Stream.of(host.capture(app.listVersions(), false).split("\n")).toList());
          versions.remove(host.capture(app.currentRunningVersion(), false).trim());

          for (String version : versions) {
            if (stop) {
              log.info( "Stopping stale container for role #{role} with version #{version}" );
              host.execute(app.stop(version), false);
            } else {
              log.info(  "Detected stale container for role #{role} with version #{version} (use `kamal app stale_containers --stop` to stop)" );
            }
          }

        }

      });

    });

  }

  /**
   * Show app images on servers
   */
  public void images(DeployContext deployContext) {

    on(deployContext.hosts(), host -> {

      log.info(host.capture(apps.app(null, host.hostName()).listImages()));

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
    DeployContext deployContext,
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

    on(deployContext.hosts(), host -> {

      for (Role role : deployContext.rolesOn(host.hostName())) {

        log.info(host.capture(apps.app(role, host.hostName()).logs(null, since, lines != null ? lines.toString() : null, grep, grepOptions)));

      }

    });

  }

  /**
   * Remove app containers and images from servers
   */
  public void remove(DeployContext deployContext) {
    lockManager.withLock(deployContext, () -> {
      stop(deployContext);
      removeContainers(deployContext);
      removeImages(deployContext);
    });
  }

  /**
   * Remove app container with given version from servers
   */
  public void removeContainer(DeployContext deployContext, String version) {

    lockManager.withLock(deployContext, () -> {

      on(deployContext.hosts(), host -> {

        for (Role role : deployContext.rolesOn(host.hostName())) {

          host.execute(audit.record("Removed app container with version " + version));
          host.execute(apps.app(role, host.hostName()).removeContainer(version));

        }

      });

    });

  }

  /**
   * Remove all app containers from servers
   */
  public void removeContainers(DeployContext deployContext) {

    lockManager.withLock(deployContext, () -> {

      on(deployContext.hosts(), host -> {

        for (Role role : deployContext.rolesOn(host.hostName())) {

          host.execute(audit.record("Removed all app containers"));
          host.execute(apps.app(role, host.hostName()).removeContainers());

        }

      });

    });

  }

  /**
   * Remove all app images from servers
   */
  public void removeImages(DeployContext deployContext) {

    lockManager.withLock(deployContext, () -> {

      on(deployContext.hosts(), host -> {

        for (Role role : deployContext.rolesOn(host.hostName())) {

          host.execute(audit.record("Removed all app images"));
          host.execute(apps.app(role, host.hostName()).removeImages());

        }

      });

    });

  }

  /**
   * Show app version currently running on servers
   */
  public void version(DeployContext deployContext) {

    on(deployContext.hosts(), host -> {

      Role role = deployContext.rolesOn(host.hostName()).getFirst();

      log.info(host.capture(apps.app(role, host.hostName()).currentRunningVersion()));


    });

  }

  // private

  private void usingVersion(DeployContext deployContext, String newVersion, Consumer<String> block) {
    if (newVersion != null) {
      String oldVersion = deployContext.config().version();
      try {
        deployContext.config().version(newVersion);
        block.accept(newVersion);
      } finally {
        deployContext.config().version(oldVersion);
      }
    } else {
      block.accept( deployContext.config().version() );
    }
  }

  // TODO: current running version

  private String versionOrLatest(DeployContext deployContext) {
    return deployContext.config().version() != null ? deployContext.config().version() : deployContext.config().latestTag();
  }

  private void withLockIfStopping(DeployContext deployContext, boolean stop, Runnable block) {
    if(stop) {
      lockManager.withLock(deployContext, block);
    }else {
      block.run();
    }
  }

}
