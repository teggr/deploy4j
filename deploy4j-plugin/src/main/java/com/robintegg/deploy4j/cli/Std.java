package com.robintegg.deploy4j.cli;

public class Std {
  public static CliCommand pipe(String from, String to) {
    return () -> from + " | " + to;
  }

  public static CliCommand or(CliCommand a, CliCommand b) {
    return () -> a.toCli() + " || " + b.toCli();
  }

  public static Mkdir mkdir() {
    return new Mkdir();
  }

  public static class Mkdir extends AbstractCliCommand {

    public Mkdir() {
      super("mkdir");
    }


    public Mkdir withParentDirectories() {
      append("-p");
      return this;
    }

    public Mkdir directory(String dir) {
      append(dir);
      return this;
    }
  }

  public static CliCommand superUser() {
    return () -> "[ \"${EUID:-$id -u)}\" -eq 0 ]";
  }

}
