package dev.deploy4j.commands;

import dev.deploy4j.Cmd;
import dev.deploy4j.configuration.Configuration;

public class BaseCommands {

  protected final Configuration config;

  public BaseCommands(Configuration config) {
    this.config = config;
  }

  public Cmd containerIdFor(String containerName, boolean onlyRunning) {
    Cmd cmd = Cmd.cmd("docker", "container", "ls");
    if (!onlyRunning) cmd = cmd.args("--all");
    cmd = cmd.args(
      "--filter",
      "name=^" + containerName + "$",
      "--quiet"
    ).description("container id for");
    return cmd;
  }

}
