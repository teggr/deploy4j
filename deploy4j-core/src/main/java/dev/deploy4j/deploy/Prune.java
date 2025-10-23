package dev.deploy4j.deploy;

import dev.deploy4j.deploy.host.commands.AuditorHostCommands;
import dev.deploy4j.deploy.host.commands.PruneHostCommands;
import dev.deploy4j.deploy.host.ssh.SshHosts;

public class Prune extends Base {

  private final LockManager lockManager;
  private final PruneHostCommands prune;
  private final AuditorHostCommands audit;

  public Prune(SshHosts sshHosts, LockManager lockManager, PruneHostCommands prune, AuditorHostCommands audit) {
    super(sshHosts);
    this.lockManager = lockManager;
    this.prune = prune;
    this.audit = audit;
  }

  /**
   * Prune unused images and stopped containers
   */
  public void all(DeployContext deployContext) {

    lockManager.withLock(deployContext, () -> {

      containers(deployContext);
      images(deployContext);

    });

  }

  /**
   * Prune unused images
   */
  public void images(DeployContext deployContext) {

    lockManager.withLock(deployContext, () -> {

      on(deployContext.hosts(), host -> {

        host.execute(audit.record("Pruned images"));
        host.execute(prune.danglingImages());
        host.execute(prune.taggedImages());


      });

    });

  }

  public void containers(DeployContext deployContext) {
    containers(deployContext, null);
  }

  /**
   * Prune all stopped containers, except the last n (default 5)
   *
   * @param retain Number of containers to retain
   */
  public void containers(DeployContext deployContext, Integer retain) {

    if (retain == null) {
      retain = deployContext.config().retainContainer();
    }
    Integer finalRetain = retain;

    if (finalRetain < 1) {
      throw new RuntimeException("retain must be at least 1");
    }

    lockManager.withLock(deployContext, () -> {

      on(deployContext.hosts(), host -> {

        host.execute(audit.record("Pruned containers"));
        host.execute(prune.appContainers(finalRetain));
        host.execute(prune.healthcheckContainers());

      });

    });

  }
}
