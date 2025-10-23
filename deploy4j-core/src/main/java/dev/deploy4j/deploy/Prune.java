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
  public void all(Commander commander) {

    lockManager.withLock(commander, () -> {

      containers(commander);
      images(commander);

    });

  }

  /**
   * Prune unused images
   */
  public void images(Commander commander) {

    lockManager.withLock(commander, () -> {

      on(commander.hosts(), host -> {

        host.execute(audit.record("Pruned images"));
        host.execute(prune.danglingImages());
        host.execute(prune.taggedImages());


      });

    });

  }

  public void containers(Commander commander) {
    containers(commander, null);
  }

  /**
   * Prune all stopped containers, except the last n (default 5)
   *
   * @param retain Number of containers to retain
   */
  public void containers(Commander commander, Integer retain) {

    if (retain == null) {
      retain = commander.config().retainContainer();
    }
    Integer finalRetain = retain;

    if (finalRetain < 1) {
      throw new RuntimeException("retain must be at least 1");
    }

    lockManager.withLock(commander, () -> {

      on(commander.hosts(), host -> {

        host.execute(audit.record("Pruned containers"));
        host.execute(prune.appContainers(finalRetain));
        host.execute(prune.healthcheckContainers());

      });

    });

  }
}
