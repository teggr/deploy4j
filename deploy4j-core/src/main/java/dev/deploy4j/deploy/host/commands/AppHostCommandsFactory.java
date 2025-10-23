package dev.deploy4j.deploy.host.commands;

import dev.deploy4j.deploy.configuration.Configuration;
import dev.deploy4j.deploy.configuration.Role;

public class AppHostCommandsFactory {

  private final Configuration config;

  public AppHostCommandsFactory(Configuration config) {
    this.config = config;
  }

  public AppHostCommands app(Role role, String host) {
    return new AppHostCommands(config, role, host);
  }

}
