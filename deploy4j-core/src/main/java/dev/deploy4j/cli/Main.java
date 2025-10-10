package dev.deploy4j.cli;

import dev.deploy4j.Commander;

public class Main {

  private final Cli cli;
  private final Commander commander;

  public Main(Cli cli, Commander commander) {
    this.cli = cli;
    this.commander = commander;
  }

  /**
   * Setup all accessories, push the env, and deploy app to servers
   */
  public void setup() {

    long start = System.currentTimeMillis();

    try {

      cli.server().bootstrap();

      // envify()
      // context.getEnv().push();
      // context.accesssory().boot();

      deploy();

    } finally {

      long end = System.currentTimeMillis();

      System.out.println("=================================");
      System.out.println("Deployed in " + (end - start) / 1000 + " seconds");

    }

  }

  /**
   * Deploy the app to servers
   */
  public void deploy() {

    cli.registry().login();
    cli.build().pull();
    // ensure Traefik is running...
    cli.traefik().boot();
    // Detect stale containers...
    cli.app().staleContainers();
    cli.app().boot();
    cli.prune().all();

  }

}
