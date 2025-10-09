package dev.deploy4j;

import dev.deploy4j.commands.*;
import dev.deploy4j.configuration.Configuration;
import dev.deploy4j.configuration.Role;
import dev.deploy4j.ssh.SshHost;

import java.util.List;

/**
 * Context class to hold shared information and configurations for deployment processes.
 */
public class Commander implements AutoCloseable {

  private final Configuration config;

  private final Docker dockerCommands;
  private final Server server;
  private final Registry registry;
  private final Builder builder;
  private final Traefik traefik;
  private final Specifics specifics;

  public Commander(Configuration config) {
    this.config = config;
    this.specifics = new Specifics(config, List.of(), List.of()); // TODT hosts and roles

    this.dockerCommands =  new Docker(config);
    this.server = new Server(config);
    this.registry = new Registry(config);
    this.builder = new Builder(config);
    this.traefik = new Traefik(config);
  }

  private Specifics specifics() {
    return specifics;
  }

  public List<String> hosts() {
    return specifics().hosts();
  }

  public List<String> accessoryHosts() {
    return specifics().accessoryHosts();
  }

  public Configuration getConfig() {
    return config;
  }

  @Override
  public void close() throws Exception {

    // shutdown
   // hosts.forEach(Host::close);

  }

  public App app(Role role, String host) {
    return new App( config, role, host );
  }

  // accesssory
  // auditor

  public Builder builder() {
    return builder;
  }

  public Docker docker() {
    return dockerCommands;
  }

  // healthcheck
  // hook
  // lock
  // prune

  public Registry registry() {
    return registry;
  }

  public Server server() {
    return server;
  }

  public Traefik traefik() {
    return traefik;
  }

  public List<String> traefikHosts() {
    return specifics.traefikHosts();
  }

  public Boot boot(SshHost host, Role role, String version) {
    return new Boot( this, config, host, role, version ); // barrier?
  }

  public SshHost host(String host) {
    return  new SshHost( host, config.ssh() );
  }

  public List<Role> rolesOn(String host) {
    return specifics.rolesOn(host);
  }

}
