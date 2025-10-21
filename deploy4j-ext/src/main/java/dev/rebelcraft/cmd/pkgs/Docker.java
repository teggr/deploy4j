package dev.rebelcraft.cmd.pkgs;

import dev.rebelcraft.cmd.FluentCmd;

public class Docker extends FluentCmd<Docker> {

  public static Docker docker() {
    return new Docker();
  }

  Docker() {
    super("docker");
  }

  public Docker v() {
    super.args("-v");
    return this;
  }

  public Docker config(String config) {
    super.args("--config", config);
    return this;
  }

  public Docker container() {
    return this.args("container");
  }

  public Docker cp() {
    return this.args("cp");
  }

  public Docker image() {
    return this.args("image");
  }

  public Docker exec() {
    return this.args("exec");
  }

  public Docker inspect() {
    return this.args("inspect");
  }

  public Docker login() {
    return this.args("login");
  }

  public Docker logout() {
    return this.args("logout");
  }

  public Docker logs() {
    return this.args("logs");
  }

  public Docker ls() {
    return this.args("ls");
  }

  public Docker ps() {
    return this.args("ps");
  }

  public Docker pull() {
    return this.args("pull");
  }

  public Docker rename() {
    return this.args("rename");
  }

  public Docker run() {
    return this.args("run");
  }

  public Docker start() {
    return this.args("start");
  }

  public Docker stop() {
    return this.args("stop");
  }

  public Docker tag() {
    return this.args("tag");
  }

  public Docker version() {
    return this.args("version");
  }

}
