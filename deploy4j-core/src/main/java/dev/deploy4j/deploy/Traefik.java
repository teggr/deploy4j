package dev.deploy4j.deploy;

import dev.deploy4j.deploy.host.commands.RegistryHostCommands;
import dev.deploy4j.deploy.host.commands.TraefikHostCommands;
import dev.deploy4j.deploy.host.ssh.SshHosts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Traefik extends Base {

  private static final Logger log = LoggerFactory.getLogger(Traefik.class);

  private final LockManager lockManager;
  private final RegistryHostCommands registry;
  private final TraefikHostCommands traefik;

  public Traefik(SshHosts sshHosts, LockManager lockManager, RegistryHostCommands registry, TraefikHostCommands traefik) {
    super(sshHosts);
    this.lockManager = lockManager;
    this.registry = registry;
    this.traefik = traefik;
  }

  /**
   * Boot Traefik on servers
   */
  public void boot(Commander commander) {

    lockManager.withLock(commander, () -> {

      on(commander.traefikHosts(), host -> {

        host.execute(registry.login());
        host.execute(traefik.startOrRun());

      });

    });


  }


  /**
   * Reboot Traefik on servers (stop container, remove container, start new container)
   *
   * @param rolling Reboot traefik on hosts in sequence, rather than in parallel
   */
  public void reboot(Commander commander, boolean rolling) {

    lockManager.withLock(commander, () -> {

      on(commander.traefikHosts(), host -> {

        host.execute(commander.auditor().record("Rebooted traefik"));
        host.execute(registry.login());
        host.execute(traefik.stop());
        host.execute(traefik.removeContainer());
        host.execute(traefik.run());

      });

    });

  }

  /**
   * Start existing Traefik container on servers
   */
  public void start(Commander commander) {

    lockManager.withLock(commander, () -> {

      on(commander.traefikHosts(), host -> {

        host.execute(commander.auditor().record("Started traefik"));
        host.execute(traefik.start());


      });

    });

  }

  /**
   * Stop existing Traefik container on servers
   */
  public void stop(Commander commander) {

    lockManager.withLock(commander, () -> {

      on(commander.traefikHosts(), host -> {

        host.execute(commander.auditor().record("Stopped traefik"));
        host.execute(traefik.stop());


      });

    });

  }

  /**
   * Restart existing Traefik container on servers
   */
  public void restart(Commander commander) {

    lockManager.withLock(commander, () -> {

      stop(commander);
      start(commander);

    });

  }

  /**
   * Show details about Traefik container from servers
   */
  public void details(Commander commander) {

    on(commander.traefikHosts(), host -> {

      System.out.println(host.capture(traefik.info()));

    });

  }

  /**
   * Show log lines from Traefik on servers
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

    // TODO: follow
//    if (lines != null || (since != null || grep != null)) {
//
//    } else {
//      lines = 100;
//    }

    on(commander.traefikHosts(), host -> {

      System.out.println(host.capture(traefik.logs(since, lines != null ? lines.toString() : null, grep, grepOptions)));

    });

  }

  /**
   * Remove Traefik container and image from servers
   */
  public void remove(Commander commander) {

    lockManager.withLock(commander, () -> {

      stop(commander);
      removeContainer(commander);
      removeImage(commander);

    });

  }


  /**
   * Remove Traefik container from servers
   */
  public void removeContainer(Commander commander) {

    lockManager.withLock(commander, () -> {

      on(commander.traefikHosts(), host -> {

        host.execute(commander.auditor().record("Removed traefik container"));
        host.execute(traefik.removeContainer());

      });

    });

  }

  /**
   * Remove Traefik image from servers
   */
  public void removeImage(Commander commander) {

    lockManager.withLock(commander, () -> {

      on(commander.traefikHosts(), host -> {

        host.execute(commander.auditor().record("Removed traefik image"));
        host.execute(traefik.removeImage());

      });

    });

  }

}
