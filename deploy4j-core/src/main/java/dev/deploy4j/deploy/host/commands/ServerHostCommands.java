package dev.deploy4j.deploy.host.commands;

import dev.rebelcraft.cmd.Cmd;
import dev.deploy4j.deploy.configuration.Configuration;

public class ServerHostCommands extends BaseHostCommands {

  public ServerHostCommands(Configuration config) {
    super(config);
  }

  public Cmd ensureRunDirectory() {
    return makeDirectory(config.runDirectory())
      .description("ensure run directory");
  }

}
