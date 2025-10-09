package dev.deploy4j.configuration;

import org.apache.commons.lang.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Deploy4jConfig {

  public static final String DEFAULT_RUN_DIRECTORY = ".deploy4j";

  private final String destination;
  private final String version;
  private final String service;
  private final String image;
  private final RegistryConfig registry;
  private final List<ServerConfig> servers;
  private final SshConfig ssh;
  private final Map<String, String> env;
  private final TraefikConfig traefik;
  private final HealthCheckConfig healthCheck;

  public Deploy4jConfig(
    String destination,
    String version,
    String service,
    String image,
    RegistryConfig registry,
    List<ServerConfig> servers,
    SshConfig ssh,
    Map<String, String> env,
    TraefikConfig traefik,
    HealthCheckConfig healthCheck
  ) {
    this.destination = destination;
    this.version = version;
    this.service = service;
    this.image = image;
    this.registry = registry;
    this.servers = servers;
    this.ssh = ssh;
    this.env = env;
    this.traefik = traefik;
    this.healthCheck = healthCheck;
  }

  public String service() {
    return service;
  }

  public String destination() {
    return destination;
  }

  public List<ServerConfig> servers() {
    return servers;
  }

  public SshConfig ssh() {
    return ssh;
  }

  public TraefikConfig traefik() {
    return traefik;
  }

  public String runDirectory() {
    return ".deploy4j";
  }

  public RegistryConfig registry() {
    return registry;
  }

  public String absoluteImage() {
    return "%s:%s".formatted(repository(), version());
  }

  public String version() {
    // declared version || ENV version || git commit hash || "latest"
    if(StringUtils.isNotBlank(version)) {
      return version;
    }
    return null;
  }

  private String repository() {
    return Stream.of(registry().server(), image)
      .filter(StringUtils::isNotBlank)
      .collect(Collectors.joining("/"));
  }

  public String primaryRole() {
    return "web";
  }

  public String[] loggingArgs() {
    // TODO logging drivers and options
    return new String[]{};
  }

  public String latestImage() {
    return "%s:%s".formatted(repository(), latestTag());
  }

  private String latestTag() {
    return Stream.of( "latest", destination() )
      .collect(Collectors.joining("-"));
  }

  public String stopWaitTime() {
    return null;  // TODO: support stop wait time
  }
}
