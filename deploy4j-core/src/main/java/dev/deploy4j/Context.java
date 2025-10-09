package dev.deploy4j;

import dev.deploy4j.commands.*;
import dev.deploy4j.config.Traefik;
import dev.deploy4j.configuration.Deploy4jConfig;
import dev.deploy4j.configuration.ServerConfig;

import java.util.ArrayList;
import java.util.List;

/**
 * Context class to hold shared information and configurations for deployment processes.
 */
public class Context implements AutoCloseable {

  private final Deploy4jConfig config;
  private final Server server;
  private final Registry registry;
  private final Build build;

  private final List<Host> hosts = new ArrayList<>();
  private final Traefik traefik;
  private final App app;
  private final Prune prune;

  private final DockerCommands dockerCommands;
  private final ServerCommands serverCommands;
  private final RegistryCommands registryCommands;
  private final BuilderCommands builderCommands;
  private final TraefikCommands traefikCommands;

  public Context(Deploy4jConfig config) {
    this.config = config;

    // create the hosts
    for (ServerConfig serverConfig : config.servers()) {
      hosts.add(new Host(serverConfig.host(), serverConfig.role(), config));
    }

    this.server = new Server(this);
    this.registry = new Registry(this);
    this.build = new Build(this);
    this.traefik = new Traefik(this);
    this.app = new App(this);
    this.prune = new Prune(this);

    this.dockerCommands =  new DockerCommands(config);
    this.serverCommands = new ServerCommands(config);
    this.registryCommands = new RegistryCommands(config);
    this.builderCommands = new BuilderCommands(config);
    this.traefikCommands = new TraefikCommands(config);
  }

  public Server server() {
    return server;
  }

  public List<Host> hosts() {
    return hosts;
  }

  public Deploy4jConfig getConfig() {
    return config;
  }

  @Override
  public void close() throws Exception {

    // shutdown
    hosts.forEach(Host::close);

  }

  public Registry registry() {
    return registry;
  }

  public Build build() {
    return build;
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

  public DockerCommands dockerCmds() {
    return dockerCommands;
  }

  public ServerCommands serverCmds() {
    return serverCommands;
  }

  public RegistryCommands registryCommands() {
    return registryCommands;
  }

  public BuilderCommands builderCommands() {
    return builderCommands;
  }

  public List<Host> traefikHosts() {
    return hosts.stream()
      .filter( Host::runningTraefik )
      .toList();
  }

  public TraefikCommands traefikCommands() {
    return traefikCommands;
  }

  public AppCommands appCommands(Host host) {
    return new AppCommands( config, host );
  }

}
