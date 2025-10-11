package dev.deploy4j.cli;

import dev.deploy4j.Commander;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;


public class Main {

  private static final Logger log = LoggerFactory.getLogger(Main.class);

  private final Cli cli;
  private final Commander commander;

  public Main(Cli cli, Commander commander) {
    this.cli = cli;
    this.commander = commander;
  }

  /**
   * Setup all accessories, push the env, and deploy app to servers
   *
   * @param skipPush Skip image build and push
   */
  public void setup(boolean skipPush) {

    long start = System.currentTimeMillis();

    try {

      cli.server().bootstrap();

      envify(skipPush);
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
   * Create .env by evaluating .env.erb (or .env.staging.erb -> .env.staging when using -d staging)
   *
   * @param skipPush Skip .env file push
   */
  public void envify(boolean skipPush) {

    String envTemplatePath = ".env.thyme";
    String envPath = ".env";
    if( commander.getConfig().destination() != null ) {
      envTemplatePath = ".env.%s.thyme".formatted(commander.getConfig().destination());
      envPath = ".env.%s".formatted(commander.getConfig().destination());
    }

    File envTemplate = new File(envTemplatePath);
    boolean exists = envTemplate.exists();

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
