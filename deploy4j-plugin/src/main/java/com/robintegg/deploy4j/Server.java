package com.robintegg.deploy4j;

import com.robintegg.deploy4j.cli.*;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class Server {

  private final CLI cli;

  public Server(ServerConfiguration serverConfiguration, CLI cli) {
    this.cli = cli;
  }

  public void installDocker() throws Exception {

    log.info("checking docker installed");
    int dockerInstalled = cli.executeCommandForStatus(Docker.version());

    if (dockerInstalled != 0) {

      log.info("docker not installed");

      CliCommand dockerInstall = Std.pipe(
          CUrl.http().args("fsSL").url("https://get.docker.com").toCli(),
          Sh.execute()
      );

      List<CliCommand> commands = List.of(dockerInstall, Docker.version());
      for (CliCommand cmd : commands) {
        cli.executeCommand(cmd);
      }

    } else {

      log.info("docker installed");

    }

  }

  @SneakyThrows
  public void pushApplication(ApplicationFiles applicationFiles) {

    log.info("pushing application files {}", applicationFiles);

    // jar file
    String from = applicationFiles.getTargetDirectory().toAbsolutePath().toString();
    String to = "/root/" + flip(applicationFiles.getTargetDirectory().toString());
    String fileName = applicationFiles.getJarFile().getFileName().toString();

    cli.executeCommand(ensureDirectoryExists(to));
    cli.copyFile(from, to, fileName);

    // docker file
    from = applicationFiles.getWorkingDirectory().toAbsolutePath().toString();
    to = "/root/" + flip(applicationFiles.getWorkingDirectory().toString());
    fileName = applicationFiles.getDockerFile().getFileName().toString();

    cli.executeCommand(ensureDirectoryExists(to));
    cli.copyFile(from, to, fileName);

    cli.executeCommand(
        Docker.build()
            .forceRm()
            .quiet()
            .tag(applicationFiles.getApplicationName())
            .file(to + "/" + fileName)
            .context(to)
    );

  }

  private CliCommand ensureDirectoryExists(String to) throws Exception {
    return Std.mkdir()
        .withParentDirectories()
        .directory(to);
  }

  private String flip(String string) {
    return string.replaceAll("\\\\", "/");
  }

  public void runTraefik() {

    cli.executeCommand(
        Docker.run()
            .name("traefik")
            .detach()
            .restartUnlessStopped()
            .publish(80)
            .publish(8080)
            .volume("/var/run/docker.sock:/var/run/docker.sock")
            .env("log.level", "DEBUG")
            .image("traefik:v2.9")
            .arg("--providers.docker=true")
            .arg("--api.insecure=true")
    );

  }

  public void bootApplication() {

    cli.executeCommand(startOrRun());

  }

  private CliCommand startOrRun() {
    return Std.or(
        Docker.start().container("container_name"),
        Docker.run()
            .detach()
            .restartUnlessStopped()
            .name("container_name")
    );
  }

}
