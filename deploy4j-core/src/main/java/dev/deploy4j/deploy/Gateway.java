package dev.deploy4j.deploy;

import dev.deploy4j.deploy.host.commands.AuditorHostCommands;
import dev.deploy4j.deploy.host.commands.DockerHostCommands;
import dev.deploy4j.deploy.host.commands.RegistryHostCommands;
import dev.deploy4j.deploy.host.commands.GatewayHostCommands;
import dev.deploy4j.deploy.host.ssh.SshHosts;
import dev.deploy4j.deploy.local.LocalHost;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Gateway extends Base {

  private static final Logger log = LoggerFactory.getLogger(Gateway.class);

  private final LockManager lockManager;
  private final RegistryHostCommands registry;
  private final GatewayHostCommands gateway;
  private final AuditorHostCommands audit;
  private final DockerHostCommands docker;

  public Gateway(SshHosts sshHosts, Hooks hooks, LocalHost localHost, LockManager lockManager, RegistryHostCommands registry, GatewayHostCommands gateway, AuditorHostCommands audit, DockerHostCommands docker) {
    super(sshHosts, hooks, localHost);
    this.lockManager = lockManager;
    this.registry = registry;
    this.gateway = gateway;
    this.audit = audit;
    this.docker = docker;
  }

  /**
   * Boot Gateway on servers
   */
  public void boot(DeployContext deployContext) {

    lockManager.withLock(deployContext, () -> {

      on(deployContext, deployContext.gatewayHosts(), host -> {
        host.execute(docker.createNetwork());
      });

      on(deployContext, deployContext.gatewayHosts(), host -> {

        host.execute(registry.login());
        host.execute(gateway.ensureEnvDirectory());
        host.upload( gateway.secretsIO(), gateway.secretsPath(), 600 );
        host.execute(gateway.startOrRun());

      });

    });


  }


  /**
   * Reboot Gateway on servers (stop container, remove container, start new container)
   *
   * @param rolling Reboot gateway on hosts in sequence, rather than in parallel
   */
  public void reboot(DeployContext deployContext, boolean rolling) {

    lockManager.withLock(deployContext, () -> {

      runHook(deployContext, "pre-gateway-reboot");

      on(deployContext, deployContext.gatewayHosts(), host -> {


        host.execute(audit.record("Rebooted gateway"));
        host.execute(registry.login());
        host.execute(gateway.stop(), false);
        host.execute(gateway.removeContainer());
        host.execute(gateway.ensureEnvDirectory());
        host.upload( gateway.secretsIO(), gateway.secretsPath(), 600 );
        host.execute(gateway.run());

      });

      runHook(deployContext, "post-gateway-reboot");

    });

  }

  /**
   * Start existing Gateway container on servers
   */
  public void start(DeployContext deployContext) {

    lockManager.withLock(deployContext, () -> {

      on(deployContext, deployContext.gatewayHosts(), host -> {

        host.execute(audit.record("Started gateway"));
        host.execute(gateway.start());


      });

    });

  }

  /**
   * Stop existing Gateway container on servers
   */
  public void stop(DeployContext deployContext) {

    lockManager.withLock(deployContext, () -> {

      on(deployContext, deployContext.gatewayHosts(), host -> {

        host.execute(audit.record("Stopped gateway"));
        host.execute(gateway.stop(), false);


      });

    });

  }

  /**
   * Restart existing Gateway container on servers
   */
  public void restart(DeployContext deployContext) {

    lockManager.withLock(deployContext, () -> {

      stop(deployContext);
      start(deployContext);

    });

  }

  /**
   * Show details about Gateway container from servers
   */
  public void details(DeployContext deployContext) {

    on(deployContext, deployContext.gatewayHosts(), host -> {

      log.info(host.capture(gateway.info()));

    });

  }

  /**
   * Show log lines from Gateway on servers
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

    // TODO: follow
//    if (lines != null || (since != null || grep != null)) {
//
//    } else {
//      lines = 100;
//    }

    on(deployContext, deployContext.gatewayHosts(), host -> {

      log.info(host.capture(gateway.logs(since, lines != null ? lines.toString() : null, grep, grepOptions)));

    });

  }

  /**
   * Remove Gateway container and image from servers
   */
  public void remove(DeployContext deployContext) {

    lockManager.withLock(deployContext, () -> {

      stop(deployContext);
      removeContainer(deployContext);
      removeImage(deployContext);

    });

  }


  /**
   * Remove Gateway container from servers
   */
  public void removeContainer(DeployContext deployContext) {

    lockManager.withLock(deployContext, () -> {

      on(deployContext, deployContext.gatewayHosts(), host -> {

        host.execute(audit.record("Removed gateway container"));
        host.execute(gateway.removeContainer());

      });

    });

  }

  /**
   * Remove Gateway image from servers
   */
  public void removeImage(DeployContext deployContext) {

    lockManager.withLock(deployContext, () -> {

      on(deployContext, deployContext.gatewayHosts(), host -> {

        host.execute(audit.record("Removed gateway image"));
        host.execute(gateway.removeImage());

      });

    });

  }

}
