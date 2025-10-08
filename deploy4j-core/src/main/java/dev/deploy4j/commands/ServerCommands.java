package dev.deploy4j.commands;

import dev.deploy4j.Cmd;
import dev.deploy4j.Context;
import dev.deploy4j.configuration.Deploy4jConfig;

public class ServerCommands {

  private final Deploy4jConfig config;

  public ServerCommands(Deploy4jConfig config) {
    this.config = config;
  }

  public Cmd ensureRunDirectory() {
    return Cmd.cmd("mkdir", "-p", config.runDirectory() );
  }

}
