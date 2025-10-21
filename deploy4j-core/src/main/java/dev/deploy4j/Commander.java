package dev.deploy4j;

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

  private BuilderCommands builder;
  private DockerCommands docker;
  private HealthcheckCommands healthcheck;
  private HookCommands hook;
  private LockCommands lock;
  private PruneCommands prune;
  private RegistryCommands registry;
  private ServerCommands server;
  private TraefikCommands traefik;

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

  public AppCommands app(Role role, String host) {
    return new AppCommands(config, role, host);
  }

  public AccessoryCommands accessory(String name) {
    return new AccessoryCommands(config, name);
  }

  public AuditorCommands auditor() {
    return auditor(null);
  }

  public AuditorCommands auditor(Map<String, String> details) {
    return new AuditorCommands(config(), details);
  }

  public BuilderCommands builder() {
    if(builder == null) {
      builder = new BuilderCommands(config());
    }
    return builder;
  }

  public DockerCommands docker() {
    if(docker == null) {
      docker = new DockerCommands(config());
    }
    return docker;
  }

  public HealthcheckCommands healthcheck() {
    if(healthcheck == null) {
      healthcheck = new HealthcheckCommands(config());
    }
    return healthcheck;
  }

  public HookCommands hook() {
    if(hook == null) {
      hook = new HookCommands(config());
    }
    return hook;
  }

  public LockCommands lock() {
    if(lock == null) {
      lock = new LockCommands(config());
    }
    return lock;
  }

  public PruneCommands prune() {
    if(prune == null) {
      prune = new PruneCommands(config());
    }
    return prune;
  }

  public RegistryCommands registry() {
    if(registry == null) {
      registry = new RegistryCommands(config());
    }
    return registry;
  }

  public ServerCommands server() {
    if(server == null) {
      server = new ServerCommands(config());
    }
    return server;
  }

  public TraefikCommands traefik() {
    if(traefik == null) {
      traefik = new TraefikCommands(config());
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
