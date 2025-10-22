package dev.deploy4j.deploy.cli;

import dev.deploy4j.deploy.configuration.Role;
import dev.deploy4j.deploy.utils.erb.ERB;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;


public class Main extends Base {

  private static final Logger log = LoggerFactory.getLogger(Main.class);

  private final App app;
  private final Server server;
  private final Env env;
  private final Accessory accessory;
  private final Registry registry;
  private final Build build;
  private final Prune prune;
  private final Environment environment;
  private final Traefik traefik;

  public Main(Commander commander, App app, Server server, Env env, Accessory accessory, Registry registry, Build build, Prune prune, Environment environment, Traefik traefik) {
    super(commander);
    this.app = app;
    this.server = server;
    this.env = env;
    this.accessory = accessory;
    this.registry = registry;
    this.build = build;
    this.prune = prune;
    this.environment = environment;
    this.traefik = traefik;
  }

  /**
   * Setup all accessories, push the env, and deploy app to servers
   *
   * @param skipPush Skip image build and push
   */
  public void setup(boolean skipPush) {

    withLock(() -> {

      System.out.println("Ensure Docker is installed...");
      server.bootstrap();

      System.out.println("Evaluate and push env files...");
      envify(false, null);
      env.push();

      accessory.boot("all", true);

      deploy(skipPush);

    });


  }

  /**
   * Deploy the app to servers
   *
   * @param skipPush Skip image build and push
   */
  public void deploy(boolean skipPush) {

    System.out.println("Log into image registry...");
    registry.login();

    if (skipPush) {
      System.out.println("Pull app image...");
      build.pull();
    }

    withLock(() -> {

      System.out.println("Ensure Traefik is running...");
      traefik.boot();

      System.out.println("Detect stale containers...");
      app.staleContainers();

      app.boot();

      System.out.println("Prune old containers and images...");
      prune.all();

    });

  }

  /**
   * Deploy app to servers without bootstrapping servers, starting Traefik, pruning, and registry login
   *
   * @param skipPush Skip image build and push
   */
  public void redeploy(boolean skipPush) {

    if (skipPush) {
      System.out.println("Pull app image...");
      build.pull();
    }

    withLock(() -> {

      System.out.println("Detect stale containers...");
      app.staleContainers();

      app.boot();

    });


  }

  /**
   * Rollback app to VERSION
   */
  public void rollback(String version) {

    withLock(() -> {

      boolean rolledBack = false;

      commander().config().version(version);
      String oldVersion = null;

      if (containerAvailable(version)) {

        app.boot();
        rolledBack = true;
      } else {
        System.err.println("The app version '%s' is not available as a container (use 'deploy4j app containers' for available versions)".formatted(version));
      }

    });

  }

  /**
   * Show details about all containers
   */
  public void details() {
    traefik.details();
    app.details();
    accessory.details("all");
  }

  /**
   * Show audit log from servers
   */
  public void audit() {
    on(commander().hosts(), host -> {
      System.out.println(host.capture(commander().auditor().reveal()));
    });
  }

  /**
   * Show combined config (including secrets!)
   */
  public void config() {
    // TODO: run locallu
    System.out.println(commander().config());
  }

  // TODO: docs

  /**
   * Create config stub in config/deploy.yml and env stub in .env
   */
  public void init(boolean bundle) {

    File deployFile = new File("config/deploy.yml");
    if (deployFile.exists()) {
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
    if (!deployFile.exists()) {
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
   * @param skipPush    Skip .env file push
   * @param destination
   */
  public void envify(boolean skipPush, String destination) {

    String envTemplatePath;
    String envPath;
    if (destination != null) {
      envTemplatePath = ".env.%s.thyme".formatted(destination);
      envPath = ".env.%s".formatted(destination);
    } else {
      envTemplatePath = ".env.thyme";
      envPath = ".env";
    }

    File envTemplateFile = new File(envTemplatePath);
    if (envTemplateFile.exists()) {
      String content = environment
        .withOriginalEnv(() -> new ERB(envTemplateFile).result());
      try {
        FileUtils.writeStringToFile(new File(envPath), content, StandardCharsets.UTF_8);
      } catch (IOException e) {
        throw new RuntimeException(e);
      }

      if (skipPush) {
        environment.reloadEnv();
        env.push();
      }

    } else {
      System.out.println("Skipping envify (no " + envTemplatePath + " exists)");
    }

  }


  /**
   * Remove Traefik, app, accessories, and registry session from servers
   */
  public void remove() {

    withLock(() -> {

      traefik.remove();
      app.remove();
      accessory.remove("all");
      registry.logout();

    });

  }

  public void version() {
    System.out.println(Version.VERSION);
  }

  // private

  private boolean containerAvailable(String version) {

    try {

      on(commander().hosts(), host -> {

        for (Role role : commander().rolesOn(host.hostName())) {

          String containerId = host.capture(commander().app(role, host.hostName()).containerIdForVersion(version));
          if (containerId == null) {
            throw new RuntimeException("Container not found");
          }

        }


      });

    } catch (RuntimeException e) {
      if (e.getMessage().equalsIgnoreCase("Container not found")) {
        System.err.println("Error looking for container version %s: %s".formatted(version, e.getMessage()));
        return false;
      } else {
        throw e;
      }
    }

    return true;
  }

  private Map<String, String> deployOptions() {
    return Map.of(
      "version", commander().config().version()
    );
    // TODO: merge with options
  }

}
