package com.robintegg.deploy4j.cli;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public abstract class AbstractCliCommand implements CliCommand {

  private String command;

  protected void append(String arg) {
    command = command + " " + arg;
  }

  @Override
  public String toCli() {
    return command;
  }

}
