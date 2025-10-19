package dev.deploy4j.raw;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

@JsonIgnoreProperties
public class TraefikConfig {

  @JsonCreator
  public TraefikConfig(
    @JsonProperty("image") String image,
    @JsonProperty("host_port") String hostPort,
    @JsonProperty("publish") boolean publish,
    @JsonProperty("labels") Map<String, String> labels,
    @JsonProperty("args") Map<String, String> args,
    @JsonProperty("options") Map<String, String> options,
    @JsonProperty("env") EnvironmentConfig env
  ) {

  }

}
