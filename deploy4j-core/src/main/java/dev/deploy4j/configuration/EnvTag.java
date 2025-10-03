package dev.deploy4j.configuration;

import java.util.Map;

import static dev.deploy4j.Commands.argumentize;

public record EnvTag(String name, Map<String, String> env) {
  public void merge(EnvTag envTag) {
    this.env.putAll(envTag.env());
  }

  public String[] args() {
    return argumentize("--env", this.env);
  }
}
