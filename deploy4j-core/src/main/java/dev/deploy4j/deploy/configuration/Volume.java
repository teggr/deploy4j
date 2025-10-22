package dev.deploy4j.deploy.configuration;

import dev.deploy4j.deploy.utils.file.File;

import java.nio.file.Paths;

import static dev.rebelcraft.cmd.CmdUtils.argumentize;

public class Volume {

  private final String hostPath;
  private final String containerPath;

  public Volume(String hostPath, String containerPath) {
    this.hostPath = hostPath;
    this.containerPath = containerPath;
  }

  public String[] dockerArgs() {
    return argumentize("--volume", "%s:%s".formatted(hostPathForDockerVolume(), containerPath()));
  }

  // private

  private String hostPathForDockerVolume() {
    if (Paths.get(hostPath()).isAbsolute()) {
      return hostPath();
    } else {
      return File.join("$(pwd)", hostPath());
    }
  }

  // attributes

  public String hostPath() {
    return hostPath;
  }

  public String containerPath() {
    return containerPath;
  }

}
