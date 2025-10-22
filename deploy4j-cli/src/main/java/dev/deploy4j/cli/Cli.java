package dev.deploy4j.cli;

import dev.deploy4j.deploy.cli.*;

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

    this.app = new App( commander);
    this.server = new Server( commander);
    this.env = new Env(commander);
    this.accessory = new Accessory(commander);
    this.registry = new Registry( commander);
    this.build = new Build( commander);
    this.prune = new Prune(commander);
    this.environment = environment;
    this.traefik = new Traefik(commander);
    this.lock = new Lock(commander);
    this.main = new Main(commander, app, server, env, accessory, registry, build, prune, environment, traefik );

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
