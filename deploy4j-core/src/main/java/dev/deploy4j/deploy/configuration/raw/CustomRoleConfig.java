package dev.deploy4j.deploy.configuration.raw;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CustomRoleConfig {

  private final List<ServerConfig> hosts;
  private final Boolean traefik;
  private final String cmd;
  private final EnvironmentConfig env;
  private final LoggingConfig logging;
  private final HealthCheckConfig healthcheck;
  private final Map<String, String> options;
  private final String assetPath;
  private final Map<String, String> labels;

  @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
  public CustomRoleConfig(
    @JsonProperty("hosts") List<?> hosts,
    @JsonProperty("traefik") Boolean traefik,
    @JsonProperty("cmd") String cmd,
    @JsonProperty("env") EnvironmentConfig env,
    @JsonProperty("logging") LoggingConfig logging,
    @JsonProperty("healthcheck") HealthCheckConfig healthcheck,
    @JsonProperty("options") Map<String, String> options,
    @JsonProperty("asset_path") String assetPath,
    @JsonProperty("labels") Map<String, String> labels
    ) {
    this.hosts = hosts.stream().map( o -> {;
      if( o instanceof String s ) {
        return new ServerConfig(s);
      } else if( o instanceof Map<?,?> m ) {
        return new ServerConfig( (Map<String, List<String>>)m );
      } else {
        throw new IllegalArgumentException("Invalid server entry: " + o);
      }
    }).toList();
    this.traefik = traefik;
    this.cmd = cmd;
    this.env = env;
    this.logging = logging;
    this.healthcheck = healthcheck;
    this.options = options;
    this.assetPath = assetPath;
    this.labels = labels;
  }

  public CustomRoleConfig() {
    this.hosts = List.of();
    this.traefik = false;
    this.cmd = null;
    this.env = null;
    this.logging = null;
    this.healthcheck = null;
    this.options = null;
    this.assetPath = null;
    this.labels = null;
  }

  public List<ServerConfig> hosts() {
    return hosts;
  }

  public Boolean traefik() {
    return traefik;
  }

  public String cmd() {
    return cmd;
  }

  public EnvironmentConfig env() {
    return env;
  }

  public LoggingConfig logging() {
    return logging;
  }

  public HealthCheckConfig healthcheck() {
    return healthcheck;
  }

  public Map<String, String> options() {
    return options;
  }

  public String assetPath() {
    return assetPath;
  }

  public Map<String, String> labels() {
    return labels;
  }
}
