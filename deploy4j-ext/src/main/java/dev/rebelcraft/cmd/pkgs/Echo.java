package dev.rebelcraft.cmd.pkgs;

import dev.rebelcraft.cmd.FluentCmd;

public class Echo extends FluentCmd<Echo> {

  public static Echo echo() {
    return new Echo();
  }

  Echo() {
    super("echo");
  }

  public Echo message(String message) {
    super.args("\"" + message + "\"");
    return this;
  }

}
