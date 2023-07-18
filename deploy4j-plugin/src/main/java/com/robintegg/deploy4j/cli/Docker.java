package com.robintegg.deploy4j.cli;

import static java.lang.String.format;

public class Docker {

  public static CliCommand version() {
    return () -> "docker -v";
  }

  public static Build build() {
    return new Build();
  }

  public static class Build extends AbstractCliCommand {

    public Build() {
      super("docker build");
    }

    public Build forceRm() {
      append("--force-rm");
      return this;
    }

    public Build quiet() {
      append("--quiet");
      return this;
    }

    public Build tag(String tag) {
      append("-t " + tag);
      return this;
    }

    public Build file(String file) {
      append("--file " + file);
      return this;
    }

    public Build context(String context) {
      append(context);
      return this;
    }

  }

  public static Run run() {
    return new Run();
  }

  public static class Run extends AbstractCliCommand {

    public Run() {
      super("docker run");
    }

    public Run name(String name) {
      append("--name " + name);
      return this;
    }

    public Run detach() {
      append("--detach");
      return this;
    }

    public Run restartUnlessStopped() {
      append("--restart unless-stopped");
      return this;
    }

    public Run publish(int port) {
      append("--publish " + port + ":" + port);
      return this;
    }

    public Run volume(String volumes) {
      append("--volume " + volumes);
      return this;
    }

    public Run env(String var, String val) {
      append("-e " + var + "=" + val);
      return this;
    }

    public Run image(String image) {
      append(image);
      return this;
    }

    public Run arg(String arg) {
      append(arg);
      return this;
    }

  }

  public static Start start() {
    return new Start();
  }

  public static class Start extends AbstractCliCommand {

    public Start() {
      super("docker start");
    }

    public Start container(String containerName) {
      append(containerName);
      return this;
    }

  }
}
