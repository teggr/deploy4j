package dev.deploy4j;

import dev.deploy4j.cli.app.Boot;
import dev.deploy4j.commands.*;
import dev.deploy4j.configuration.Accessory;
import dev.deploy4j.configuration.Configuration;
import dev.deploy4j.configuration.ConfigureArgs;
import dev.deploy4j.configuration.Role;
import dev.deploy4j.ssh.SshHost;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Context class to hold shared information and configurations for deployment processes.
 */
public class Commander implements AutoCloseable {

  private ConfigureArgs configureArgs;

  private boolean holdingLock;
  private boolean connected;

  private Configuration config;

  private List<Role> specificRoles;
  private List<String> specificHosts;

  private  Builder builder;
  private  Docker docker;
  private Healthcheck healthcheck;
  private Hook hook;
  private  Lock lock;
  private  Prune prune;
  private  Registry registry;
  private  Server server;
  private  Traefik traefik;

  private Specifics specifics;

  private final Map<String, SshHost> sshHosts = new HashMap<>();

  public Commander() {
    this.holdingLock = false;
    this.connected = false;
    this.specifics = null;
  }

  public Configuration config() {
    if (config == null) {
      config = Configuration.createFrom(configureArgs);
      configureArgs = null;
    }
    return config;
  }

  public void configure(String configFile, String destination, String version) {
    this.config = null;
    this.configureArgs = new ConfigureArgs(configFile, destination, version);
  }

  public void specificPrimary(Boolean primary) {
    specifics = null;
    specificHosts = List.of(config().primaryHost());
  }

  public void specificRoles(String[] roleNames) {
    specifics = null;
    if (roleNames != null) {
      specificRoles = Utils.filterSpecificItems(roleNames, config().roles());
      if (specificRoles.isEmpty()) {
        throw new RuntimeException("No --roles match for " + String.join(",", roleNames));
      }
    }
  }

  public List<Role> specificRoles() {
    return specificRoles;
  }

  public void specificHosts(String[] hosts) {
    specifics = null;
    if (hosts != null) {
      specificHosts = Utils.filterSpecificItems(hosts, config().allHosts());
      if (specificHosts.isEmpty()) {
        throw new RuntimeException("No --hosts match for " + String.join(",", hosts));
      }
    }
  }

  public List<String> specificHosts() {
    return specificHosts;
  }

  public void withSpecificHosts(List<String> hosts, Runnable runnable) {
      List<String> originalHosts = specificHosts;
      specificHosts = hosts;
    try {
      runnable.run();
    } finally {
      specificHosts = originalHosts;
    }
  }

  public List<String> accessoryNames() {
    return config().accessories().stream()
      .map(Accessory::name)
      .toList();
  }

  public List<String> accessoriesOn(String host) {
    return config().accessories().stream()
      .filter(accessory -> accessory.hosts().contains(host))
      .map(Accessory::name)
      .toList();
  }

  public App app(Role role, String host) {
    return new App(config, role, host);
  }

  public dev.deploy4j.commands.Accessory accessory(String name) {
    return new dev.deploy4j.commands.Accessory(config, name);
  }

  public Auditor auditor() {
    return auditor(null);
  }

  public Auditor auditor(Map<String, String> details) {
    return new Auditor(config(), details);
  }

  public Builder builder() {
    if(builder == null) {
      builder = new Builder(config());
    }
    return builder;
  }

  public Docker docker() {
    if(docker == null) {
      docker = new Docker(config());
    }
    return docker;
  }

  public Healthcheck healthcheck() {
    if(healthcheck == null) {
      healthcheck = new Healthcheck(config());
    }
    return healthcheck;
  }

  public Hook hook() {
    if(hook == null) {
      hook = new Hook(config());
    }
    return hook;
  }

  public Lock lock() {
    if(lock == null) {
      lock = new Lock(config());
    }
    return lock;
  }

  public Prune prune() {
    if(prune == null) {
      prune = new Prune(config());
    }
    return prune;
  }

  public Registry registry() {
    if(registry == null) {
      registry = new Registry(config());
    }
    return registry;
  }

  public Server server() {
    if(server == null) {
      server = new Server(config());
    }
    return server;
  }

  public Traefik traefik() {
    if(traefik == null) {
      traefik = new Traefik(config());
    }
    return traefik;
  }

  // TODO: with verbosity
  // TODO: boot strategy

  public boolean holdingLock() {
    return holdingLock;
  }

  public void holdingLock(boolean holdingLock) {
    this.holdingLock = holdingLock;
  }

  public boolean connected() {
    return connected;
  }

  // private

  // TODO: configure ssh kit with

  private Specifics specifics() {
    if(specifics == null) {
      return new Specifics(config(), specificHosts, specificRoles);
    }
    return specifics;
  }

  // delegates

  public List<String> hosts() {
    return specifics().hosts();
  }

  public List<Role> roles() {
    return specifics().roles();
  }

  public String primaryHost() {
    return specifics().primaryHost();
  }

  public Role primaryRole() {
    return specifics().primaryRole();
  }

  public List<Role> rolesOn(String host) {
    return specifics.rolesOn(host);
  }

  public List<String> traefikHosts() {
    return specifics.traefikHosts();
  }

  public List<String> accessoryHosts() {
    return specifics().accessoryHosts();
  }

  // manage

  @Override
  public void close() throws Exception {

    // shutdown
    sshHosts.values().forEach(SshHost::close);

  }

  public SshHost host(String host) {
    SshHost sshHost = sshHosts.get(host);
    if (sshHost == null) {
      sshHost = new SshHost(host, config().ssh());
      sshHosts.put(host, sshHost);
    }
    return sshHost;
  }










}
