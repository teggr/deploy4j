package dev.deploy4j.deploy.configuration.raw;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SpringBootConfig {

  private final Integer actuatorPort;
  private final String actuatorBasePath;

  @JsonCreator
  public SpringBootConfig(
    @JsonProperty("actuator_port") Integer actuatorPort,
    @JsonProperty("actuator_base_path") String actuatorBasePath
  ) {
    this.actuatorPort = actuatorPort;
    this.actuatorBasePath = actuatorBasePath;
  }

  public SpringBootConfig() {
    this.actuatorPort = null;
    this.actuatorBasePath = null;
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
      other.actuatorPort() != null ? other.actuatorPort() : this.actuatorPort(),
      other.actuatorBasePath() != null ? other.actuatorBasePath() : this.actuatorBasePath()
    );
  }
}
