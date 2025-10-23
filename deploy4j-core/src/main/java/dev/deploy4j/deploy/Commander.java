package dev.deploy4j.deploy;

import dev.deploy4j.deploy.configuration.Accessory;
import dev.deploy4j.deploy.configuration.Configuration;
import dev.deploy4j.deploy.configuration.Role;
import dev.deploy4j.deploy.host.commands.AccessoryHostCommands;
import dev.deploy4j.deploy.host.commands.AppHostCommands;
import dev.deploy4j.deploy.host.commands.AuditorHostCommands;
import dev.deploy4j.deploy.utils.Utils;

import java.util.List;
import java.util.Map;

/**
 * Context class to hold shared information and configurations for deployment processes.
 * <p>
 * This will become Deployment Context
 */
public class Commander implements LockContext {

  private final Configuration config;
  private final List<Role> specificRoles;
  private final List<String> specificHosts;
  private final Specifics specifics;

  private boolean holdingLock;
  private boolean connected;

  public Commander(
    Configuration config,
    String[] hosts,
    String[] roleNames,
    Boolean primary
  ) {
    this.config = config;
    this.holdingLock = false;
    this.connected = false;

    if (hosts != null) {
      specificHosts = Utils.filterSpecificItems(hosts, config().allHosts());
      if (specificHosts.isEmpty()) {
        throw new RuntimeException("No --hosts match for " + String.join(",", hosts));
      }
    } else if (primary != null) {
      specificHosts = List.of(config().primaryHost());
    } else {
      specificHosts = null;
    }

    if (roleNames != null) {
      specificRoles = Utils.filterSpecificItems(roleNames, config().roles());
      if (specificRoles.isEmpty()) {
        throw new RuntimeException("No --roles match for " + String.join(",", roleNames));
      }
    } else {
      specificRoles = null;
    }

    this.specifics = new Specifics(config(), specificHosts, specificRoles);

  }

  public Configuration config() {
    return config;
  }

  public List<Role> specificRoles() {
    return specificRoles;
  }

  public List<String> specificHosts() {
    return specificHosts;
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

  public AppHostCommands app(Role role, String host) {
    return new AppHostCommands(config(), role, host);
  }

  public AccessoryHostCommands accessory(String name) {
    return new AccessoryHostCommands(config(), name);
  }

  public AuditorHostCommands auditor() {
    return auditor(null);
  }

  public AuditorHostCommands auditor(Map<String, String> details) {
    return new AuditorHostCommands(config(), details);
  }

  // TODO: with verbosity
  // TODO: boot strategy

  @Override
  public boolean holdingLock() {
    return holdingLock;
  }

  @Override
  public void holdingLock(boolean holdingLock) {
    this.holdingLock = holdingLock;
  }

  public boolean connected() {
    return connected;
  }

  // private

  // TODO: configure ssh kit with

  private Specifics specifics() {
    return specifics;
  }

  // delegates

  @Override
  public List<String> hosts() {
    return specifics().hosts();
  }

  public List<Role> roles() {
    return specifics().roles();
  }

  @Override
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

}
