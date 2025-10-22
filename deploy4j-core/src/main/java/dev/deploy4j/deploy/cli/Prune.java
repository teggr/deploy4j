package dev.deploy4j.deploy.cli;

import dev.deploy4j.deploy.host.commands.PruneHostCommands;

public class Prune extends Base {

  private final PruneHostCommands prune;

  public Prune(Commander commander, PruneHostCommands prune) {
    super(commander);
    this.prune = prune;
  }

  /**
   * Prune unused images and stopped containers
   */
  public void all() {

    withLock(() -> {

      containers();
      images();

    });

  }

  /**
   * Prune unused images
   */
  public void images() {

    withLock(() -> {

      on(commander().hosts(), host -> {

        host.execute(commander().auditor().record("Pruned images"));
        host.execute(prune.danglingImages());
        host.execute(prune.taggedImages());


      });

    });

  }

  public void containers() {
    containers(null);
  }

  /**
   * Prune all stopped containers, except the last n (default 5)
   *
   * @param retain Number of containers to retain
   */
  public void containers(Integer retain) {

    if (retain == null) {
      retain = commander().config().retainContainer();
    }
    Integer finalRetain = retain;

    if (finalRetain < 1) {
      throw new RuntimeException("retain must be at least 1");
    }

    withLock(() -> {

      on(commander().hosts(), host -> {

        host.execute(commander().auditor().record("Pruned containers"));
        host.execute(prune.appContainers(finalRetain));
        host.execute(prune.healthcheckContainers());

      });

    });

  }
}
