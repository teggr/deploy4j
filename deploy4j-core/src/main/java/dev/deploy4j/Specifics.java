package dev.deploy4j;

import dev.deploy4j.configuration.Configuration;
import dev.deploy4j.configuration.Role;

import java.util.ArrayList;
import java.util.List;

import static java.util.Optional.ofNullable;

public class Specifics {

  private final String primaryHost;
  private final Role primaryRole;
  private final List<String> hosts;
  private final List<Role> roles;

  private final Configuration config;
  private final List<String> specificHosts;
  private final List<Role> specificRoles;

  public Specifics(Configuration config, List<String> specificHosts, List<Role> specificRoles) {

    this.config = config;
    this.specificHosts = specificHosts;
    this.specificRoles = specificRoles;

    this.roles = specifiedRoles();
    this.hosts = specifiedHosts();

    this.primaryHost = ofNullable(specificHosts)
      .map(List::getFirst)
      .orElseGet(() ->
        ofNullable(primarySpecificRole())
          .map(Role::primaryHost)
          .orElseGet(config::primaryHost)
      );
    this.primaryRole = primaryOrFirstRole(rolesOn(primaryHost));

    roles.sort((role1, role2) -> {
      if (role1.equals(primaryRole)) return -1;
      if (role2.equals(primaryRole)) return 1;
      return 0;
    });
    hosts.sort((host1, host2) -> {
      boolean host1HasPrimary = rolesOn(host1).stream().anyMatch(role -> role.equals(primaryRole));
      boolean host2HasPrimary = rolesOn(host2).stream().anyMatch(role -> role.equals(primaryRole));
      return Boolean.compare(!host1HasPrimary, !host2HasPrimary);
    });
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


  public List<Role> roles() {
    return roles;
  }

  public List<String> hosts() {
    return hosts;
  }

  // traefik and accesssory hosts


  public List<String> accessoryHosts() {
    // config.accessories.flat_map(&:hosts) & specified_hosts
    return List.of();
  }

  public List<String> traefikHosts() {
    return config.traefikHosts(); // specifiedHosts
  }

  private Role primarySpecificRole() {
    if (specificRoles != null) {
      return primaryOrFirstRole(specificRoles);
    }
    return null;
  }

  private Role primaryOrFirstRole(List<Role> roles) {
    return roles.stream()
      .filter(role -> role.equals(config.primaryRole()))
      .findFirst()
      .orElse(config.roles().get(0));
  }

  private List<Role> specifiedRoles() {
    List<Role> roles = new ArrayList<>();
    if (specificRoles != null) {
      roles.addAll(specificRoles);
    } else {
      roles.addAll(config.roles());
    }
    return new ArrayList<>( roles.stream()
      .filter(role -> {
        List<String> hosts = new ArrayList<>();
        if (specificHosts != null) {
          hosts.addAll(specificHosts);
        } else {
          hosts.addAll(config.allHosts());
        }
        hosts.retainAll(role.hosts());
        return !hosts.isEmpty();
      })
      .toList() );
  }

  private List<String> specifiedHosts() {
    List<String> hosts = new ArrayList<>();
    if (specificHosts != null) {
      hosts.addAll(specificHosts);
    } else {
      hosts.addAll(config.allHosts());
    }
    return new ArrayList<>( hosts.stream().filter(host -> {
        List<Role> roles = new ArrayList<>();
        if (specificRoles != null) {
          roles.addAll(specificRoles);
        } else {
          roles.addAll(config.roles());
        }
        return roles.stream()
          .flatMap(role -> role.hosts().stream())
          .toList()
          .contains(host);
      })
      .toList() );
  }

}
