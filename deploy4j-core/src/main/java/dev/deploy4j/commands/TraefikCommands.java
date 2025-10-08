package dev.deploy4j.commands;

import dev.deploy4j.Cmd;
import dev.deploy4j.Commands;
import dev.deploy4j.configuration.Deploy4jConfig;

import java.util.List;

import static dev.deploy4j.Commands.*;

public class TraefikCommands {

  private final Deploy4jConfig config;

  public TraefikCommands(Deploy4jConfig config) {
    this.config = config;
  }

  public Cmd run() {
    return Cmd.cmd("docker", "run")
      .args("--name", "traefik")
      .args("--detach")
      .args("--restart", "unless-stopped")
      .args(publishArgs())
      .args("--volume", "/var/run/docker.sock:/var/run/docker.sock")
      .args(envArgs())
      .args(config.loggingArgs())
      .args(labelArgs())
      .args(dockerOptionsArgs())
      .args(config.traefik().image())
      .args("--providers.docker")
      .args(cmdOptionArgs());
  }

  private List<String> cmdOptionArgs() {
    return optionize( config.traefik().args() , "=");
  }

  private List<String> dockerOptionsArgs() {
    return Commands.optionize(config.traefik().options());
  }

  private String[] labelArgs() {
   return Commands.argumentize("--label", config.traefik().labels());
  }

  private String[] envArgs() {
    return config.traefik().envArgs();
  }

  private String[] publishArgs() {
    if (config.traefik().publish()) {
      return Commands.argumentize("--publish", List.of(
        config.traefik().port()
      ));
    }
    return new String[]{};
  }

  public Cmd start() {
    return Cmd.cmd("docker", "container", "start", "traefik");
  }

  public Cmd stop() {
    return Cmd.cmd("docker", "container", "stop", "traefik");
  }

  public Cmd startOrRun() {
    return any(
      start(),
      run()
    );
  }

  public Cmd info() {
    return Cmd.cmd("docker", "ps", "--filter", "name=^traefik$");
  }

  public Cmd logs(String since, String lines, String grep, String grepOptions) {
    return pipe(
      Cmd.cmd("docker", "logs", "traefik", since != null ? "--since " + since : null, lines != null ? "--tail " + lines : null, "--timestamps", "2>&1"),
      grep != null ? Cmd.cmd("grep", "\"" + grep + "\"" + (grepOptions != null ? " " + grepOptions : "")) : null
    );
  }

  public Cmd followLogs() {
    throw new UnsupportedOperationException();
  }

  public Cmd removeContainer() {
    return Cmd.cmd("docker", "container", "prune", "--force", "--filter", "label=org.opencontainers.image.title=Traefik");
  }

  public Cmd removeImage() {
    return Cmd.cmd("docker", "image", "prune", "--force", "--filter", "label=org.opencontainers.image.title=Traefik");
  }

}
