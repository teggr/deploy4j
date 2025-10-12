package dev.deploy4j.configuration;

import java.util.HashMap;
import java.util.Map;

import static dev.deploy4j.Commands.argumentize;

public class Env {

  private final Map<String, String> config;

  // TODO: secrets file + context
  public Env(Map<String, String> config) {
    this.config = config; // clear, secret
  }

  public String[] args() {
    // --env-file secrets
    return argumentize("--env", this.config);
  }

  public Env merge(Env other) {
    Map<String, String> merged = new HashMap<>();
    merged.putAll(config);
    merged.putAll(other.config);
    return new Env(merged);
  }

}
