package dev.deploy4j.deploy.configuration.env;

import dev.deploy4j.deploy.Secrets;
import dev.deploy4j.deploy.configuration.Env;
import dev.deploy4j.deploy.configuration.raw.EnvironmentConfig;

public class Tag {

  private final String name;
  private final EnvironmentConfig config;
  private final Secrets secrets;

  public Tag(String name, EnvironmentConfig config, Secrets secrets) {
    this.name = name;
    this.config = config;
    this.secrets = secrets;
  }

  public Env env() {
    return new Env(config, secrets);
  }

  // attributes

  public String name() {
    return name;
  }

  public EnvironmentConfig config() {
    return config;
  }

}
