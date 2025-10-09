package dev.deploy4j;

import dev.deploy4j.configuration.Deploy4jConfig;

public class BaseCommands {

  protected final Deploy4jConfig config;

  public BaseCommands(Deploy4jConfig config) {
    this.config = config;
  }

  public Cmd containerIdFor(String containerName, boolean onlyRunning) {
    Cmd cmd = Cmd.cmd("docker", "container", "ls");
    if (!onlyRunning) cmd = cmd.args("--all");
    cmd = cmd.args(
      "--filter",
      "name=^" + containerName + "$",
      "--quiet"
    );
    return cmd;
  }

}
