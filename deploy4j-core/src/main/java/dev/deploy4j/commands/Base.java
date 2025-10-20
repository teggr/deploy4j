package dev.deploy4j.commands;

import dev.deploy4j.Cmd;
import dev.deploy4j.Tags;
import dev.deploy4j.configuration.Configuration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class Base {

  protected static final String DOCKER_HEALTH_STATUS_FORMAT = "'{{if .State.Health}}{{.State.Health.Status}}{{else}}{{.State.Status}}{{end}}'";

  protected Configuration config;

  public Base(Configuration config) {
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
    Cmd cmd = Cmd.cmd("docker", "container", "ls");
    if (!onlyRunning) cmd = cmd.args("--all");
    cmd = cmd.args(
      "--filter",
      "name=^" + containerName + "$",
      "--quiet"
    ).description("container id for");
    return cmd;
  }

  public Cmd makeDirectoryFor(String remoteFile) {
    return makeDirectory(new File(remoteFile).getParent());
  }

  public Cmd makeDirectory(String path) {
    return Cmd.cmd("mkdir", "-p", path);
  }

  public Cmd removeDirectory(String path) {
    return Cmd.cmd("rm", "-r", path);
  }

  // private

  public Cmd combine(Cmd[] commands, String by) {

    List<String> list = Stream.of(commands)
      .filter(Objects::nonNull)
      .map(cmd -> cmd.args( by ) )
      .flatMap(cmd -> cmd.build().stream() )
      .toList();

    if(!list.isEmpty()) {
      list = new ArrayList<>(list);
      list.removeLast();
    }

    return Cmd.cmd( list.toArray( new String[0] ) );

  }

  // Overload with default separator

  protected Cmd combine(Cmd... commands) {
    return combine(commands, "&&");
  }

  protected Cmd chain(Cmd... commands) {
    return combine(commands, ";");
  }

  protected Cmd pipe(Cmd... commands) {
    return combine(commands, "|");
  }

  protected Cmd append(Cmd... commands) {
    return combine(commands, ">>");
  }

  protected Cmd write(Cmd... commands) {
    return combine(commands, ">");
  }

  protected Cmd any(Cmd... commands) {
    return combine(commands, "||");
  }

  protected Cmd xargs(Cmd cmd) {
    return Cmd.cmd("xargs")
      .args(cmd.build());
  }

  protected Cmd shell(Cmd command) {
    return Cmd.cmd(
      "sh", "-c",
      "'" + Stream.of(command).flatMap( Cmd -> command.build().stream() ).collect(Collectors.joining(" ")).replace("'", "'\\''") + "'"
    );
  }

  protected Cmd substitute(Cmd... commands) {
    return Cmd.cmd( "\\$\\(" + Stream.of( commands ).map( cmd ->cmd.build().get(0) ).collect(Collectors.joining(" ")) + "\\)" );
  }

  protected Cmd docker(String... args) {
    return Cmd.cmd("docker").args(args);
  }

  protected Cmd git(String[] args, String path) {
    Cmd git = Cmd.cmd("git");
    if(path != null) {
      git = git.args( "-C", path );
    }
    git = git.args( args );
    return git;
  }

  protected Tags tags(Map<String,String> details) {
    return Tags.fromConfig(config(), details);
  }

  // attributes

  public Configuration config() {
    return config;
  }

}
