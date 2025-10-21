package dev.rebelcraft.cmd.pkgs;

import dev.rebelcraft.cmd.FluentCmd;

public class Wget extends FluentCmd<Wget> {

  public static Wget wget() {
    return new Wget();
  }

  Wget() {
    super("wget");
  }

  public Wget options(String... options) {
    super.args(options);
    return this;
  }

  public Wget url(String url) {
    super.args(url);
    return this;
  }

}
