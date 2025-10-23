package dev.deploy4j.deploy.host.commands;

import dev.deploy4j.deploy.configuration.Configuration;

public class AccessoryHostCommandsFactory {

  private final Configuration config;

  public AccessoryHostCommandsFactory(Configuration config) {
    this.config = config;
  }

  public AccessoryHostCommands accessory(String name) {
    return new AccessoryHostCommands(config, name);
  }

}
