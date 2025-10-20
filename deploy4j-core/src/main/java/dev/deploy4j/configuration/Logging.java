package dev.deploy4j.configuration;

import dev.deploy4j.raw.LoggingConfig;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static dev.deploy4j.Utils.argumentize;
import static dev.deploy4j.Utils.optionize;

public class Logging {

  private final LoggingConfig loggingConfig;
  private final String context;

  public Logging(LoggingConfig loggingConfig, String context) {
    this.loggingConfig = loggingConfig != null ? loggingConfig : new LoggingConfig();
    this.context = context != null ? context : "logging";
  }

  public String driver() {
    return loggingConfig().driver();
  }

  public Map<String, String> options() {
    return loggingConfig().options() != null ? loggingConfig().options() : Map.of();
  }

  public Logging merge(Logging other) {
    return new Logging(
      loggingConfig().deepMerge(other.loggingConfig()),
      null
    );
  }

  public String[] args() {
    if (driver() != null || options() != null) {
      List<String> args = new ArrayList<>();
      args.addAll(optionize(Map.of("log-driver", driver())));
      args.addAll(Arrays.stream(argumentize("--log-opt", options())).toList());
      return args.toArray(new String[0]);
    } else {
      return argumentize("--log-opt", Map.of("max-size", "10m"));
    }
  }

  // attributes

  public LoggingConfig loggingConfig() {
    return loggingConfig;
  }

}
