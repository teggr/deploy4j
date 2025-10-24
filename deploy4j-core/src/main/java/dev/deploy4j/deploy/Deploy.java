package dev.deploy4j.deploy;

import dev.deploy4j.deploy.configuration.ConfigurationPrinter;
import dev.deploy4j.deploy.configuration.Role;
import dev.deploy4j.deploy.host.commands.AppHostCommandsFactory;
import dev.deploy4j.deploy.host.ssh.SshHosts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;


public class Deploy extends Base {

  private static final Logger log = LoggerFactory.getLogger(Deploy.class);

  private final LockManager lockManager;
  private final App app;
  private final Server server;
  private final Env env;
  private final Accessory accessory;
  private final Registry registry;
  private final Build build;
  private final Prune prune;
  private final Traefik traefik;
  private final AppHostCommandsFactory apps;

  public Deploy(SshHosts sshHosts, LockManager lockManager, App app, Server server, Env env, Accessory accessory, Registry registry, Build build, Prune prune, Traefik traefik, AppHostCommandsFactory apps) {
    super(sshHosts);
    this.lockManager = lockManager;
    this.app = app;
    this.server = server;
    this.env = env;
    this.accessory = accessory;
    this.registry = registry;
    this.build = build;
    this.prune = prune;
    this.traefik = traefik;
    this.apps = apps;
  }

  /**
   * Setup all accessories, push the env, and deploy app to servers
   *
   * @param skipPush Skip image build and push
   */
  public void setup(DeployContext deployContext, boolean skipPush) {

    lockManager.withLock(deployContext, () -> {

      System.out.println("Ensure Docker is installed...");
      server.bootstrap(deployContext);

      System.out.println("Evaluate and push env files...");
      env.envify(deployContext, false, null);
      env.push(deployContext);

      accessory.boot(deployContext, "all", true);

      deploy(deployContext, skipPush);

    });

  }

  /**
   * Deploy the app to servers
   *
   * @param skipPush Skip image build and push
   */
  public void deploy(DeployContext deployContext, boolean skipPush) {

    System.out.println("Log into image registry...");
    registry.login(deployContext);

    if (skipPush) {
      System.out.println("Pull app image...");
      build.pull(deployContext);
    }

    lockManager.withLock(deployContext, () -> {

      System.out.println("Ensure Traefik is running...");
      traefik.boot(deployContext);

      System.out.println("Detect stale containers...");
      app.staleContainers(deployContext);

      app.boot(deployContext);

      System.out.println("Prune old containers and images...");
      prune.all(deployContext);

    });

  }

  /**
   * Deploy app to servers without bootstrapping servers, starting Traefik, pruning, and registry login
   *
   * @param skipPush Skip image build and push
   */
  public void redeploy(DeployContext deployContext, boolean skipPush) {

    if (skipPush) {
      System.out.println("Pull app image...");
      build.pull(deployContext);
    }

    lockManager.withLock(deployContext, () -> {

      System.out.println("Detect stale containers...");
      app.staleContainers(deployContext);

      app.boot(deployContext);

    });


  }

  /**
   * Rollback app to VERSION
   */
  public void rollback(DeployContext deployContext, String version) {

    lockManager.withLock(deployContext, () -> {

      boolean rolledBack = false;

      deployContext.config().version(version);
      String oldVersion = null;

      if (containerAvailable(deployContext, version)) {

        app.boot(deployContext);
        rolledBack = true;
      } else {
        System.err.println("The app version '%s' is not available as a container (use 'deploy4j app containers' for available versions)".formatted(version));
      }

    });

  }

  /**
   * Show details about all containers
   */
  public void details(DeployContext deployContext) {
    traefik.details(deployContext);
    app.details(deployContext);
    accessory.details(deployContext, "all");
  }

  /**
   * Show combined config (including secrets!)
   */
  public void config(DeployContext deployContext) {

    ConfigurationPrinter configurationPrinter = new ConfigurationPrinter();
    configurationPrinter.print(deployContext.config());

  }

  /**
   * Remove Traefik, app, accessories, and registry session from servers
   */
  public void remove(DeployContext deployContext) {

    lockManager.withLock(deployContext, () -> {

      traefik.remove(deployContext);
      app.remove(deployContext);
      accessory.remove(deployContext, "all");
      registry.logout(deployContext);

    });

  }

  // private

  private boolean containerAvailable(DeployContext deployContext, String version) {

    try {

      on(deployContext.hosts(), host -> {

        for (Role role : deployContext.rolesOn(host.hostName())) {

          String containerId = host.capture(apps.app(role, host.hostName()).containerIdForVersion(version));
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

  private Map<String, String> deployOptions(DeployContext deployContext) {
    return Map.of(
      "version", deployContext.config().version()
    );
    // TODO: merge with options
  }

}
