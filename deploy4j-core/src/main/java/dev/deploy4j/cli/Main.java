package dev.deploy4j.cli;

import dev.deploy4j.Commander;

public class Main implements AutoCloseable {

  private final Cli cli;
  private final Commander context;

  public Main(Cli cli, Commander context) {
    this.cli = cli;
    this.context = context;
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
//    context.prune().all();

  }


  @Override
  public void close() throws Exception {

    context.close();

  }

}
