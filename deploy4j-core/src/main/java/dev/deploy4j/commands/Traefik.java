package dev.deploy4j.commands;

import dev.deploy4j.Cmd;
import dev.deploy4j.configuration.Configuration;
import dev.deploy4j.configuration.Env;

import java.util.List;
import java.util.Map;

import static dev.deploy4j.Utils.argumentize;
import static dev.deploy4j.Utils.optionize;

public class Traefik extends Base {

  public Traefik(Configuration config) {
    super(config);
  }

  public Cmd run() {
    return Cmd.cmd("docker", "run")
      .args("--name", "traefik")
      .args("--detach")
      .args("--restart", "unless-stopped")
      .args(publishArgs())
      .args("--volume", "/var/run/docker.sock:/var/run/docker.sock")
      .args(envArgs())
      .args(config().loggingArgs())
      .args(labelArgs())
      .args(dockerOptionsArgs())
      .args(image())
      .args("--providers.docker")
      .args(cmdOptionArgs())
      .description("run traefik");
  }

  public Cmd start() {
    return Cmd.cmd("docker", "container", "start", "traefik").description("start traefik");
  }

  public Cmd stop() {
    return Cmd.cmd("docker", "container", "stop", "traefik").description("stop traefik");
  }

  public Cmd startOrRun() {
    return any(
      start(),
      run()
    ).description("start or run");
  }

  public Cmd info() {
    return Cmd.cmd("docker", "ps", "--filter", "name=^traefik$").description("info");
  }

  public Cmd logs(String since, String lines, String grep, String grepOptions) {
    return pipe(
      Cmd.cmd("docker", "logs", "traefik", since != null ? "--since " + since : null, lines != null ? "--tail " + lines : null, "--timestamps", "2>&1"),
      grep != null ? Cmd.cmd("grep", "\"" + grep + "\"" + (grepOptions != null ? " " + grepOptions : "")) : null
    ).description("logs");
  }

  // TODO: implement followLogs
  public Cmd followLogs() {
    throw new UnsupportedOperationException();
  }

  public Cmd removeContainer() {
    return Cmd.cmd("docker", "container", "prune", "--force", "--filter", "label=org.opencontainers.image.title=Traefik").description("remove traefik");
  }

  public Cmd removeImage() {
    return Cmd.cmd("docker", "image", "prune", "--force", "--filter", "label=org.opencontainers.image.title=Traefik").description("remove traefik image");
  }

  public Cmd makeEnvDirectory() {
    return makeDirectory(env().secretsDirectory());
  }

  public Cmd removeEnvFile() {
    return Cmd.cmd("rm", "-f", env().secretsFile()).description("remove traefik env file");
  }

  // private
  private String[] publishArgs() {
    if (publish()) {
      return argumentize("--publish",
        port()
      );
    }
    return new String[]{};
  }

  private String[] labelArgs() {
    return argumentize("--label", labels());
  }

  private List<String> envArgs() {
    return env().args();
  }

  private List<String> dockerOptionsArgs() {
    return optionize(options());
  }

  private List<String> cmdOptionArgs() {
    return optionize(args(), "=");
  }

  // delegate

  public String port() {
    return config.traefik().port();
  }

  public boolean publish() {
    return config.traefik().publish();
  }

  public Map<String, String> labels() {
    return config.traefik().labels();
  }

  public Env env() {
    return config.traefik().env();
  }

  public String image() {
    return config.traefik().image();
  }

  public Map<String, String> options() {
    return config.traefik().options();
  }

  public Map<String, String> args() {
    return config.traefik().args();
  }

}
