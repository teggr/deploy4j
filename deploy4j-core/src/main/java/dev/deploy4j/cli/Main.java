package dev.deploy4j.cli;

import dev.deploy4j.Commander;
import dev.deploy4j.Version;
import dev.deploy4j.configuration.Role;
import dev.deploy4j.env.ENV;
import dev.deploy4j.erb.ERB;
import dev.deploy4j.ssh.SshHost;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;


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

      System.out.println("Ensure Docker is installed...");
      cli.server().bootstrap();

      cli.accesssory().boot();

      deploy(skipPush);

  }

  /**
   * Deploy the app to servers
   *
   * @param skipPush Skip image build and push
   */
  public void deploy(boolean skipPush) {

    System.out.println("Log into image registry...");
    cli.registry().login();

    if(skipPush) {
      System.out.println("Pull app image...");
      cli.build().pull();
    }

    System.out.println("Ensure Traefik is running...");
    cli.traefik().boot();

    System.out.println("Detect stale containers...");
    cli.app().staleContainers();

    cli.app().boot();

    System.out.println("Prune old containers and images...");
    cli.prune().all();

  }

  /**
   * Deploy app to servers without bootstrapping servers, starting Traefik, pruning, and registry login
   *
   * @param skipPush Skip image build and push
   */
  public void redeploy(boolean skipPush) {

    if(skipPush) {
      System.out.println("Pull app image...");
      cli.build().pull();
    }

    System.out.println("Detect stale containers...");
    cli.app().staleContainers();

    cli.app().boot();

  }

  /**
   * Rollback app to VERSION
   */
  public void rollback(String version) {

    boolean rolledBack = false;

     commander.config().setVersion(version);
     String oldVersion = null;

     if(containerAvailable(version)) {

       cli.app().boot();
       rolledBack = true;
     } else {
       System.err.println("The app version '%s' is not available as a container (use 'deploy4j app containers' for available versions)".formatted( version ));
     }

  }

  /**
   * Show details about all containers
   */
  public void details() {
    cli.traefik().details();
    cli.app().details();
    cli.accesssory().details("all");
  }

  /**
   * Show audit log from servers
   */
  public void audit() {
    for( SshHost host : cli.on(commander.hosts()) ) {
      System.out.println(host.capture(commander.auditor().reveal()));
    }
  }

  /**
   * Show combined config (including secrets!)
   */
  public void config() {
    System.out.println(commander.config());
  }


  /**
   * Create config stub in config/deploy.yml and env stub in .env
   */
  public void init(boolean bundle) {

    File deployFile = new File("config/deploy.yml");
    if(deployFile.exists()) {
      System.out.println("Config file already exists in config/deploy.yml (remove first to create a new one)");
    } else {
      deployFile.getParentFile().mkdirs();
      try {
        FileUtils.copyInputStreamToFile(
          getClass().getClassLoader().getResourceAsStream("templates/deploy.yml"),
          deployFile
        );
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
      System.out.println("Created configuration file in config/deploy.yml");
    }

    deployFile = new File(".env");
    if(!deployFile.exists()) {
      try {
        FileUtils.copyInputStreamToFile(
          getClass().getClassLoader().getResourceAsStream("templates/template.env"),
          deployFile
        );
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
      System.out.println("Created .env file");
    }

    // TODO: hooks

    // TODO: bundle add maven dependency?

  }

  /**
   * Create .env by evaluating .env.thyme (or .env.staging.thyme -> .env.staging when using -d staging)
   *
   * @param skipPush Skip .env file push
   * @param destination
   */
  public void envify(boolean skipPush, String destination) {

      String envTemplatePath;
      String envPath;
    if(destination != null) {
      envTemplatePath = ".env.%s.thyme".formatted(destination);
      envPath = ".env.%s".formatted(destination);
    } else {
      envTemplatePath = ".env.thyme";
      envPath = ".env";
    }

    File envTemplateFile = new File(envTemplatePath);
    if(envTemplateFile.exists()) {
      String content = cli.environment()
        .withOriginalEnv(() -> new ERB( envTemplateFile ).result() );
      try {
        FileUtils.writeStringToFile( new File(envPath), content, StandardCharsets.UTF_8);
      } catch (IOException e) {
        throw new RuntimeException(e);
      }

      if(skipPush) {
        cli.environment().reloadEnv();
        cli.env().push();
      }

    } else {
      System.out.println("Skipping envify (no "+ envTemplatePath +" exists)");
    }

  }


  /**
   * Remove Traefik, app, accessories, and registry session from servers
   */
  public void remove() {

    cli.traefik().remove();
    cli.app().remove();
    cli.accesssory().remove("all");
    cli.registry().logout();

  }

  public void version() {
    System.out.println( Version.VERSION );
  }

  private boolean containerAvailable(String version) {

    try {

      for (SshHost host : cli.on(commander.hosts())) {

        for (Role role : commander.rolesOn(host.hostName())) {

          String containerId = host.capture(commander.app(role, host.hostName()).containerIdForVersion(version));
          if (containerId == null) {
            throw new RuntimeException("Container not found");
          }

        }

      }

    } catch (RuntimeException e) {
      if( e.getMessage().equalsIgnoreCase( "Container not found" ) ) {
        System.err.println("Error looking for container version %s: %s".formatted( version, e.getMessage() ));
        return false;
      } else {
        throw e;
      }
    }

    return true;
  }

}
