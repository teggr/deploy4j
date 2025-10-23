package dev.deploy4j.deploy;

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
  public void setup(Commander commander, boolean skipPush) {

    lockManager.withLock(commander, () -> {

      System.out.println("Ensure Docker is installed...");
      server.bootstrap(commander);

      System.out.println("Evaluate and push env files...");
      env.envify(commander, false, null);
      env.push(commander);

      accessory.boot(commander, "all", true);

      deploy(commander, skipPush);

    });

  }

  /**
   * Deploy the app to servers
   *
   * @param skipPush Skip image build and push
   */
  public void deploy(Commander commander, boolean skipPush) {

    System.out.println("Log into image registry...");
    registry.login(commander);

    if (skipPush) {
      System.out.println("Pull app image...");
      build.pull(commander);
    }

    lockManager.withLock(commander, () -> {

      System.out.println("Ensure Traefik is running...");
      traefik.boot(commander);

      System.out.println("Detect stale containers...");
      app.staleContainers(commander);

      app.boot(commander);

      System.out.println("Prune old containers and images...");
      prune.all(commander);

    });

  }

  /**
   * Deploy app to servers without bootstrapping servers, starting Traefik, pruning, and registry login
   *
   * @param skipPush Skip image build and push
   */
  public void redeploy(Commander commander, boolean skipPush) {

    if (skipPush) {
      System.out.println("Pull app image...");
      build.pull(commander);
    }

    lockManager.withLock(commander, () -> {

      System.out.println("Detect stale containers...");
      app.staleContainers(commander);

      app.boot(commander);

    });


  }

  /**
   * Rollback app to VERSION
   */
  public void rollback(Commander commander, String version) {

    lockManager.withLock(commander, () -> {

      boolean rolledBack = false;

      commander.config().version(version);
      String oldVersion = null;

      if (containerAvailable(commander, version)) {

        app.boot(commander);
        rolledBack = true;
      } else {
        System.err.println("The app version '%s' is not available as a container (use 'deploy4j app containers' for available versions)".formatted(version));
      }

    });

  }

  /**
   * Show details about all containers
   */
  public void details(Commander commander) {
    traefik.details(commander);
    app.details(commander);
    accessory.details(commander, "all");
  }

  /**
   * Show combined config (including secrets!)
   */
  public void config(Commander commander) {
    // TODO: run locallu
    System.out.println(commander.config());
  }

  // TODO: docs




  /**
   * Remove Traefik, app, accessories, and registry session from servers
   */
  public void remove(Commander commander) {

    lockManager.withLock(commander, () -> {

      traefik.remove(commander);
      app.remove(commander);
      accessory.remove(commander, "all");
      registry.logout(commander);

    });

  }

  // private

  private boolean containerAvailable(Commander commander, String version) {

    try {

      on(commander.hosts(), host -> {

        for (Role role : commander.rolesOn(host.hostName())) {

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

  private Map<String, String> deployOptions(Commander commander) {
    return Map.of(
      "version", commander.config().version()
    );
    // TODO: merge with options
  }

}
