package dev.rebelcraft.cmd.pkgs;

import dev.rebelcraft.cmd.FluentCmd;

public class Grep extends FluentCmd<Grep> {

  public static Grep grep() {
    return new Grep();
  }

  Grep() {
    super("grep");
  }

  public Grep search(String search) {
    super.args("'" + search + "'");
    return this;
  }

}
