package dev.deploy4j.commands;

import dev.deploy4j.Cmd;
import dev.deploy4j.configuration.Configuration;

public class Server {

  private final Configuration config;

  public Server(Configuration config) {
    this.config = config;
  }

  public Cmd ensureRunDirectory() {
    return Cmd.cmd("mkdir", "-p", config.runDirectory() ).description("ensure run directory");
  }

}
