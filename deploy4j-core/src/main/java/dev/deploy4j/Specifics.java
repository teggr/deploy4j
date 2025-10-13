package dev.deploy4j;

import dev.deploy4j.configuration.Configuration;
import dev.deploy4j.configuration.Role;
import dev.deploy4j.ssh.SshHost;

import java.util.ArrayList;
import java.util.List;

public class Specifics {

  private final Configuration config;
  private final List<String> specificHosts;
  private final List<Role> specificRoles;

  private final List<Role> roles;
  private final List<String> hosts;
  private final String primaryHost;
  private final Role primaryRole;

  public Specifics(Configuration config, List<String> specificHosts, List<Role> specificRoles) {

    this.config = config;
    this.specificHosts = specificHosts;
    this.specificRoles = specificRoles;

    this.roles = specifiedRoles();
    this.hosts = specifiedHosts();

    this.primaryHost = !specificHosts.isEmpty() ?
      specificHosts.get(0) :
      (primarySpecificRole() != null ?
        primarySpecificRole().primaryHost() :
        config.primaryHost()
      );
    this.primaryRole = primaryOrFirstRole(rolesOn(primaryHost));

  }

  public String primaryHost() {
    return primaryHost;
  }

  public List<Role> rolesOn(String host) {
    return roles().stream().filter(role ->
      {
        return role.hosts().contains(host);
      }
    ).toList();
  }

  private Role primarySpecificRole() {
    if (!specificRoles.isEmpty()) {
      return primaryOrFirstRole(specificRoles);
    }
    return null;
  }

  private Role primaryOrFirstRole(List<Role> roles) {
    return roles.stream()
      .filter(role -> role.equals(config.primaryRole()))
      .findFirst()
      .orElse(null);
  }

  public List<Role> roles() {
    return roles;
  }

  public List<String> hosts() {
    return hosts;
  }

  // traefik and accesssory hosts

  private List<Role> specifiedRoles() {
    List<Role> roles = new ArrayList<>();
    if (!specificRoles.isEmpty()) {
      roles.addAll(specificRoles);
    } else {
      roles.addAll(config.roles());
    }
    return roles.stream()
      .filter(role -> {
        List<String> hosts = new ArrayList<>();
        if (!specificHosts.isEmpty()) {
          hosts.addAll(specificHosts);
        } else {
          hosts.addAll(config.allHosts());
        }
        hosts.retainAll(role.hosts());
        return !hosts.isEmpty();
      })
      .toList();
  }

  private List<String> specifiedHosts() {
    List<String> hosts = new ArrayList<>();
    if (!specificHosts.isEmpty()) {
      hosts.addAll(specificHosts);
    } else {
      hosts.addAll(config.allHosts());
    }
    return hosts.stream().filter(host -> {
        List<Role> roles = new ArrayList<>();
        if (!specificRoles.isEmpty()) {
          roles.addAll(specificRoles);
        } else {
          roles.addAll(config.roles());
        }
        return roles.stream()
          .flatMap(role -> role.hosts().stream())
          .toList()
          .contains(host);
      })
      .toList();
  }

  public List<String> accessoryHosts() {
    // config.accessories.flat_map(&:hosts) & specified_hosts
    return List.of();
  }

  public List<String> traefikHosts() {
    return config.traefikHosts(); // specifiedHosts
  }

}
