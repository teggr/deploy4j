package dev.deploy4j.commands;

import dev.deploy4j.Cmd;
import dev.deploy4j.configuration.Configuration;

import static dev.deploy4j.Commands.any;
import static dev.deploy4j.Commands.pipe;

public class Builder {

  private final Configuration configuration;

  public Builder(Configuration configuration) {
    this.configuration = configuration;
  }

  public  Cmd clean() {
    return Cmd.cmd("docker", "image", "rm", "--force", configuration.absoluteImage()).description("clean");
  }

  public  Cmd pull() {
    return Cmd.cmd("docker", "pull", configuration.absoluteImage()).description("pull");
  }

  public  Cmd validateImage() {
    return pipe(
      Cmd.cmd("docker", "inspect", "-f", "'{{ .Config.Labels.service }}'", configuration.absoluteImage()),
      any(
        Cmd.cmd("grep", "-x", configuration.absoluteImage()),
        Cmd.cmd("(echo \"Image " + configuration.absoluteImage() + " is missing the 'service' label\" && exit 1)")
      )
    ).description("validate image");
  }
}
