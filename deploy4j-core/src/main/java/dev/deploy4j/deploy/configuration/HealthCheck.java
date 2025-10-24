package dev.deploy4j.deploy.configuration;

import dev.deploy4j.deploy.configuration.raw.HealthCheckConfig;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class HealthCheck {

  private final HealthCheckConfig healthCheckConfig;
  private final String context;

  public HealthCheck(HealthCheckConfig healthCheckConfig, String context) {
    this.healthCheckConfig = healthCheckConfig != null ? healthCheckConfig : new HealthCheckConfig();
    this.context = context;
  }

  public HealthCheck merge(HealthCheck other) {
    return new HealthCheck(
      healthCheckConfig().deepMerge(other.healthCheckConfig()),
      null
    );
  }

  public String cmd() {
    return healthCheckConfig().cmd() != null ? healthCheckConfig().cmd() : httpHealthCheck();
  }

  public Integer port() {
    return healthCheckConfig().port() != null ? healthCheckConfig().port() : 8080;
  }

  public String path() {
    return healthCheckConfig().path() != null ? healthCheckConfig().path() : "/actuator/health";
  }

  public Integer maxAttempts() {
    return healthCheckConfig().maxAttempts() != null ? healthCheckConfig().maxAttempts() : 7;
  }

  public String interval() {
    return healthCheckConfig().interval() != null ? healthCheckConfig().interval() : "1s";
  }

  public String cord() {
    return healthCheckConfig().cord() != null ? healthCheckConfig().cord() : "/tmp/deploy4j-cord";
  }

  public Integer logLines() {
    return healthCheckConfig().logLines() != null ? healthCheckConfig().logLines() : 50;
  }

  public boolean setPortOrPath() {
    return healthCheckConfig().port() != null || healthCheckConfig().path() != null;
  }

  // private

  private String httpHealthCheck() {
    if (path() != null || port() != null) {
      String hostPort = "http://localhost:%s".formatted(port());
      String uriJoin = Stream.of(hostPort, path())
        .filter(Objects::nonNull)
        .collect(Collectors.joining());
      return "curl -f %s || exit 1".formatted(uriJoin);
    } else {
      return null;
    }
  }

  // attributes

  public HealthCheckConfig healthCheckConfig() {
    return healthCheckConfig;
  }

  public Map<String, Object> resolve() {
    Map<String, Object> map = new HashMap<>();
    map.put("cmd", cmd());
    map.put("port", port());
    map.put("path", path());
    return map;
  }
}
