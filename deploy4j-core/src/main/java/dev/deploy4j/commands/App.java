package dev.deploy4j.commands;

import dev.deploy4j.Cmd;
import dev.deploy4j.configuration.Configuration;
import dev.deploy4j.configuration.Role;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static dev.deploy4j.Commands.*;

public class App extends BaseCommands {

  private static final List<String> ACTIVE_DOCKER_STATUSES = List.of("running", "restarting");

  private final String host;
  private final Role role;

  public App(Configuration config, Role role, String host) {
    super(config);
    this.role = role;
    this.host = host;
  }

  public Cmd listVersions() {
    return listVersions(List.of(), List.of());
  }

  public Cmd listVersions(List<String> dockerArgs, List<String> statuses) {
    return pipe(
      Cmd.cmd( "docker", "ps" )
        .args( filterArgs(statuses) )
        .args( dockerArgs )
        .args( "--format", "\"{{.Names}}\"" ),
      extractVersionFromName()
    );
  }

  public Cmd containerIdForVersion(String version) {
    return containerIdForVersion(version, false);
  }

  public Cmd containerIdForVersion(String version, boolean onlyRunning) {
    return containerIdFor(containerName(version), onlyRunning);
  }

  public String containerName() {
    return containerName(null);
  }

  private String containerName(String version) {
    return Stream.of(
      role.containerPrefix(),
      version != null ? version : config.version()
    ).filter(Objects::nonNull).collect(Collectors.joining("-"));
  }

  public Cmd renameContainer(String version, String newVersion) {
    return Cmd.cmd("docker", "rename",
      containerName(version),
      containerName(newVersion)
    );
  }

  public Cmd currentRunningVersion() {
    return pipe(
      currentRunningContainer("--format '{{.Names}}'"),
      extractVersionFromName()
    );
  }

  private Cmd extractVersionFromName() {
    return Cmd.cmd(
      "while read line; do echo ${line#" + role.containerPrefix() + "-}; done"
    );
  }

  public Cmd currentRunningContainerId() {
    return currentRunningContainer("--quiet");
  }

  public Cmd currentRunningContainer(String format) {
    return pipe(
      shell(
        chain(
          latestImageContainer(format),
          latestContainer(format)
        )
      ),
      Cmd.cmd("head", "-1")
    );
  }

  public Cmd latestImageContainer(String format) {
    return latestContainer(format, List.of("ancestor=$( " + String.join(" ", latestImageId().build()) + " )"));
  }

  private Cmd latestImageId() {
    return Cmd.cmd("docker", "image", "ls")
      .args(argumentize("--filter",
        List.of("reference=" + config.latestImage())
      )).
      args("--format", "'{{.ID}}'");
  }

  private Cmd latestContainer(String format) {
    return latestContainer(format, List.of());
  }

  private Cmd latestContainer(String format, List<String> filters) {
    return Cmd.cmd("docker", "ps", "--latest", format)
      .args(filterArgs(ACTIVE_DOCKER_STATUSES))
      .args(argumentize("--filter", filters));
  }

  private String[] filterArgs(List<String> statuses) {
    return argumentize(
      "--filter",
      filters(statuses)
    );
  }

  private List<String> filters(List<String> statuses) {
    List<String> filters = new ArrayList<>();
    filters.add("label=service=" + config.service());
    filters.add("label=destination=" + config.destination());
    if (role != null) {
      filters.add("label=role=" + role.name());
    }
    statuses.forEach(s -> filters.add("status=" + s));
    return filters;
  }

  public Cmd stop(String version) {
    return pipe(
      version != null ? containerIdForVersion(version) : currentRunningContainerId(),
      config.stopWaitTime() != null ? Cmd.cmd("docker", "stop", "-t", config.stopWaitTime()) : Cmd.cmd("docker", "stop")
    );
  }

  public Cmd run(String hostName) {

    Cmd cmd = Cmd.cmd("docker", "run")
      .args("--detach")
      .args("--restart", "unless-stopped")
      .args("--name", containerName());
    if (hostName != null) cmd = cmd.args("--hostname", hostName);
    cmd = cmd.args("-e", "DEPLOY4J_CONTAINER_NAME=\"" + containerName() + "\"")
      .args("-e", "DEPLOY4J_VERSION=\"" + config.version() + "\"")
      .args(role.envArgs(host))
      .args(role.healthCheckArgs())
      .args(role.loggingArgs())
      .args(config.volumeArgs())
      .args(role.assetVolumeArgs())
      .args(role.labelArgs())
      .args(role.optionArgs())
      .args(config.absoluteImage())
      .args(role.cmd());
    return cmd;

  }

}
