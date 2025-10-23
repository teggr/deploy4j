package dev.deploy4j.deploy.host.commands;

import dev.deploy4j.deploy.configuration.Configuration;
import dev.rebelcraft.cmd.Cmd;
import dev.rebelcraft.cmd.Cmds;

import static dev.rebelcraft.cmd.Cmds.*;
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
    return docker().args(config().absoluteImage()).description("pull");
  }

  public Cmd validateImage() {
    return pipe(
      docker().args("-f", "'{{ .Config.Labels.service }}'", config().absoluteImage()),
      any(
        Cmd.cmd("grep", "-x", config().absoluteImage()),
        Cmd.cmd("(echo \"Image " + config().absoluteImage() + " is missing the 'service' label\" && exit 1)")
      )
    ).description("validate image");
  }
}
