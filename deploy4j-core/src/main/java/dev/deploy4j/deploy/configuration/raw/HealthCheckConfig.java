package dev.deploy4j.deploy.configuration.raw;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class HealthCheckConfig {

  private final String cmd;
  private final String interval;
  private final Integer maxAttempts;
  private final Integer port;
  private final String path;
  private final String cord;
  private final Integer logLines;

  @JsonCreator
  public HealthCheckConfig(
    @JsonProperty("cmd") String cmd,
    @JsonProperty("interval") String interval,
    @JsonProperty("max_attempts") Integer maxAttempts,
    @JsonProperty("port") Integer port,
    @JsonProperty("path") String path,
    @JsonProperty("cord") String cord,
    @JsonProperty("log_lines") Integer logLines
  ) {
    this.cmd = cmd;
    this.interval = interval;
    this.maxAttempts = maxAttempts;
    this.port = port;
    this.path = path;
    this.cord = cord;
    this.logLines = logLines;
  }

  public HealthCheckConfig() {
    this.cmd = null;
    this.interval = null;
    this.maxAttempts = 0;
    this.port = null;
    this.path = null;
    this.cord = null;
    this.logLines = null;
  }

  public String cmd() {
    return cmd;
  }

  public String interval() {
    return interval;
  }

  public Integer maxAttempts() {
    return maxAttempts;
  }

  public Integer port() {
    return port;
  }

  public String path() {
    return path;
  }

  public String cord() {
    return cord;
  }

  public Integer logLines() {
    return logLines;
  }

  public HealthCheckConfig deepMerge(HealthCheckConfig other) {
    return new HealthCheckConfig(
      other.cmd() != null ? other.cmd() : this.cmd(),
      other.interval() != null ? other.interval() : this.interval(),
      other.maxAttempts() != 0 ? other.maxAttempts() : this.maxAttempts(),
      other.port() != null ? other.port() : this.port(),
      other.path() != null ? other.path() : this.path(),
      other.cord() != null ? other.cord() : this.cord(),
      other.logLines() != null ? other.logLines() : this.logLines()
    );
  }
}
