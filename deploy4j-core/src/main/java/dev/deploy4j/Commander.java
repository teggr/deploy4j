package dev.deploy4j;

import dev.deploy4j.cli.app.Boot;
import dev.deploy4j.commands.*;
import dev.deploy4j.configuration.Accessory;
import dev.deploy4j.configuration.Configuration;
import dev.deploy4j.configuration.Role;
import dev.deploy4j.ssh.SshHost;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Context class to hold shared information and configurations for deployment processes.
 */
public class Commander implements AutoCloseable {

  private final Configuration config;

  private final Docker docker;
  private final Server server;
  private final Registry registry;
  private final Builder builder;
  private final Traefik traefik;
  private final Prune prune;

  private final List<String> specificHosts = new ArrayList<>();
  private final List<String> specificRoles = new ArrayList<>();

  private final Map<String, SshHost> sshHosts = new HashMap<>();
  private final Specifics specifics;

  public Commander(Configuration config) {
    this.config = config;
    this.specifics = new Specifics(config, List.of(), List.of()); // TODT hosts and roles

    this.docker = new Docker(config);
    this.server = new Server(config);
    this.registry = new Registry(config);
    this.builder = new Builder(config);
    this.traefik = new Traefik(config);
    this.prune = new Prune(config);

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
    sshHosts.values().forEach(SshHost::close);

  }

  public App app(Role role, String host) {
    return new App(config, role, host);
  }

  // accesssory
  // auditor

  public Builder builder() {
    return builder;
  }

  public Docker docker() {
    return docker;
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
    return new Boot(this, config, host, role, version); // barrier?
  }

  public SshHost host(String host) {
    SshHost sshHost = sshHosts.get(host);
    if (sshHost == null) {
      sshHost = new SshHost(host, config.ssh());
      sshHosts.put(host, sshHost);
    }
    return sshHost;
  }

  public List<Role> rolesOn(String host) {
    return specifics.rolesOn(host);
  }

  public Prune prune() {
    return prune;
  }

  public List<String> accessoryNames() {
    return config.accessories().stream()
      .map(Accessory::name)
      .toList();
  }

  public List<String> specificHosts() {
    return specificHosts;
  }
}
