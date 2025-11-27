package dev.deploy4j.deploy;

import dev.deploy4j.deploy.configuration.ConfigurationPrinter;
import dev.deploy4j.deploy.configuration.Role;
import dev.deploy4j.deploy.host.commands.AppHostCommandsFactory;
import dev.deploy4j.deploy.host.ssh.SshHosts;
import dev.deploy4j.deploy.local.LocalHost;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;


public class Deploy extends Base {

  private static final Logger log = LoggerFactory.getLogger(Deploy.class);

  private final LockManager lockManager;
  private final App app;
  private final Server server;
  private final Accessory accessory;
  private final Registry registry;
  private final Build build;
  private final Prune prune;
  private final Traefik traefik;
  private final AppHostCommandsFactory apps;

  public Deploy(SshHosts sshHosts, Hooks hooks, LocalHost localHost, LockManager lockManager, App app, Server server, Accessory accessory, Registry registry, Build build, Prune prune, Traefik traefik, AppHostCommandsFactory apps) {
    super(sshHosts, hooks, localHost);
    this.lockManager = lockManager;
    this.app = app;
    this.server = server;
    this.accessory = accessory;
    this.registry = registry;
    this.build = build;
    this.prune = prune;
    this.traefik = traefik;
    this.apps = apps;
  }

  /**
   * Setup all accessories, push the env, and deploy app to servers
   */
  public void setup(DeployContext deployContext) {

    lockManager.withLock(deployContext, () -> {

      log.info("Ensure Docker is installed...");
      server.bootstrap(deployContext);

      deploy(deployContext, false, true);

    });

  }

  /**
   * Deploy the app to servers
   *
   * @param skipPull Skip image pull
   *                 @param bootAccessories Whether to boot accessories before deploying the app
   */
  public void deploy(DeployContext deployContext, boolean skipPull, boolean bootAccessories) {

    if (skipPull) {
      log.info("Skip pulling app image as requested.");
    } else {
      log.info("Pull app image...");
      build.pull(deployContext);
    }

    lockManager.withLock(deployContext, () -> {

      runHook(deployContext, "pre-deploy");

      log.info("Ensure Traefik is running...");
      traefik.boot(deployContext);

      if (bootAccessories) {
        accessory.boot(deployContext, "all", true);
      }

      log.info("Detect stale containers...");
      app.staleContainers(deployContext);

      app.boot(deployContext);

      log.info("Prune old containers and images...");
      prune.all(deployContext);

    });

    runHook(deployContext, "post-deploy");

  }

  /**
   * Deploy app to servers without bootstrapping servers, starting Traefik, pruning, and registry login
   *
   * @param skipPull Skip image pull
   */
  public void redeploy(DeployContext deployContext, boolean skipPull) {

    if (skipPull) {
      log.info("Skip pulling app image as requested.");
    } else {
      log.info("Pull app image...");
      build.pull(deployContext);
    }

    lockManager.withLock(deployContext, () -> {

      runHook(deployContext, "pre-deploy");

      log.info("Detect stale containers...");
      app.staleContainers(deployContext);

      app.boot(deployContext);

    });

    runHook(deployContext, "post-deploy");

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

        runHook(deployContext, "pre-deploy");

        app.boot(deployContext);
        rolledBack = true;
      } else {
        System.err.println("The app version '%s' is not available as a container (use 'deploy4j app containers' for available versions)".formatted(version));
      }

    });

    runHook(deployContext, "post-deploy");

  }

  /**
   * Show details about all containers
   */
  public void details(DeployContext deployContext) {
    traefik.details(deployContext);
    app.details(deployContext);
    accessory.details(deployContext, "all", false);
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

      on(deployContext, deployContext.appHosts(), host -> {

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

  public void test(DeployContext deployContext) {

    on(deployContext, deployContext.hosts(), host -> {

      log.info("Testing connectivity to " + host.hostName() + "...");
      host.execute(server.test());
      log.info("Connected to " + host.hostName() + " successfully.");

    });

  }
}
