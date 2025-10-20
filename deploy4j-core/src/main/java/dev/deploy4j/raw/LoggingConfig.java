package dev.deploy4j.raw;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class LoggingConfig {

  private final String driver;
  private final Map<String, String> options;

  @JsonCreator
  public LoggingConfig(@JsonProperty("driver") String driver,
                       @JsonProperty("options") Map<String, String> options) {
    this.driver = driver;
    this.options = options;
  }

  public LoggingConfig() {
    this.driver = null;
    this.options = null;
  }

  public String driver() {
    return driver;
  }

  public Map<String, String> options() {
    return options;
  }

  public LoggingConfig deepMerge(LoggingConfig other) {
    return new LoggingConfig(
      other.driver() != null ? other.driver() : this.driver(),
      other.options() != null ? other.options() : this.options()
    );
  }

}
