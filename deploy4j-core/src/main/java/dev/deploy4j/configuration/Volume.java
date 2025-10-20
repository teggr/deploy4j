package dev.deploy4j.configuration;

import dev.deploy4j.file.Deploy4jFile;

import java.nio.file.Paths;

import static dev.deploy4j.Commands.argumentize;

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
      return Deploy4jFile.join("$(pwd)", hostPath());
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
