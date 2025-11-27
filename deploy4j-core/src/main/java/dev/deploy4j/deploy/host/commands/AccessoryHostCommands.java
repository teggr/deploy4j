package dev.deploy4j.deploy.host.commands;

import dev.deploy4j.deploy.configuration.Accessory;
import dev.deploy4j.deploy.configuration.Configuration;
import dev.rebelcraft.cmd.Cmd;
import dev.rebelcraft.cmd.pkgs.Docker;
import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.util.List;
import java.util.Map;

import static dev.rebelcraft.cmd.Cmds.pipe;
import static dev.rebelcraft.cmd.pkgs.Docker.docker;
import static dev.rebelcraft.cmd.pkgs.Grep.grep;

public class AccessoryHostCommands extends BaseHostCommands {

  private final Accessory accessoryConfig;

  public AccessoryHostCommands(Configuration config, String name) {
    super(config);
    this.accessoryConfig = config.accessory(name);
  }

  public Cmd run(String host) {
    return docker().run()
      .args("--name", serviceName())
      .args("--detach")
      .args("--restart", "unless-stopped")
      .args(config().loggingArgs())
      .args(publishArgs())
      .args(StringUtils.isNotBlank(host) ? new String[]{ "--env", "DEPLOY4J_HOST=\"" + host +"\"" } : null)
      .args(envArgs())
      .args(volumeArgs())
      .args(labelArgs())
      .args(optionArgs())
      .args(image())
      .args(cmd())
      .description("Run accessory");
  }

  public Cmd start() {
    return docker().container().args("start", serviceName());
  }

  public Cmd stop() {
    return docker().container().args("stop", serviceName());
  }

  public Cmd info(boolean all, boolean quiet) {
    Docker ps = docker().ps();
    if(all) {
      ps = ps.args("-a");
    }
    if(quiet) {
      ps = ps.args("-q");
    }
    return ps
      .args(serviceFilter());
  }

  public Cmd logs( boolean timestamps, String since, String lines, String grep, String grepOptions) {
    return pipe(
      docker().logs()
        .args(serviceName())
        .args(since != null ? List.of("--since", since) : List.of())
        .args(lines != null ? List.of("--tail", lines) : List.of())
        .args(timestamps ? "--timestamps" : null)
        .args("2>&1"),
      grep != null ? grep().search(grep)
        .args(grepOptions) : null
    );
  }

  // TODO: follow logs

  public Cmd executeInExistingContainer(String command) {
    return docker().exec()
      // TODO: interactive mode
      .args(serviceName())
      .args(command);
  }

  // TODO: excecute in new container
  // TODO: excecute in existing container over ssh
  // TODO: execute in new container over ssh
  // TODO: run over ssh

  public void ensureLocalFilePresent(String localFile) {
    if (!new File(localFile).exists()) {
      throw new RuntimeException("Missing file: " + localFile);
    }
  }

  public Cmd pullImage() {
    return docker().image().args("pull", image());
  }

  public Cmd removeServiceDirectory() {
    return Cmd.cmd("rm", "-rf", serviceName());
  }

  public Cmd removeContainer() {
    return docker().container().args("prune", "--force")
      .args(serviceFilter());
  }

  public Cmd removeImage() {
    return docker().image().args("rm", "--force", image());
  }

  public Cmd ensureEnvDirectory() {
    return makeDirectory(accessoryConfig().envDirectory());
  }

  // private

  private String[] serviceFilter() {
    return new String[]{
      "--filter",
      "label=service=" + serviceName()
    };
  }

  // delegates

  public String serviceName() {
    return accessoryConfig().serviceName();
  }

  public String image() {
    return accessoryConfig().image();
  }

  public List<String> hosts() {
    return accessoryConfig().hosts();
  }

  public String port() {
    return accessoryConfig().port();
  }

  public Map<String, String> files() {
    return accessoryConfig().files();
  }

  public Map<String, String> directories() {
    return accessoryConfig().directories();
  }

  public String cmd() {
    return accessoryConfig().cmd();
  }

  public String[] publishArgs() {
    return accessoryConfig().publishArgs();
  }

  public List<String> envArgs() {
    return accessoryConfig().envArgs();
  }

  public String[] volumeArgs() {
    return accessoryConfig().volumeArgs();
  }

  public String[] labelArgs() {
    return accessoryConfig().labelArgs();
  }

  public List<String> optionArgs() {
    return accessoryConfig().optionArgs();
  }

  public String secretsPath() {
    return accessoryConfig().secretsPath();
  }

  public String secretsIO() {
    return accessoryConfig().secretsIO();
  }

  // attributes

  public Accessory accessoryConfig() {
    return accessoryConfig;
  }

}
