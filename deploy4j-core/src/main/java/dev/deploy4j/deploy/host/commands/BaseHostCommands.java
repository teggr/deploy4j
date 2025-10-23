package dev.deploy4j.deploy.host.commands;

import dev.deploy4j.deploy.Tags;
import dev.deploy4j.deploy.configuration.Configuration;
import dev.rebelcraft.cmd.Cmd;
import dev.rebelcraft.cmd.Cmds;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static dev.rebelcraft.cmd.pkgs.Docker.docker;

public abstract class BaseHostCommands {

  protected static final String DOCKER_HEALTH_STATUS_FORMAT = "'{{if .State.Health}}{{.State.Health.Status}}{{else}}{{.State.Status}}{{end}}'";

  protected Configuration config;

  public BaseHostCommands(Configuration config) {
    this.config = config;
  }

  public String runOverSsh(String[] command, String host) {
    String cmd = "ssh";
    if (config().ssh().proxy() != null) {
      // TODO: something with a proxy jump or command
    }
    cmd += " -t %s@%s".formatted(
      config().ssh().user(),
      host
    );
    cmd += " -p %s".formatted(config().ssh().port());
    cmd += " '" + Stream.of(command).collect(Collectors.joining(" ")).replaceAll("'", "'\\\\''") + "'";
    return cmd;
  }

  public Cmd containerIdFor(String containerName, boolean onlyRunning) {
    Cmd cmd = docker().container().args("ls");
    if (!onlyRunning) cmd = cmd.args("--all");
    cmd = cmd.args(
      "--filter",
      "name=^" + containerName + "$",
      "--quiet"
    ).description("container id for");
    return cmd;
  }

  public Cmd makeDirectoryFor(String remoteFile) {
    return Cmds.makeDirectoryFor(remoteFile);
  }

  public Cmd makeDirectory(String path) {
    return Cmds.makeDirectory(path);
  }

  public Cmd removeDirectory(String path) {
    return Cmds.removeDirectory(path);
  }

  // private

  protected Cmd git(String[] args, String path) {
    Cmd git = Cmd.cmd("git");
    if (path != null) {
      git = git.args("-C", path);
    }
    git = git.args(args);
    return git;
  }

  protected Tags tags(Map<String, String> details) {
    return Tags.fromConfig(config(), details);
  }

  // attributes

  public Configuration config() {
    return config;
  }

  protected String sensitive(String s) {
    return s;
  }
}
