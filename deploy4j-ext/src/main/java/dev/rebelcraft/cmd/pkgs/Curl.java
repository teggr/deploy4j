package dev.rebelcraft.cmd.pkgs;

import dev.rebelcraft.cmd.FluentCmd;

public class Curl extends FluentCmd<Curl> {

  public static Curl curl() {
    return new Curl();
  }

  Curl() {
    super("curl");
  }

  public Curl options(String... args) {
    super.args(args);
    return this;
  }

  public Curl url(String url) {
    super.args(url);
    return this;
  }

}
