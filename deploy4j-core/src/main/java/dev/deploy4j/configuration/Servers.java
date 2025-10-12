package dev.deploy4j.configuration;

import dev.deploy4j.raw.ServerConfig;
import dev.deploy4j.raw.ServerRoleConfig;

import java.util.List;
import java.util.Map;

public class Servers {

  public static final String DEFAULT_ROLE_NAME = "web";
  private final Configuration config;
  private final List<ServerConfig> serversConfig;
  private final Map<String, ServerRoleConfig> serverRolesConfig;
  private final List<Role> roles;

  public Servers(Configuration config) {
    this.config = config;
    this.serversConfig = config.rawConfig().servers();
    this.serverRolesConfig = config.rawConfig().serverRoles();
    // TODO validate servers

    this.roles = roleNames().stream()
      .map( roleName -> new Role(roleName, config) )
      .toList();

  }

  public List<Role> roles() {
    return roles;
  }

  private List<String> roleNames() {
    return serversConfig.isEmpty() ?
      serverRolesConfig.keySet().stream().sorted().toList() :
      List.of(DEFAULT_ROLE_NAME);
  }
}
