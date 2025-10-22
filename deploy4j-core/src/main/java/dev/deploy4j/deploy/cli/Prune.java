package dev.deploy4j.deploy.cli;

public class Prune extends Base {

  public Prune(Commander commander) {
    super(commander);
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
        host.execute(commander().prune().danglingImages());
        host.execute(commander().prune().taggedImages());


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
        host.execute(commander().prune().appContainers(finalRetain));
        host.execute(commander().prune().healthcheckContainers());

      });

    });

  }
}
