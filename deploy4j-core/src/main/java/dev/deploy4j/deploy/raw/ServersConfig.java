package dev.deploy4j.deploy.raw;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.List;
import java.util.Map;

public class ServersConfig {

  private final List<ServerConfig> list;
  private final Map<String, RoleConfig> map;

  @JsonCreator
  public ServersConfig(List<?> hosts) {
    this.list = hosts.stream().map( o -> {;
      if( o instanceof String s ) {
        return new ServerConfig(s);
      } else if( o instanceof Map<?,?> m ) {
        return new ServerConfig( (Map<String, List<String>>)m );
      } else {
        throw new IllegalArgumentException("Invalid server entry: " + o);
      }
    }).toList();
    this.map = null;
  }

  @JsonCreator
  public ServersConfig(Map<String, RoleConfig> map) {
    this.list = null;
    this.map = map;
  }

  public boolean isAList() {
    return list != null;
  }

  public List<ServerConfig> list() {
    return list;
  }

  public Map<String, RoleConfig> roles() {
    return map;
  }

}
