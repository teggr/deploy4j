package dev.deploy4j.configuration;

public record HealthCheckConfig(
  String cmd,
  String interval,
  int maxAttempts,
  String port,
  String path
) {
  public HealthCheckConfig {
    if (cmd == null || cmd.isBlank()) cmd = "curl -f http://localhost:8080/actuator/health";
    if (interval == null || interval.isBlank()) interval = "10s";
    if (maxAttempts <= 0) maxAttempts = 3;
    if (port == null || port.isBlank()) port = "8080";
    if (path == null || path.isBlank()) path = "/actuator/health";
  }
}
