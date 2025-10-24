package dev.deploy4j.deploy.host.commands;

import dev.deploy4j.deploy.configuration.Configuration;
import dev.deploy4j.deploy.configuration.Role;
import dev.rebelcraft.cmd.Cmd;
import org.apache.commons.lang.StringUtils;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static dev.rebelcraft.cmd.CmdUtils.argumentize;
import static dev.rebelcraft.cmd.Cmds.*;
import static dev.rebelcraft.cmd.pkgs.Docker.docker;
import static dev.rebelcraft.cmd.pkgs.Grep.grep;

public class AppHostCommands extends BaseHostCommands {

  private static final List<String> ACTIVE_DOCKER_STATUSES = List.of("running", "restarting");

  private final String host;
  private final Role role;

  public AppHostCommands(Configuration config, Role role, String host) {
    super(config);
    this.role = role;
    this.host = host;
  }

  public Cmd run(String hostName) {

    Cmd cmd = docker().run()
      .args("--detach")
      .args("--restart", "unless-stopped")
      .args("--name", containerName());
    if (hostName != null) cmd = cmd.args("--hostname", hostName);
    cmd = cmd.args("-e", "DEPLOY4J_CONTAINER_NAME=\"" + containerName() + "\"")
      .args("-e", "DEPLOY4J_VERSION=\"" + config().version() + "\"")
      .args(role().envArgs(host()))
      //.args(role().healthCheckArgs()) TODO: bring this back - acuator and possibly ignore
      .args(role().loggingArgs())
      .args(config().volumeArgs())
      .args(role().assetVolumeArgs())
      .args(role().labelArgs())
      .args(role().optionArgs())
      .args(config().absoluteImage())
      .args(role().cmd())
      .description("run container");
    return cmd;

  }

  public Cmd start() {
    return docker().start().args(containerName());
  }

  public Cmd status(String version) {
    return pipe(
      containerIdForVersion(version),
      xargs(docker().inspect().args("--format", DOCKER_HEALTH_STATUS_FORMAT))
    );
  }

  public Cmd stop() {
    return stop(null);
  }

  public Cmd stop(String version) {
    return pipe(
      version != null ? containerIdForVersion(version) : currentRunningContainerId(),
      xargs(config.stopWaitTime() != null ? docker().stop().args("-t", config().stopWaitTime().toString()) : docker().stop())
    ).description("stop container");
  }

  public Cmd info() {
    return docker().ps()
      .args(filterArgs(List.of()));
  }

  public Cmd currentRunningContainerId() {
    return currentRunningContainer("--quiet");
  }

  public Cmd containerIdForVersion(String version) {
    return containerIdForVersion(version, false);
  }

  public Cmd containerIdForVersion(String version, boolean onlyRunning) {
    return containerIdFor(containerName(version), onlyRunning)
      .description("container id for version");
  }

  public Cmd currentRunningVersion() {
    return pipe(
      currentRunningContainer("--format '{{.Names}}'"),
      extractVersionFromName()
    ).description("current running version");
  }

  public Cmd listVersions() {
    return listVersions(List.of(), List.of())
      .description("list versions");
  }

  public Cmd listVersions(List<String> dockerArgs, List<String> statuses) {
    return pipe(
      docker().ps()
        .args(filterArgs(statuses))
        .args(dockerArgs)
        .args("--format", "\"{{.Names}}\""),
      extractVersionFromName()
    ).description("list versions");
  }

  public Cmd makeEnvDirectory() {
    return makeDirectory(role.env(host).secretsDirectory());
  }

  public Cmd removeEnvFile() {
    return Cmd.cmd("rm", "-f", role().env(host()).secretsFile());
  }

  // private

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

  private Cmd latestImageId() {
    return docker().image().args("ls")
      .args(argumentize("--filter",
        List.of("reference=" + config().latestImage())
      )).
      args("--format", "'{{.ID}}'")
      .description("latest image id");
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

  private Cmd latestContainer(String format) {
    return latestContainer(format, List.of());
  }

  private Cmd latestContainer(String format, List<String> filters) {
    return docker().ps().args("--latest", format)
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

  private Cmd extractVersionFromName() {
    return Cmd.cmd(
      "while read line; do echo ${line#" + role.containerPrefix() + "-}; done"
    ).description("extract version from container name");
  }

  private List<String> filters(List<String> statuses) {
    List<String> filters = new ArrayList<>();
    filters.add("label=service=" + config().service());
    if(config().destination() != null){
      filters.add("label=destination=" + config().destination());
    }
    if (role != null) {
      filters.add("label=role=" + role().name());
    }
    statuses.forEach(s -> filters.add("status=" + s));
    return filters;
  }

  // includes

  // assets

  public Cmd extractAssets() {

    String assetContainer = role().containerPrefix() + "-assets";

    return combine(
      "&&", new Cmd[]{
        makeDirectory(role().assetExtractedPath(null)),
        any(docker().stop().args("-t 1", assetContainer, "2> /dev/null"), Cmd.cmd("true")),
        docker().run().args("--name", assetContainer, "--detach", "--rm", config().absoluteImage(), "sleep 1000000"),
        docker().cp().args("-L", assetContainer + ":" + role().assetPath() + "/.", role().assetExtractedPath(null)),
        docker().stop().args("-t 1", assetContainer)
      }

    );
  }

  public Cmd syncAssetVolumes(String oldVersion) {

    List<Cmd> cmds = new ArrayList<>();

    String newExtractedPath = role().assetExtractedPath(config().version());
    String newVolumePath = role().assetVolume(null).hostPath();
    cmds.add(makeDirectory(newVolumePath));
    cmds.add(copyContents(newExtractedPath, newVolumePath, false));

    if (oldVersion != null) {
      String oldExtractedPath = role().assetExtractedPath(oldVersion);
      String oldVolumePath = role().assetVolume(oldVersion).hostPath();
      cmds.add(copyContents(newExtractedPath, oldVolumePath, true));
      cmds.add(copyContents(oldExtractedPath, newVolumePath, true));
    }

    return chain(cmds.toArray(new Cmd[0]));

  }

  public Cmd cleanUpAssets() {
    return chain(
      findAndRemoveOlderSiblings(role().assetExtractedPath(null)),
      findAndRemoveOlderSiblings(role().assetVolumePath(null))
    );
  }

  // private

  private Cmd findAndRemoveOlderSiblings(String path) {
    return Cmd.cmd("find")
      .args(Paths.get(path).toString())
      .args("-maxdepth 1")
      .args("-name", "'" + role().containerPrefix() + "-*'")
      .args("!", "-name", Paths.get(path).toString())
      .args("-exec rm -rf \"{}\" +");
  }

  private Cmd copyContents(String source, String destination, Boolean continueOnError) {
    Cmd cmd = Cmd.cmd("cp", "-rnT", source, destination);
    if (continueOnError) {
      cmd = cmd.args("|| true");
    }
    return cmd;
  }

  // containers

  private static final String DOCKER_HEALTH_LOG_FORMAT = "'{{json .State.Health}}'";

  public Cmd listContainers() {
    return docker().ls().args("--all")
      .args(filterArgs(List.of()));
  }

  public Cmd listContainerNames() {
    return docker().ls().args("--all")
      .args("--format", "'{{ .Names }}'");
  }

  public Cmd removeContainer(String version) {
    return pipe(
      containerIdFor(containerName(version), false),
      xargs(docker().container().args("rm"))
    );
  }

  public Cmd renameContainer(String version, String newVersion) {
    return docker().rename().args(
      containerName(version),
      containerName(newVersion)
    ).description("rename container");
  }

  public Cmd removeContainers() {
    return docker().container().args("prune", "--force")
      .args(filterArgs(List.of()));
  }

  public Cmd containerHealthLog(String version) {
    return pipe(
      containerIdFor(containerName(version), false),
      xargs(docker().inspect().args("--format", DOCKER_HEALTH_LOG_FORMAT))
    ).description("container health log");
  }

  // cord

  public Cmd cord(String version) {
    return pipe(
      docker().inspect().args("-f '{{ range .Mounts }}{{printf \"%s %s\\n\" .Source .Destination}}{{ end }}'", containerName(version)),
      Cmd.cmd("awk", "'$2 == \"%s\" {print $1}'".formatted(role().cordVolume().containerPath()))
    ).description("cord");
  }

  public Cmd tieCord(String cord) {
    return createEmptyFile(cord);
  }

  public Cmd cutCord(String cord) {
    return removeDirectory(cord);
  }

  // private

  public Cmd createEmptyFile(String file) {
    return chain(
      makeDirectoryFor(file),
      Cmd.cmd("touch", file)
    );
  }

  // execution

  public Cmd executeInExistingContainer(String command, Map<String, String> env) {
    return docker().exec()
      // TODO interactive mode
      .args(argumentize("--env", env))
      .args(containerName())
      .args(command);
  }

  // TODO: execute in new container
  // TODO: execute in existing container over ssh
  // TODO: execute in new container over ssh

  // images

  public Cmd listImages() {
    return docker().image().args("ls")
      .args(config.repository());
  }

  public Cmd removeImages() {
    return docker().image().args("prune", "--all", "--force")
      .args(filterArgs(List.of()));
  }

  public Cmd tagLatestImage() {
    return docker().tag()
      .args(config.absoluteImage())
      .args(config.latestImage())
      .description("tag latest image");
  }

  // logging

  public Cmd logs(String version, String since, String lines, String grep, String grepOptions) {
    return pipe(
      docker().logs().args("traefik", since != null ? "--since " + since : null, lines != null ? "--tail " + lines : null, "--timestamps", "2>&1"),
      grep != null ? grep().search(grep)
        .args(grepOptions) : null
    ).description("logs");
  }

  // TODO: follow logs

  // attributes

  public Role role() {
    return role;
  }

  public String host() {
    return host;
  }
}
