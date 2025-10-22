package dev.deploy4j.deploy.raw;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BootConfig {

  private final String limit;
  private final String wait;

  @JsonCreator
  public BootConfig(
    @JsonProperty("limit") String limit,
    @JsonProperty("wait") String wait
  ) {
    this.limit = limit;
    this.wait = wait;
  }

  public BootConfig() {
    this.limit = null;
    this.wait = null;
  }

  public String limit() {
    return limit;
  }

  public String waiter() {
    return wait;
  }

}
