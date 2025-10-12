package dev.deploy4j.configuration;

import dev.deploy4j.raw.HealthCheckConfig;

import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class HealthCheck {

  private final HealthCheckConfig healthCheckConfig;

  public HealthCheck(HealthCheckConfig healthCheckConfig) {
    this.healthCheckConfig = healthCheckConfig;
  }

  public String cmd() {
    return healthCheckConfig.cmd() != null ? healthCheckConfig.cmd() : httpHealthCheck();
  }

  public String port() {
    return healthCheckConfig.port() != null ? healthCheckConfig.port() : "3000";
  }

  public String path() {
    return healthCheckConfig.path() != null ? healthCheckConfig.path() : "/up";
  }

  public String interval() {
    return healthCheckConfig.interval() != null ? healthCheckConfig.interval() : "1s";
  }

  // cord
  // log lines
  // setportorpath

  private String httpHealthCheck() {
    String host = Stream.of("http://localhost", port())
      .filter(Objects::nonNull)
      .collect(Collectors.joining(":"));
    String uri = Stream.of(host, path())
      .filter(Objects::nonNull)
      .collect(Collectors.joining("/"));

    return "curl -f %s || exit 1".formatted(uri);
  }

  public boolean setPortOrPath() {
    return port() != null || path() != null;
  }

}
