package dev.deploy4j.deploy.host.commands;

import dev.deploy4j.deploy.configuration.Configuration;
import dev.rebelcraft.cmd.Cmd;

import static dev.rebelcraft.cmd.pkgs.Docker.docker;

public class BuilderHostCommands extends BaseHostCommands {

  // TODO:@ builder base
  public BuilderHostCommands(Configuration config) {
    super(config);
  }

  public Cmd clean() {
    return docker().image().args("rm", "--force", config().absoluteImage()).description("clean");
  }

  public Cmd pull() {
    return docker().pull().args(config().absoluteImage()).description("pull");
  }
}
