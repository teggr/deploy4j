package dev.deploy4j.commands;

import dev.deploy4j.Cmd;

import static dev.deploy4j.Commands.*;

public class DockerCommands {

  // Install Docker using the https://github.com/docker/docker-install convenience script.
  public static Cmd install() {
    return pipe(getDocker(), Cmd.cmd("sh"));
  }

  // Checks the Docker client version. Fails if Docker is not installed.
  public  static Cmd installed() {
    return Cmd.cmd("docker", "-v");
  }

  // Checks the Docker server version. Fails if Docker is not running.
  public  static Cmd running() {
    return Cmd.cmd("docker", "version");
  }

  // Do we have superuser access to install Docker and start system services?
  public  static Cmd isSuperUser() {
    return Cmd.cmd("[ \"${EUID:-$(id -u)}\" -eq 0 ] || command -v sudo >/dev/null || command -v su >/dev/null");
  }

  private  static Cmd getDocker() {
    return shell(
      any(
        Cmd.cmd("curl", "-fsSL", "https://get.docker.com"),
        Cmd.cmd("wget", "-O -", "https://get.docker.com"),
        Cmd.cmd("echo", "\"exit 1\"")
      )
    );
  }

}
