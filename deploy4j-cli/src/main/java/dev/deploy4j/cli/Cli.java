package dev.deploy4j.cli;

import dev.deploy4j.deploy.*;
import dev.deploy4j.deploy.host.commands.*;

public class Cli {

  private final Environment environment;
  private final Main main;
  private final Server server;
  private final Registry registry;
  private final Build build;
  private final Traefik traefik;
  private final App app;
  private final Prune prune;
  private final Accessory accessory;
  private final Lock lock;
  private final Env env;

  public Cli(Environment environment, Commander commander) {

    BuilderHostCommands builder = new BuilderHostCommands(commander.config());

    DockerHostCommands docker = new DockerHostCommands(commander.config());

    HealthcheckHostCommands healthcheck = new HealthcheckHostCommands(commander.config());

    HookHostCommands hook = new HookHostCommands(commander.config());

    LockHostCommands lock = new LockHostCommands(commander.config());

    PruneHostCommands prune = new PruneHostCommands(commander.config());

    RegistryHostCommands registry = new RegistryHostCommands(commander.config());

    ServerHostCommands server = new ServerHostCommands(commander.config());

    TraefikHostCommands traefik = new TraefikHostCommands(commander.config());

    this.app = new App(commander);
    this.server = new Server(commander, docker, server);
    this.env = new Env(commander, traefik);
    this.accessory = new Accessory(commander, registry);
    this.registry = new Registry(commander, registry);
    this.build = new Build(commander, builder);
    this.prune = new Prune(commander, prune);
    this.environment = environment;
    this.traefik = new Traefik(commander, registry, traefik);
    this.lock = new Lock(commander, server, lock);
    this.main = new Main(commander, this.app, this.server, this.env, this.accessory, this.registry, build, this.prune, this.environment, this.traefik);

  }

  public Env env() {
    return env;
  }

  public Environment environment() {
    return environment;
  }

  public Server server() {
    return server;
  }

  public Registry registry() {
    return registry;
  }

  public Build build() {
    return build;
  }

  public Main main() {
    return main;
  }

  public Traefik traefik() {
    return traefik;
  }

  public App app() {
    return app;
  }

  public Prune prune() {
    return prune;
  }

  public Accessory accessory() {
    return accessory;
  }

  public Lock lock() {
    return lock;
  }

}
