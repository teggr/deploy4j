package dev.deploy4j.commands;

import dev.rebelcraft.cmd.Cmd;
import dev.deploy4j.configuration.Configuration;

public class ServerCommands extends BaseCommands {

  public ServerCommands(Configuration config) {
    super(config);
  }

  public Cmd ensureRunDirectory() {
    return makeDirectory(config.runDirectory())
      .description("ensure run directory");
  }

}
