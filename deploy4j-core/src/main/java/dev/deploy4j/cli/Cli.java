package dev.deploy4j.cli;

import dev.deploy4j.Commander;
import dev.deploy4j.ssh.SshHost;

import java.util.List;

public class Cli {

  private final Main main;
  private final Server server;
  private final Registry registry;
  private final Build build;
  private final Traefik traefik;
  private final App app;
  private final Prune prune;
  private final Accessory accesssory;

  private final Commander commander;
  private final Lock lock;

  public Cli(Commander commander) {

    this.main = new Main(this, commander);
    this.server = new Server(this, commander);
    this.registry = new Registry(this, commander);
    this.build = new Build(this, commander);

    this.traefik = new Traefik(this, commander);
    this.app = new App(this, commander);
    this.prune = new Prune(this, commander);
    this.accesssory = new Accessory(this, commander);

    this.lock = new Lock(this, commander);

    this.commander = commander;

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

  public Accessory accesssory() {
    return accesssory;
  }

  public List<SshHost> on(List<String> hosts) {

    return hosts.stream()
      .map(h -> commander.host( h ) )
      .toList();

  }

  public Lock lock() {
    return lock;
  }
}
