package dev.deploy4j.deploy.host.commands;

import dev.deploy4j.deploy.configuration.Configuration;
import dev.deploy4j.deploy.configuration.Env;
import dev.deploy4j.deploy.utils.file.File;
import dev.rebelcraft.cmd.Cmd;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static dev.rebelcraft.cmd.CmdUtils.argumentize;
import static dev.rebelcraft.cmd.CmdUtils.optionize;
import static dev.rebelcraft.cmd.Cmds.any;
import static dev.rebelcraft.cmd.Cmds.pipe;
import static dev.rebelcraft.cmd.pkgs.Docker.docker;

public class SpringBootAdminHostCommands extends BaseHostCommands {

  public SpringBootAdminHostCommands(Configuration config) {
    super(config);
  }

  public Cmd run() {
    return docker().run()
      .args("--name", "spring-boot-admin")
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
      .description("run spring-boot-admin");
  }

  public Cmd start() {
    return docker().container().args("start", "spring-boot-admin").description("start spring-boot-admin");
  }

  public Cmd stop() {
    return docker().container().args("stop", "spring-boot-admin").description("stop spring-boot-admin");
  }

  public Cmd startOrRun() {
    return any(
      start(),
      run()
    ).description("start or run");
  }

  public Cmd info() {
    return docker().ps().args("--filter", "name=^spring-boot-admin$").description("info");
  }

  public Cmd logs(String since, String lines, String grep, String grepOptions) {
    return pipe(
      docker().logs().args("spring-boot-admin", since != null ? "--since " + since : null, lines != null ? "--tail " + lines : null, "--timestamps", "2>&1"),
      grep != null ? Cmd.cmd("grep", "\"" + grep + "\"" + (grepOptions != null ? " " + grepOptions : "")) : null
    ).description("logs");
  }

  // TODO: implement followLogs
  public Cmd followLogs() {
    throw new UnsupportedOperationException();
  }

  public Cmd removeContainer() {
    return docker().container().args("prune", "--force", "--filter", "label=org.opencontainers.image.title=spring-boot-admin").description("remove spring-boot-admin");
  }

  public Cmd removeImage() {
    return docker().image().args("prune", "--force", "--filter", "label=org.opencontainers.image.title=spring-boot-admin").description("remove spring-boot-admin image");
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
    return File.join( config().envDirectory(), "spring-boot-admin" );
  }

  public String secretsIO() {
    return env().secretsIO();
  }

  public String secretsPath() {
    return File.join( config().envDirectory(), "spring-boot-admin", "spring-boot-admin.env" );
  }

  private List<String> dockerOptionsArgs() {
    return optionize(options());
  }

  private List<String> cmdOptionArgs() {
    return optionize(args(), "=");
  }

  // delegate

  public String port() {
    return config.springBootAdmin().port();
  }

  public boolean publish() {
    return config.springBootAdmin().publish();
  }

  public Map<String, String> labels() {
    return config.springBootAdmin().labels();
  }

  public Env env() {
    return config.springBootAdmin().env();
  }

  public String image() {
    return config.springBootAdmin().image();
  }

  public Map<String, String> options() {
    return config.springBootAdmin().options();
  }

  public Map<String, String> args() {
    return config.springBootAdmin().args();
  }

}
