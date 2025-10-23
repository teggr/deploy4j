package dev.deploy4j.deploy.configuration.raw;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

@JsonIgnoreProperties
public class TraefikConfig {

  private final String image;
  private final Integer hostPort;
  private final Boolean publish;
  private final Map<String, String> labels;
  private final Map<String, String> args;
  private final Map<String, String> options;
  private final EnvironmentConfig env;

  @JsonCreator
  public TraefikConfig(
    @JsonProperty("image") String image,
    @JsonProperty("host_port") Integer hostPort,
    @JsonProperty("publish") Boolean publish,
    @JsonProperty("labels") Map<String, String> labels,
    @JsonProperty("args") Map<String, String> args,
    @JsonProperty("options") Map<String, String> options,
    @JsonProperty("env") EnvironmentConfig env
  ) {
    this.image = image;
    this.hostPort = hostPort;
    this.publish = publish;
    this.labels = labels;
    this.args = args;
    this.options = options;
    this.env = env;
  }

  public TraefikConfig() {
    this.image = null;
    this.hostPort = null;
    this.publish = null;
    this.labels = null;
    this.args = null;
    this.options = null;
    this.env = null;
  }

  public String image() {
    return image;
  }

  public Integer hostPort() {
    return hostPort;
  }

  public Boolean publish() {
    return publish;
  }

  public Map<String, String> labels() {
    return labels;
  }

  public Map<String, String> args() {
    return args;
  }

  public Map<String, String> options() {
    return options;
  }

  public EnvironmentConfig env() {
    return env;
  }

}
