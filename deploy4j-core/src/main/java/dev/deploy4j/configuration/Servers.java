package dev.deploy4j.configuration;

import dev.deploy4j.raw.ServerConfig;
import dev.deploy4j.raw.ServersConfig;

import java.util.List;
import java.util.Map;

public class Servers {

  private final Configuration config;
  private final ServersConfig serversConfig;
  private final List<Role> roles;

  public Servers(Configuration config) {
    this.config = config;
    this.serversConfig = config.rawConfig().servers();
    // TODO validate servers

    this.roles = roleNames().stream()
      .map( roleName -> new Role(roleName, config) )
      .toList();

  }

  // private

  private List<String> roleNames() {
    return serversConfig().isAList() ?
      List.of("web") :
      serversConfig().roles().keySet().stream().sorted().toList() ;
  }

  // attributes

  public Configuration config() {
    return config;
  }

  public ServersConfig serversConfig() {
    return serversConfig;
  }

  public List<Role> roles() {
    return roles;
  }

}
