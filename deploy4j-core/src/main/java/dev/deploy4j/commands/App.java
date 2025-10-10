package dev.deploy4j.commands;

import dev.deploy4j.Cmd;
import dev.deploy4j.configuration.Configuration;
import dev.deploy4j.configuration.Role;
import org.apache.commons.lang.StringUtils;

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
    return listVersions(List.of(), List.of())
      .description("list versions");
  }

  public Cmd listVersions(List<String> dockerArgs, List<String> statuses) {
    return pipe(
      Cmd.cmd("docker", "ps")
        .args(filterArgs(statuses))
        .args(dockerArgs)
        .args("--format", "\"{{.Names}}\""),
      extractVersionFromName()
    ).description("list versions");
  }

  public Cmd containerIdForVersion(String version) {
    return containerIdForVersion(version, false);
  }

  public Cmd containerIdForVersion(String version, boolean onlyRunning) {
    return containerIdFor(containerName(version), onlyRunning)
      .description("container id for version");
  }

  public String containerName() {
    return containerName(null);
  }

  private String containerName(String version) {
    return Stream.of(
        role.containerPrefix(),
        StringUtils.isNotBlank(version) ? version : config.version()
      ).filter(Objects::nonNull)
      .collect(Collectors.joining("-")).trim();
  }

  public Cmd renameContainer(String version, String newVersion) {
    return Cmd.cmd("docker", "rename",
      containerName(version),
      containerName(newVersion)
    ).description("rename container");
  }

  public Cmd currentRunningVersion() {
    return pipe(
      currentRunningContainer("--format '{{.Names}}'"),
      extractVersionFromName()
    ).description("current running version");
  }

  private Cmd extractVersionFromName() {
    return Cmd.cmd(
      "while read line; do echo ${line#" + role.containerPrefix() + "-}; done"
    ).description("extract version from container name");
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
    ).description("current running container");
  }

  public Cmd latestImageContainer(String format) {
    return latestContainer(format, List.of("ancestor=$( " + String.join(" ", latestImageId().build()) + " )"))
      .description("latest image container");
  }

  private Cmd latestImageId() {
    return Cmd.cmd("docker", "image", "ls")
      .args(argumentize("--filter",
        List.of("reference=" + config.latestImage())
      )).
      args("--format", "'{{.ID}}'")
      .description("latest image id");
  }

  private Cmd latestContainer(String format) {
    return latestContainer(format, List.of());
  }

  private Cmd latestContainer(String format, List<String> filters) {
    return Cmd.cmd("docker", "ps", "--latest", format)
      .args(filterArgs(ACTIVE_DOCKER_STATUSES))
      .args(argumentize("--filter", filters))
      .description("latest container");
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
      xargs(config.stopWaitTime() != null ? Cmd.cmd("docker", "stop", "-t", config.stopWaitTime()) : Cmd.cmd("docker", "stop"))
    ).description("stop container");
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
      .args(role.cmd())
      .description("run container");
    return cmd;

  }

  public Cmd tagLatestImage() {
    return Cmd.cmd("docker", "tag")
      .args(config.absoluteImage())
      .args(config.latestImage())
      .description("tag latest image");
  }
}
