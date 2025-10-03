package dev.deploy4j.commands;

import dev.deploy4j.Cmd;
import dev.deploy4j.configuration.Deploy4jConfig;

public class ServerCommands {

  private final Deploy4jConfig configuration;

  public ServerCommands(Deploy4jConfig configuration) {
    this.configuration = configuration;
  }

  public Cmd ensureRunDirectory() {
      return Cmd.cmd("mkdir", "-p", configuration.runDirectory() );
  }

}
