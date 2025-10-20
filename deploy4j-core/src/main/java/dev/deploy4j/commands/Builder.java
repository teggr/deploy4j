package dev.deploy4j.commands;

import dev.deploy4j.Cmd;
import dev.deploy4j.configuration.Configuration;

public class Builder extends Base {

  // TODO:@ builder base
  public Builder(Configuration config) {
   super(config);
  }

  public  Cmd clean() {
    return Cmd.cmd("docker", "image", "rm", "--force", config().absoluteImage()).description("clean");
  }

  public  Cmd pull() {
    return Cmd.cmd("docker", "pull", config().absoluteImage()).description("pull");
  }

  public  Cmd validateImage() {
    return pipe(
      Cmd.cmd("docker", "inspect", "-f", "'{{ .Config.Labels.service }}'", config().absoluteImage()),
      any(
        Cmd.cmd("grep", "-x", config().absoluteImage()),
        Cmd.cmd("(echo \"Image " + config().absoluteImage() + " is missing the 'service' label\" && exit 1)")
      )
    ).description("validate image");
  }
}
