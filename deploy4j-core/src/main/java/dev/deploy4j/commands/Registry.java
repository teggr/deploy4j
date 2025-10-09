package dev.deploy4j.commands;

import dev.deploy4j.Cmd;
import dev.deploy4j.configuration.Configuration;

public class Registry {

  private final Configuration config;

  public Registry(Configuration config) {
    this.config = config;
  }

  public Cmd login() {
    return Cmd.cmd("docker login")
      .args(config.registry().server())
      .args("-u", config.registry().username()) // redact
      .args("-p", config.registry().password()); // redact
  }

  public Cmd logout() {
    return Cmd.cmd("docker logout")
      .args(config.registry().server());
  }

}
