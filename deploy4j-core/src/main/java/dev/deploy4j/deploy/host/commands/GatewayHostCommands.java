package dev.deploy4j.deploy.host.commands;

import dev.deploy4j.deploy.configuration.Configuration;
import dev.deploy4j.deploy.configuration.Env;
import dev.deploy4j.deploy.utils.file.File;
import dev.rebelcraft.cmd.Cmd;
import dev.rebelcraft.cmd.Cmds;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static dev.rebelcraft.cmd.CmdUtils.argumentize;
import static dev.rebelcraft.cmd.CmdUtils.optionize;
import static dev.rebelcraft.cmd.CmdUtils.optionizeFlexible;
import static dev.rebelcraft.cmd.Cmds.*;
import static dev.rebelcraft.cmd.Cmds.any;
import static dev.rebelcraft.cmd.pkgs.Docker.docker;

public class GatewayHostCommands extends BaseHostCommands {

  public GatewayHostCommands(Configuration config) {
    super(config);
  }

  public Cmd run() {
    return docker().run()
      .args("--name", "gateway")
      .args("--network", "deploy4j")
      .args("--detach")
      .args("--restart", "unless-stopped")
      .args(publishArgs())
      .args(envArgs())
      .args(config().loggingArgs())
      .args(labelArgs())
      .args(dockerOptionsArgs())
      .args(image())
      .args(cmdOptionArgs())
      .description("run gateway");
  }

  public Cmd start() {
    return docker().container().args("start", "gateway").description("start gateway");
  }

  public Cmd stop() {
    return docker().container().args("stop", "gateway").description("stop gateway");
  }

  public Cmd startOrRun() {
    return any(
      start(),
      run()
    ).description("start or run");
  }

  public Cmd info() {
    return docker().ps().args("--filter", "name=^gateway$").description("info");
  }

  public Cmd logs(String since, String lines, String grep, String grepOptions) {
    return pipe(
      docker().logs().args("gateway", since != null ? "--since " + since : null, lines != null ? "--tail " + lines : null, "--timestamps", "2>&1"),
      grep != null ? Cmd.cmd("grep", "\"" + grep + "\"" + (grepOptions != null ? " " + grepOptions : "")) : null
    ).description("logs");
  }

  // TODO: implement followLogs
  public Cmd followLogs() {
    throw new UnsupportedOperationException();
  }

  public Cmd removeContainer() {
    return docker().container().args("prune", "--force", "--filter", "label=org.opencontainers.image.title=Gateway").description("remove gateway");
  }

  public Cmd removeImage() {
    return docker().image().args("prune", "--force", "--filter", "label=org.opencontainers.image.title=Gateway").description("remove gateway image");
  }

  public Cmd ensureEnvDirectory() {
    return makeDirectory(envDirectory());
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

  public List<String> envArgs() {
    List<String> list = new ArrayList<>();
    list.addAll(env().clearArgs());
    list.addAll(Arrays.asList(argumentize("--env-file", secretsPath())));
    return list;
  }

  public String envDirectory() {
    return File.join( config().envDirectory(), "gateway" );
  }

  public String secretsIO() {
    return env().secretsIO();
  }

  public String secretsPath() {
    return File.join( config().envDirectory(), "gateway", "gateway.env" );
  }

  private List<String> dockerOptionsArgs() {
    return optionizeFlexible(options());
  }

  private List<String> cmdOptionArgs() {
    return optionize(args(), "=");
  }

  // delegate

  public String port() {
    return config.gateway().port();
  }

  public boolean publish() {
    return config.gateway().publish();
  }

  public Map<String, String> labels() {
    return config.gateway().labels();
  }

  public Env env() {
    return config.gateway().env();
  }

  public String image() {
    return config.gateway().image();
  }

  public Map<String, Object> options() {
    return config.gateway().options();
  }

  public Map<String, String> args() {
    return config.gateway().args();
  }

}
