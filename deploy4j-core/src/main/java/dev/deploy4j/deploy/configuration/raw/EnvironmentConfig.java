package dev.deploy4j.deploy.configuration.raw;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class EnvironmentConfig {

  private final Map<String, String> map;
  private final Map<String, String> clear;
  private final List<String> secrets;
  private final Map<String, EnvironmentConfig> tags;

  @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
  public EnvironmentConfig(
    @JsonProperty("clear") Map<String, String> clear,
    @JsonProperty("secret") List<String> secret,
    @JsonProperty("tags") Map<String, EnvironmentConfig> tags,
    @JsonAnySetter Map<String, String> map
  ) {
    this.map = map;
    this.clear = clear;
    this.secrets = secret;
    this.tags = tags;
  }

  public EnvironmentConfig() {
    this.map = null;
    this.clear = null;
    this.secrets = null;
    this.tags = null;
  }

  public boolean isClearAndSecrets() {
    return clear != null || secrets != null;
  }

  public boolean isAMap() {
    return map != null;
  }

  public boolean isTags() {
    return tags != null;
  }

  public Map<String, String> map() {
    return map;
  }

  public Map<String, String> clear() {
    return clear;
  }

  public List<String> secrets() {
    return secrets;
  }

  public Map<String, EnvironmentConfig> tags() {
    return tags;
  }

}
