package dev.deploy4j.cli;

import dev.deploy4j.Commander;
import dev.deploy4j.Environment;

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

    this.environment = environment;

    this.main = new Main(this, commander);
    this.server = new Server(this, commander);
    this.registry = new Registry(this, commander);
    this.build = new Build(this, commander);

    this.traefik = new Traefik(this, commander);
    this.app = new App(this, commander);
    this.prune = new Prune(this, commander);
    this.accessory = new Accessory(this, commander);

    this.lock = new Lock(this, commander);
    this.env = new Env(this,commander);

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
