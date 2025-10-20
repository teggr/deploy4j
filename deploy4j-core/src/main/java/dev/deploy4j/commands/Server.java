package dev.deploy4j.commands;

import dev.deploy4j.Cmd;
import dev.deploy4j.configuration.Configuration;

public class Server extends Base {

  public Server(Configuration config) {
    super(config);
  }

  public Cmd ensureRunDirectory() {
    return makeDirectory(config.runDirectory())
      .description("ensure run directory");
  }

}
