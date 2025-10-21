package dev.deploy4j.commands;

import dev.deploy4j.configuration.Configuration;
import dev.rebelcraft.cmd.Cmd;

import static dev.rebelcraft.cmd.pkgs.Curl.curl;
import static dev.rebelcraft.cmd.pkgs.Docker.docker;
import static dev.rebelcraft.cmd.pkgs.Echo.echo;
import static dev.rebelcraft.cmd.pkgs.Wget.wget;

public class DockerCommands extends BaseCommands {

  public DockerCommands(Configuration config) {
    super(config);
  }

  // Install Docker using the https://github.com/docker/docker-install convenience script.
  public Cmd install() {
    return pipe(getDocker(), Cmd.cmd("sh")).description("install");
  }

  // Checks the Docker client version. Fails if Docker is not installed.
  public Cmd installed() {
    return docker().version().description("installed");
  }

  // Checks the Docker server version. Fails if Docker is not running.
  public Cmd running() {
    return docker().version().description("running");
  }

  // Do we have superuser access to install Docker and start system services?
  public Cmd superUser() {
    return Cmd.cmd("[ \"${EUID:-$(id -u)}\" -eq 0 ] || command -v sudo >/dev/null || command -v su >/dev/null").description("superuser");
  }

  private Cmd getDocker() {
    return shell(
      any(
        curl().options("-fsSL").url("https://get.docker.com"),
        wget().options("-O -").url("https://get.docker.com"),
        echo().message("exit 1")
      )
    ).description("get docker");
  }

}
