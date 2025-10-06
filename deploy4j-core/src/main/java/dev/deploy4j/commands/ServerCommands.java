package dev.deploy4j.commands;

import dev.deploy4j.Cmd;
import dev.deploy4j.configuration.Deploy4jConfig;

public class ServerCommands {

  public static Cmd ensureRunDirectory( String runDirectory ) {
    return Cmd.cmd("mkdir", "-p", runDirectory );
  }

}
