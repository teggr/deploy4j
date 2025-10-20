package dev.deploy4j.commands;

import dev.deploy4j.Cmd;
import dev.deploy4j.configuration.Configuration;

import java.io.File;

public abstract class Base {

  protected Configuration config;

  public Base(Configuration config) {
    this.config = config;
  }

  public Cmd makeDirectoryFor(String remoteFile) {
    return makeDirectory( new File(remoteFile).getParent() );
  }

  public Cmd makeDirectory(String path) {
    return Cmd.cmd("mkdir", "-p", path );
  }


}
