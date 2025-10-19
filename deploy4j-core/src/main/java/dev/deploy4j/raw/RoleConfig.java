package dev.deploy4j.raw;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

public class RoleConfig {

  private final List<ServerConfig> list;
  private final CustomRoleConfig customRole;

  @JsonCreator
  public RoleConfig(CustomRoleConfig customRole) {
    this.list = null;
    this.customRole = customRole;
  }

  @JsonCreator
  public RoleConfig(List<?> hosts) {
    this.list = hosts.stream().map(o -> {
      if (o instanceof String s) {
        return new ServerConfig(s);
      } else if (o instanceof Map<?, ?> m) {
        return new ServerConfig((Map<String, List<String>>) m);
      } else {
        throw new IllegalArgumentException("Invalid server entry: " + o);
      }
    }).toList();
    this.customRole = null;
  }

  public boolean isAList() {
    return list != null;
  }

  public List<ServerConfig> list() {
    return list;
  }

  public CustomRoleConfig customRole() {
    return customRole;
  }

}
