package dev.deploy4j.deploy.configuration.raw;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

@JsonIgnoreProperties
public class GatewayConfig {

  private final String image;
  private final Integer hostPort;
  private final Boolean publish;
  private final Map<String, String> labels;
  private final Map<String, String> args;
  private final Map<String, FlexibleValue> options;
  private final EnvironmentConfig env;

  @JsonCreator
  public GatewayConfig(
    @JsonProperty("image") String image,
    @JsonProperty("host_port") Integer hostPort,
    @JsonProperty("publish") Boolean publish,
    @JsonProperty("labels") Map<String, String> labels,
    @JsonProperty("args") Map<String, String> args,
    @JsonProperty("options") Map<String, FlexibleValue> options,
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

  public GatewayConfig() {
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

  public Map<String, FlexibleValue> options() {
    return options;
  }

  public EnvironmentConfig env() {
    return env;
  }

}
