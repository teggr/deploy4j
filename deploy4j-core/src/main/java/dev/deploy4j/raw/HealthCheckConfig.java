package dev.deploy4j.raw;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class HealthCheckConfig {

  private final String cmd;
  private final String interval;
  private final int maxAttempts;
  private final String port;
  private final String path;
  private final String cord;
  private final Integer logLines;

  @JsonCreator
  public HealthCheckConfig(
    @JsonProperty("cmd") String cmd,
    @JsonProperty("interval") String interval,
    @JsonProperty("max_attempts") int maxAttempts,
    @JsonProperty("port") String port,
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

  public String cmd() {
    return cmd;
  }

  public String interval() {
    return interval;
  }

  public int maxAttempts() {
    return maxAttempts;
  }

  public String port() {
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

}
