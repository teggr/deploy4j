package dev.deploy4j.commands;

import dev.rebelcraft.cmd.Cmd;
import dev.deploy4j.configuration.Configuration;

import java.io.File;
import java.util.List;
import java.util.Map;

public class Accessory extends Base {

  private final dev.deploy4j.configuration.Accessory accessoryConfig;

  public Accessory(Configuration config, String name) {
    super(config);
    this.accessoryConfig = config.accessory(name);
  }

  public Cmd run() {
    return Cmd.cmd("docker", "run")
      .args("--name", serviceName())
      .args("--detach")
      .args("--restart", "unless-stopped")
      .args(config().loggingArgs())
      .args(publishArgs())
      .args(envArgs())
      .args(volumeArgs())
      .args(labelArgs())
      .args(optionArgs())
      .args(image())
      .args(cmd())
      .description("Run accessory");
  }

  public Cmd start() {
    return Cmd.cmd("docker", "container", "start", serviceName() );
  }

  public Cmd stop() {
    return Cmd.cmd( "docker", "container", "stop", serviceName() );
  }

  public Cmd info() {
    return Cmd.cmd("docker", "ps")
      .args(serviceFilter());
  }

  public Cmd logs(String since, String lines, String grep, String grepOptions) {
    return pipe(
      Cmd.cmd("docker", "logs")
        .args(serviceName())
        .args(since != null ? List.of("--since", since) : List.of())
        .args(lines != null ? List.of("--tail", lines) : List.of())
        .args("--timestamps")
        .args("2>&1"),
      grep != null ? Cmd.cmd("grep", "'" + grep + "'")
        .args( grepOptions ) : null
    );
  }

  // TODO: follow logs

  public Cmd executeInExistingContainer(String command) {
    return Cmd.cmd( "docker", "exec" )
      // TODO: interactive mode
      .args( serviceName() )
      .args( command );
  }

  // TODO: excecute in new container
  // TODO: excecute in existing container over ssh
  // TODO: execute in new container over ssh
  // TODO: run over ssh

  public void ensureLocalFilePresent(String localFile) {
    if( !new File(localFile).exists() ) {
      throw new RuntimeException("Missing file: " + localFile);
    }
  }

  public Cmd removeServiceDirectory() {
    return Cmd.cmd("rm", "-rf", serviceName() );
  }

  public Cmd removeContainer() {
    return Cmd.cmd( "docker", "container", "prune", "--force")
      .args( serviceFilter() );
  }

  public Cmd removeImage() {
    return Cmd.cmd( "docker", "image", "rm", "--force", image() );
  }

  public Cmd makeEnvDirectory() {
    return makeDirectory( accessoryConfig().env().secretsDirectory() );
  }

  public Cmd removeEnvFile() {
    return Cmd.cmd( "rm", "-f", accessoryConfig().env().secretsDirectory() );
  }

  // private

  private String[] serviceFilter() {
    return new String[]{
      "--filter",
      "label=service=" +  serviceName()
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

  // attributes

  public dev.deploy4j.configuration.Accessory accessoryConfig() {
    return accessoryConfig;
  }

}
