package dev.deploy4j.deploy.cli;

import dev.deploy4j.deploy.host.commands.AccessoryHostCommands;
import dev.deploy4j.deploy.host.commands.RegistryHostCommands;
import dev.rebelcraft.cmd.Cmd;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

public class Accessory extends Base {

  private static final Logger log = LoggerFactory.getLogger(Accessory.class);

  private final RegistryHostCommands registry;

  public Accessory(Commander commander, RegistryHostCommands registry) {
    super(commander);
    this.registry = registry;
  }

  /**
   * Boot new accessory service on host (use NAME=all to boot all accessories)
   */
  public void boot() {

    boot(null, true);

  }

  /**
   * Boot new accessory service on host
   *
   * @param name  (use NAME=all to boot all accessories)
   * @param login
   */
  public void boot(String name, boolean login) {

    withLock(() -> {

      if ("all".equalsIgnoreCase(name)) {

        commander().accessoryNames()
          .forEach(accessoryName -> boot(accessoryName, login));

      } else {

        withAccessory(name, (accessory, hosts) -> {

          directories(name);
          upload(name);

          on(hosts, host -> {

            if (login) {
              host.execute(registry.login());
            }
            host.execute(commander().auditor().record("Booted " + name + " accessory"));
            host.execute(accessory.run());

          });

        });

      }

    });

  }

  /**
   * Upload accessory files to host
   */
  public void upload(String name) {

    withLock(() -> {

      withAccessory(name, (accessory, hosts) -> {

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
  public void directories(String name) {

    withLock(() -> {

      withAccessory(name, (accessory, hosts) -> {

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
  public void reboot(String name) {

    withLock(() -> {

      if ("all".equalsIgnoreCase(name)) {

        commander().accessoryNames()
          .forEach(accessoryName -> reboot(accessoryName));

      } else {

        withAccessory(name, (accessory, hosts) -> {

          on(hosts, host -> {

            host.execute(registry.login());

          });

        });

        stop(name);
        removeContainer(name);
        boot(name, false);

      }

    });

  }

  /**
   * Start existing accessory container on host
   */
  public void start(String name) {

    withLock(() -> {

      withAccessory(name, (accessory, hosts) -> {

        on(hosts, host -> {

          host.execute(commander().auditor().record("Started " + name + " accessory"));
          host.execute(accessory.start());

        });

      });

    });

  }

  /**
   * Stop existing accessory container on host
   */
  public void stop(String name) {

    withLock(() -> {

      withAccessory(name, (accessory, hosts) -> {

        on(hosts, host -> {

          host.execute(commander().auditor().record("Stopped " + name + " accessory"));
          host.execute(accessory.stop());

        });

      });

    });

  }

  /**
   * Restart existing accessory container on host
   */
  public void restart(String name) {

    withLock(() -> {

      withAccessory(name, (accessory, hosts) -> {

        stop(name);
        start(name);

      });

    });

  }

  /**
   * Show details about accessory on host (use NAME=all to show all accessories)
   */
  public void details(String name) {

    if ("all".equalsIgnoreCase(name)) {

      commander().accessoryNames()
        .forEach(accessoryName -> details(accessoryName));

    } else {

      String type = "Accessory " + name;

      withAccessory(type, (accessory, hosts) -> {

        on(hosts, host -> {

          System.out.println(host.capture(accessory.info()));

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
  public void exec(boolean interactive, boolean reuse, String name, String cmd) {

    // TODO: interactive and not reuse

    withAccessory(name, (accessory, hosts) -> {

      System.out.println("Launching command from existing container...");
      on(hosts, host -> {

        host.execute(commander().auditor().record("Executed cmd '" + cmd + "' on " + name + " accessory"));
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
    String since,
    Integer lines,
    String grep,
    String grepOptions,
    boolean follow,
    String name
  ) {

    withAccessory(name, (accessory, hosts) -> {

//      // TODO: follow
//      if (lines != null || (since != null || grep != null)) {
//
//      } else {
//        lines = 100;
//      }
//
//      Integer finalLines = lines;
      on(hosts, host -> {

        System.out.println(host.capture(accessory.logs(since, lines != null ? lines.toString() : null, grep, grepOptions)));

      });

    });

  }

  /**
   * Remove accessory container, image and data directory from host (use NAME=all to remove all accessories)
   */
  public void remove(String name) {

    withLock(() -> {

      if ("all".equalsIgnoreCase(name)) {

        commander().accessoryNames()
          .forEach(accessoryName -> remove(accessoryName));

      } else {

        removeAccessory(name);

      }

    });

  }


  /**
   * Remove accessory container from host
   */
  public void removeContainer(String name) {

    withLock(() -> {

      withAccessory(name, (accessory, hosts) -> {

        on(hosts, host -> {

          host.execute(commander().auditor().record("Remove " + name + " accessory container"));
          host.execute(accessory.removeContainer());

        });

      });

    });

  }


  /**
   * Remove accessory image from host
   */
  public void removeImage(String name) {

    withLock(() -> {

      withAccessory(name, (accessory, hosts) -> {

        on(hosts, host -> {

          host.execute(commander().auditor().record("Removed " + name + " accessory image"));
          host.execute(accessory.removeImage());

        });

      });

    });

  }

  /**
   * Remove accessory directory used for uploaded files and data directories from host
   */
  public void removeServiceDirectory(String name) {

    withLock(() -> {

      withAccessory(name, (accessory, hosts) -> {

        on(hosts, host -> {

          host.execute(accessory.removeServiceDirectory());

        });

      });

    });

  }

  // private

  private void withAccessory(String name, BiConsumer<AccessoryHostCommands, List<String>> block) {
    if (commander().config().accessory(name) != null) {
      AccessoryHostCommands accessory = commander().accessory(name);
      block.accept(accessory, accessoryHosts(accessory));
    } else {
      errorOnMissingAccessory(name);
    }
  }

  private void errorOnMissingAccessory(String name) {
    List<String> options = commander().accessoryNames();
    throw new RuntimeException("No accessory by the name of '" + name + "'" + (options != null ? " (options:" + options.stream().collect(Collectors.joining(",")) + ")" : ""));
  }

  private List<String> accessoryHosts(AccessoryHostCommands accessory) {
    if (!commander().specificHosts().isEmpty()) {
      List<String> intersection = new ArrayList<>(commander().specificHosts());
      intersection.retainAll(accessory.hosts());
      return intersection;
    } else {
      return accessory.hosts();
    }
  }

  private void removeAccessory(String name) {

    withAccessory(name, (accessory, hosts) -> {

      stop(name);
      removeContainer(name);
      removeImage(name);
      removeServiceDirectory(name);

    });

  }

}
