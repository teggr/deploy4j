package dev.deploy4j.configuration;

import dev.deploy4j.raw.LoggingConfig;

public class Logging {

  private final LoggingConfig loggingConfig;

  public Logging(LoggingConfig loggingConfig) {
    this.loggingConfig = loggingConfig;
  }

  public String[] args() {
    // optionize those arguments
    return new String[]{};
  }

}
