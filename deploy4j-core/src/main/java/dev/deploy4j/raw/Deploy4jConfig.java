package dev.deploy4j.raw;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public record Deploy4jConfig(
  String service,
  String image,
  RegistryConfig registry,
  List<ServerConfig> servers,
  Map<String, ServerRoleConfig> serverRoles,
  SshConfig ssh,
  Map<String, String> env,
  Map<String, AccessoryConfig> accessories,
  TraefikConfig traefik,
  HealthCheckConfig healthCheck
) {

  public String primaryRole() {
    return null;
  }

  public LoggingConfig logging() {
    return new LoggingConfig();
  }

  public Map<String, String> labels() {
    return new HashMap<>();
  }

}
