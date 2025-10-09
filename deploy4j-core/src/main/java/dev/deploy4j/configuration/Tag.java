package dev.deploy4j.configuration;

import java.util.Map;

import static dev.deploy4j.Commands.argumentize;

public class Tag {

  private final String name;
  private final Map<String, String> config;

  public Tag(String name, Map<String, String> config) {
    this.name = name;
    this.config = config;
  }

  public String name() {
    return name;
  }

  public Map<String, String> config() {
    return config;
  }

  public Env env() {
    return new Env(config);
  }

}
