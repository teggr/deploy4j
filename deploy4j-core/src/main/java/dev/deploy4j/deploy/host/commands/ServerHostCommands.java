package dev.deploy4j.deploy.host.commands;

import dev.rebelcraft.cmd.Cmd;
import dev.deploy4j.deploy.configuration.Configuration;

import static dev.rebelcraft.cmd.Cmds.pipe;

public class ServerHostCommands extends BaseHostCommands {

  public ServerHostCommands(Configuration config) {
    super(config);
  }

  public Cmd ensureRunDirectory() {
    return makeDirectory(config().runDirectory())
      .description("ensure run directory");
  }

  public Cmd removeAppDirectory() {
    return removeDirectory(config().appDirectory());
  }

  public Cmd appDirectoryCount() {
    return pipe(
      Cmd.cmd("ls", config().appsDirectory()),
      Cmd.cmd("wc", "-l")
    );
  }

}
