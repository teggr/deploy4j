package dev.deploy4j.configuration.env;

import dev.deploy4j.configuration.Env;
import dev.deploy4j.raw.EnvironmentConfig;

public class Tag {

  private final String name;
  private final EnvironmentConfig config;

  public Tag(String name, EnvironmentConfig config) {
    this.name = name;
    this.config = config;
  }

  public Env env() {
    return new Env(config);
  }

  // attributes

  public String name() {
    return name;
  }

  public EnvironmentConfig config() {
    return config;
  }

}
