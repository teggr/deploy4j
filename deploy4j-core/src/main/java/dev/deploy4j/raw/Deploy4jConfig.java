package dev.deploy4j.raw;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Deploy4jConfig {

  private final String service;
  private final String image;
  private final Map<String, String> labels;
  private final List<String> volumes;
  private final RegistryConfig registry;
  private final ServersConfig servers;
  private final EnvironmentConfig env;
  private final String assetPath;
  private final String hooksPath;
  private final Boolean requireDestination;
  private final String primaryRole;
  private final Boolean allowEmptyRoles;
  private final Integer stopWaitTime;
  private final Integer retainContainers;
  private final String minimumVersion;
  private final Integer readinessDelay;
  private final String runDirectory;
  private final SshConfig ssh;
  private final Map<String, AccessoryConfig> accessories;
  private final TraefikConfig traefik;
  private final BootConfig boot;
  private final HealthCheckConfig healthCheck;
  private final LoggingConfig logging;

  @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
  public Deploy4jConfig(
    @JsonProperty("service") String service,
    @JsonProperty("image") String image,
    @JsonProperty("labels") Map<String, String> labels,
    @JsonProperty("volumes") List<String> volumes,
    @JsonProperty("registry") RegistryConfig registry,
    @JsonProperty("servers") ServersConfig servers,
    @JsonProperty("env") EnvironmentConfig env,
    @JsonProperty("asset_path") String assetPath,
    @JsonProperty("hooks_path") String hooksPath,
    @JsonProperty("require_destination") Boolean requireDestination,
    @JsonProperty("primary_role") String primaryRole,
    @JsonProperty("allow_empty_roles") Boolean allowEmptyRoles,
    @JsonProperty("stop_wait_time") Integer stopWaitTime,
    @JsonProperty("retain_containers") Integer retainContainers,
    @JsonProperty("minimum_version") String minimumVersion,
    @JsonProperty("readiness_delay") Integer readinessDelay,
    @JsonProperty("run_directory") String runDirectory,
    @JsonProperty("ssh") SshConfig ssh,
    @JsonProperty("accessories") Map<String, AccessoryConfig> accessories,
    @JsonProperty("traefik") TraefikConfig traefik,
    @JsonProperty("boot") BootConfig boot,
    @JsonProperty("healthcheck") HealthCheckConfig healthCheck,
    @JsonProperty("logging") LoggingConfig logging
  ) {
    this.service = service;
    this.image = image;
    this.labels = labels;
    this.volumes = volumes;
    this.registry = registry;
    this.servers = servers;
    this.env = env;
    this.assetPath = assetPath;
    this.hooksPath = hooksPath;
    this.requireDestination = requireDestination;
    this.primaryRole = primaryRole;
    this.allowEmptyRoles = allowEmptyRoles;
    this.stopWaitTime = stopWaitTime;
    this.retainContainers = retainContainers;
    this.minimumVersion = minimumVersion;
    this.readinessDelay = readinessDelay;
    this.runDirectory = runDirectory;
    this.ssh = ssh;
    this.accessories = accessories;
    this.traefik = traefik;
    this.boot = boot;
    this.healthCheck = healthCheck;
    this.logging = logging;
  }

  public String service() {
    return service;
  }

  public String image() {
    return image;
  }

  public Map<String, String> labels() {
    return labels;
  }

  public List<String> volumes() {
    return volumes;
  }

  public RegistryConfig registry() {
    return registry;
  }

  public ServersConfig servers() {
    return servers;
  }

  public EnvironmentConfig env() {
    return env;
  }

  public String assetPath() {
    return assetPath;
  }

  public String hooksPath() {
    return hooksPath;
  }

  public Boolean requireDestination() {
    return requireDestination;
  }

  public String primaryRole() {
    return primaryRole;
  }

  public Boolean allowEmptyRoles() {
    return allowEmptyRoles;
  }

  public Integer stopWaitTime() {
    return stopWaitTime;
  }

  public Integer retainContainers() {
    return retainContainers;
  }

  public String minimumVersion() {
    return minimumVersion;
  }

  public Integer readinessDelay() {
    return readinessDelay;
  }

  public String runDirectory() {
    return runDirectory;
  }

  public SshConfig ssh() {
    return ssh;
  }

  public Map<String, AccessoryConfig> accessories() {
    return accessories;
  }

  public TraefikConfig traefik() {
    return traefik;
  }

  public BootConfig boot() {
    return boot;
  }

  public HealthCheckConfig healthCheck() {
    return healthCheck;
  }

  public LoggingConfig logging() {
    return logging;
  }
}
