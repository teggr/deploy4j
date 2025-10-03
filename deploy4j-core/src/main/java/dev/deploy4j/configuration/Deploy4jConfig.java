package dev.deploy4j.configuration;

import java.util.List;
import java.util.Map;

public record Deploy4jConfig(
  String service,
  String image,
  List<ServerConfig> servers,
  SshConfig ssh,
  Map<String, String> env,
  HealthCheckConfig healthCheck
) {

}
