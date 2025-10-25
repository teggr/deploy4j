package dev.deploy4j.deploy;

import dev.deploy4j.deploy.host.commands.AccessoryHostCommands;
import dev.deploy4j.deploy.host.commands.AccessoryHostCommandsFactory;
import dev.deploy4j.deploy.host.commands.AuditorHostCommands;
import dev.deploy4j.deploy.host.commands.RegistryHostCommands;
import dev.deploy4j.deploy.host.ssh.SshHosts;
import dev.rebelcraft.cmd.Cmd;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

public class Accessory extends Base {

  private static final Logger log = LoggerFactory.getLogger(Accessory.class);

  private final LockManager lockManager;
  private final RegistryHostCommands registry;
  private final AuditorHostCommands audit;
  private final AccessoryHostCommandsFactory accessories;

  public Accessory(SshHosts sshHosts, LockManager lockManager, RegistryHostCommands registry, AuditorHostCommands audit, AccessoryHostCommandsFactory accessories) {
    super(sshHosts);
    this.lockManager = lockManager;
    this.registry = registry;
    this.audit = audit;
    this.accessories = accessories;
  }

  /**
   * Boot new accessory service on host (use NAME=all to boot all accessories)
   */
  public void boot(DeployContext deployContext) {

    boot(deployContext, null, true);

  }

  /**
   * Boot new accessory service on host
   *
   * @param name  (use NAME=all to boot all accessories)
   * @param login
   */
  public void boot(DeployContext deployContext, String name, boolean login) {

    lockManager.withLock(deployContext, () -> {

      if ("all".equalsIgnoreCase(name)) {

        deployContext.accessoryNames()
          .forEach(accessoryName -> boot(deployContext, accessoryName, login));

      } else {

        withAccessory(deployContext, name, (accessory, hosts) -> {

          directories(deployContext, name);
          upload(deployContext, name);

          on(hosts, host -> {

            if (login) {
              host.execute(registry.login());
            }
            host.execute(audit.record("Booted " + name + " accessory"));
            host.execute(accessory.run());

          });

        });

      }

    });

  }

  /**
   * Upload accessory files to host
   */
  public void upload(DeployContext deployContext, String name) {

    lockManager.withLock(deployContext, () -> {

      withAccessory(deployContext, name, (accessory, hosts) -> {

        on(hosts, host -> {

          accessory.files().forEach((local, remote) -> {
            accessory.ensureLocalFilePresent(local);

            host.execute(accessory.makeDirectoryFor(remote));
            host.upload(local, remote, 0);
            host.execute(Cmd.cmd("chmod", "755", remote));

          });

        });

      });

    });

  }

  /**
   * Create accessory directories on host
   */
  public void directories(DeployContext deployContext, String name) {

    lockManager.withLock(deployContext, () -> {

      withAccessory(deployContext, name, (accessory, hosts) -> {

        on(hosts, host -> {

          for (String hostPath : accessory.directories().keySet()) {
            host.execute(accessory.makeDirectory(hostPath));
          }

        });

      });

    });

  }

  /**
   * Reboot existing accessory on host (stop container, remove container, start new container; use NAME=all to boot all accessories)
   */
  public void reboot(DeployContext deployContext, String name) {

    lockManager.withLock(deployContext, () -> {

      if ("all".equalsIgnoreCase(name)) {

        deployContext.accessoryNames()
          .forEach(accessoryName -> reboot(deployContext, accessoryName));

      } else {

        withAccessory(deployContext, name, (accessory, hosts) -> {

          on(hosts, host -> {

            host.execute(registry.login());

          });

        });

        stop(deployContext, name);
        removeContainer(deployContext, name);
        boot(deployContext, name, false);

      }

    });

  }

  /**
   * Start existing accessory container on host
   */
  public void start(DeployContext deployContext, String name) {

    lockManager.withLock(deployContext, () -> {

      withAccessory(deployContext, name, (accessory, hosts) -> {

        on(hosts, host -> {

          host.execute(audit.record("Started " + name + " accessory"));
          host.execute(accessory.start());

        });

      });

    });

  }

  /**
   * Stop existing accessory container on host
   */
  public void stop(DeployContext deployContext, String name) {

    lockManager.withLock(deployContext, () -> {

      withAccessory(deployContext, name, (accessory, hosts) -> {

        on(hosts, host -> {

          host.execute(audit.record("Stopped " + name + " accessory"));
          host.execute(accessory.stop(), false);

        });

      });

    });

  }

  /**
   * Restart existing accessory container on host
   */
  public void restart(DeployContext deployContext, String name) {

    lockManager.withLock(deployContext, () -> {

      withAccessory(deployContext, name, (accessory, hosts) -> {

        stop(deployContext, name);
        start(deployContext, name);

      });

    });

  }

  /**
   * Show details about accessory on host (use NAME=all to show all accessories)
   */
  public void details(DeployContext deployContext, String name) {

    if ("all".equalsIgnoreCase(name)) {

      deployContext.accessoryNames()
        .forEach(accessoryName -> details(deployContext, accessoryName));

    } else {

      String type = "Accessory " + name;

      withAccessory(deployContext, type, (accessory, hosts) -> {

        on(hosts, host -> {

          log.info(host.capture(accessory.info()));

        });

      });

    }

  }

  /**
   * Execute a custom command on servers (use --help to show options)
   *
   * @param interactive Execute command over ssh for an interactive shell (use for console/bash)
   * @param reuse       Reuse currently running container instead of starting a new one
   * @param name
   * @param cmd
   */
  public void exec(DeployContext deployContext, boolean interactive, boolean reuse, String name, String cmd) {

    // TODO: interactive and not reuse

    withAccessory(deployContext, name, (accessory, hosts) -> {

      log.info("Launching command from existing container...");
      on(hosts, host -> {

        host.execute(audit.record("Executed cmd '" + cmd + "' on " + name + " accessory"));
        host.capture(accessory.executeInExistingContainer(cmd));

      });

    });

  }

  /**
   * Show log lines from accessory on host (use --help to show options)
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
    boolean follow,
    String name
  ) {

    withAccessory(deployContext, name, (accessory, hosts) -> {

//      // TODO: follow
//      if (lines != null || (since != null || grep != null)) {
//
//      } else {
//        lines = 100;
//      }
//
//      Integer finalLines = lines;
      on(hosts, host -> {

        log.info(host.capture(accessory.logs(since, lines != null ? lines.toString() : null, grep, grepOptions)));

      });

    });

  }

  /**
   * Remove accessory container, image and data directory from host (use NAME=all to remove all accessories)
   */
  public void remove(DeployContext deployContext, String name) {

    lockManager.withLock(deployContext, () -> {

      if ("all".equalsIgnoreCase(name)) {

        deployContext.accessoryNames()
          .forEach(accessoryName -> remove(deployContext, accessoryName));

      } else {

        removeAccessory(deployContext, name);

      }

    });

  }


  /**
   * Remove accessory container from host
   */
  public void removeContainer(DeployContext deployContext, String name) {

    lockManager.withLock(deployContext, () -> {

      withAccessory(deployContext, name, (accessory, hosts) -> {

        on(hosts, host -> {

          host.execute(audit.record("Remove " + name + " accessory container"));
          host.execute(accessory.removeContainer());

        });

      });

    });

  }


  /**
   * Remove accessory image from host
   */
  public void removeImage(DeployContext deployContext, String name) {

    lockManager.withLock(deployContext, () -> {

      withAccessory(deployContext, name, (accessory, hosts) -> {

        on(hosts, host -> {

          host.execute(audit.record("Removed " + name + " accessory image"));
          host.execute(accessory.removeImage());

        });

      });

    });

  }

  /**
   * Remove accessory directory used for uploaded files and data directories from host
   */
  public void removeServiceDirectory(DeployContext deployContext, String name) {

    lockManager.withLock(deployContext, () -> {

      withAccessory(deployContext, name, (accessory, hosts) -> {

        on(hosts, host -> {

          host.execute(accessory.removeServiceDirectory());

        });

      });

    });

  }

  // private

  private void withAccessory(DeployContext deployContext, String name, BiConsumer<AccessoryHostCommands, List<String>> block) {
    if (deployContext.config().accessory(name) != null) {
      AccessoryHostCommands accessory = accessories.accessory(name);
      block.accept(accessory, accessoryHosts(deployContext, accessory));
    } else {
      errorOnMissingAccessory(deployContext, name);
    }
  }

  private void errorOnMissingAccessory(DeployContext deployContext, String name) {
    List<String> options = deployContext.accessoryNames();
    throw new RuntimeException("No accessory by the name of '" + name + "'" + (options != null ? " (options:" + options.stream().collect(Collectors.joining(",")) + ")" : ""));
  }

  private List<String> accessoryHosts(DeployContext deployContext, AccessoryHostCommands accessory) {
    if (!deployContext.specificHosts().isEmpty()) {
      List<String> intersection = new ArrayList<>(deployContext.specificHosts());
      intersection.retainAll(accessory.hosts());
      return intersection;
    } else {
      return accessory.hosts();
    }
  }

  private void removeAccessory(DeployContext deployContext, String name) {

    withAccessory(deployContext, name, (accessory, hosts) -> {

      stop(deployContext, name);
      removeContainer(deployContext, name);
      removeImage(deployContext, name);
      removeServiceDirectory(deployContext, name);

    });

  }

}
