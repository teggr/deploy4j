package dev.deploy4j.deploy.configuration.raw;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SpringBootConfig {

  private final List<String> hosts;
  private final List<String> tags;
  private final Integer actuatorPort;
  private final String actuatorBasePath;

  @JsonCreator
  public SpringBootConfig(
    @JsonProperty("hosts") List<String> hosts,
    @JsonProperty("tags") List<String> tags,
    @JsonProperty("actuator_port") Integer actuatorPort,
    @JsonProperty("actuator_base_path") String actuatorBasePath
  ) {
    this.hosts = hosts;
    this.tags = tags;
    this.actuatorPort = actuatorPort;
    this.actuatorBasePath = actuatorBasePath;
  }

  public SpringBootConfig() {
    this.hosts = null;
    this.tags = null;
    this.actuatorPort = null;
    this.actuatorBasePath = null;
  }

  public List<String> hosts() {
    return hosts;
  }

  public List<String> tags() {
    return tags;
  }

  public Integer actuatorPort() {
    return actuatorPort;
  }

  public String actuatorBasePath() {
    return actuatorBasePath;
  }

  public SpringBootConfig deepMerge(SpringBootConfig other) {
    if (other == null) {
      return this;
    }
    return new SpringBootConfig(
      other.hosts() != null ? other.hosts() : this.hosts(),
      other.tags() != null ? other.tags() : this.tags(),
      other.actuatorPort() != null ? other.actuatorPort() : this.actuatorPort(),
      other.actuatorBasePath() != null ? other.actuatorBasePath() : this.actuatorBasePath()
    );
  }
}
