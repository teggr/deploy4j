package dev.deploy4j.deploy;

import dev.deploy4j.deploy.host.commands.AuditorHostCommands;
import dev.deploy4j.deploy.host.commands.DockerHostCommands;
import dev.deploy4j.deploy.host.commands.RegistryHostCommands;
import dev.deploy4j.deploy.host.commands.SpringBootAdminHostCommands;
import dev.deploy4j.deploy.host.ssh.SshHosts;
import dev.deploy4j.deploy.local.LocalHost;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SpringBootAdmin extends Base {

  private static final Logger log = LoggerFactory.getLogger(SpringBootAdmin.class);

  private final LockManager lockManager;
  private final RegistryHostCommands registry;
  private final SpringBootAdminHostCommands springBootAdmin;
  private final AuditorHostCommands audit;
  private final DockerHostCommands docker;

  public SpringBootAdmin(SshHosts sshHosts, Hooks hooks, LocalHost localHost, LockManager lockManager, RegistryHostCommands registry, SpringBootAdminHostCommands springBootAdmin, AuditorHostCommands audit, DockerHostCommands docker) {
    super(sshHosts, hooks, localHost);
    this.lockManager = lockManager;
    this.registry = registry;
    this.springBootAdmin = springBootAdmin;
    this.audit = audit;
    this.docker = docker;
  }

  /**
   * Boot Spring Boot Admin on servers
   */
  public void boot(DeployContext deployContext) {

    lockManager.withLock(deployContext, () -> {

      on(deployContext, deployContext.springBootAdminHosts(), host -> {
        host.execute(docker.createNetwork());
      });

      on(deployContext, deployContext.springBootAdminHosts(), host -> {

        host.execute(registry.login());
        host.execute(springBootAdmin.ensureEnvDirectory());
        host.upload( springBootAdmin.secretsIO(), springBootAdmin.secretsPath(), 600 );
        host.execute(springBootAdmin.startOrRun());

      });

    });


  }


  /**
   * Reboot Spring Boot Admin on servers (stop container, remove container, start new container)
   *
   * @param rolling Reboot spring-boot-admin on hosts in sequence, rather than in parallel
   */
  public void reboot(DeployContext deployContext, boolean rolling) {

    lockManager.withLock(deployContext, () -> {

      runHook(deployContext, "pre-spring-boot-admin-reboot");

      on(deployContext, deployContext.springBootAdminHosts(), host -> {


        host.execute(audit.record("Rebooted spring-boot-admin"));
        host.execute(registry.login());
        host.execute(springBootAdmin.stop(), false);
        host.execute(springBootAdmin.removeContainer());
        host.execute(springBootAdmin.ensureEnvDirectory());
        host.upload( springBootAdmin.secretsIO(), springBootAdmin.secretsPath(), 600 );
        host.execute(springBootAdmin.run());

      });

      runHook(deployContext, "post-spring-boot-admin-reboot");

    });

  }

  /**
   * Start existing Spring Boot Admin container on servers
   */
  public void start(DeployContext deployContext) {

    lockManager.withLock(deployContext, () -> {

      on(deployContext, deployContext.springBootAdminHosts(), host -> {

        host.execute(audit.record("Started spring-boot-admin"));
        host.execute(springBootAdmin.start());


      });

    });

  }

  /**
   * Stop existing Spring Boot Admin container on servers
   */
  public void stop(DeployContext deployContext) {

    lockManager.withLock(deployContext, () -> {

      on(deployContext, deployContext.springBootAdminHosts(), host -> {

        host.execute(audit.record("Stopped spring-boot-admin"));
        host.execute(springBootAdmin.stop(), false);


      });

    });

  }

  /**
   * Restart existing Spring Boot Admin container on servers
   */
  public void restart(DeployContext deployContext) {

    lockManager.withLock(deployContext, () -> {

      stop(deployContext);
      start(deployContext);

    });

  }

  /**
   * Show details about Spring Boot Admin container from servers
   */
  public void details(DeployContext deployContext) {

    on(deployContext, deployContext.springBootAdminHosts(), host -> {

      log.info(host.capture(springBootAdmin.info()));

    });

  }

  /**
   * Show log lines from Spring Boot Admin on servers
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

    on(deployContext, deployContext.springBootAdminHosts(), host -> {

      log.info(host.capture(springBootAdmin.logs(since, lines != null ? lines.toString() : null, grep, grepOptions)));

    });

  }

  /**
   * Remove Spring Boot Admin container and image from servers
   */
  public void remove(DeployContext deployContext) {

    lockManager.withLock(deployContext, () -> {

      stop(deployContext);
      removeContainer(deployContext);
      removeImage(deployContext);

    });

  }


  /**
   * Remove Spring Boot Admin container from servers
   */
  public void removeContainer(DeployContext deployContext) {

    lockManager.withLock(deployContext, () -> {

      on(deployContext, deployContext.springBootAdminHosts(), host -> {

        host.execute(audit.record("Removed spring-boot-admin container"));
        host.execute(springBootAdmin.removeContainer());

      });

    });

  }

  /**
   * Remove Spring Boot Admin image from servers
   */
  public void removeImage(DeployContext deployContext) {

    lockManager.withLock(deployContext, () -> {

      on(deployContext, deployContext.springBootAdminHosts(), host -> {

        host.execute(audit.record("Removed spring-boot-admin image"));
        host.execute(springBootAdmin.removeImage());

      });

    });

  }

}
