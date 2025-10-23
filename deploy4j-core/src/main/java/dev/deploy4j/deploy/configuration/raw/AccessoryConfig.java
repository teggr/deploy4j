package dev.deploy4j.deploy.configuration.raw;

import com.fasterxml.jackson.annotation.*;

import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AccessoryConfig  {

  private final String service;
  private final String image;
  private final String host;
  private final List<String> hosts;
  private final List<String> roles;
  private final String cmd;
  private final String port;
  private final Map<String, String> labels;
  private final Map<String, String> options;
  private final EnvironmentConfig env;
  private final List<String> files;
  private final List<String> directories;
  private final List<String> volumes;

  @JsonCreator
  public AccessoryConfig(
    @JsonProperty("service")  String service,
    @JsonProperty("image") String image,
    @JsonProperty("host") String host,
    @JsonProperty("hosts") List<String> hosts,
    @JsonProperty("roles") List<String> roles,
    @JsonProperty("cmd") String cmd,
    @JsonProperty("port") String port,
    @JsonProperty("labels") Map<String, String> labels,
    @JsonProperty("options") Map<String, String> options,
    @JsonProperty("env") EnvironmentConfig env,
    @JsonProperty("files") List<String> files,
    @JsonProperty("directories") List<String> directories,
    @JsonProperty("volumes") List<String> volumes
  ) {
    this.service = service;
    this.image = image;
    this.host = host;
    this.hosts = hosts;
    this.roles = roles;
    this.cmd = cmd;
    this.port = port;
    this.labels = labels;
    this.options = options;
    this.env = env;
    this.files = files;
    this.directories = directories;
    this.volumes = volumes;
  }

  public String service() {
    return service;
  }

  public String image() {
    return image;
  }

  public String host() {
    return host;
  }

  public List<String> hosts() {
    return hosts;
  }

  public List<String> roles() {
    return roles;
  }

  public String cmd() {
    return cmd;
  }

  public String port() {
    return port;
  }

  public Map<String, String> labels() {
    return labels;
  }

  public Map<String, String> options() {
    return options;
  }

  public EnvironmentConfig env() {
    return env;
  }

  public List<String> files() {
    return files;
  }

  public List<String> directories() {
    return directories;
  }

  public List<String> volumes() {
    return volumes;
  }
}
