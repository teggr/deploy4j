package dev.deploy4j.commands;

import dev.deploy4j.Cmd;
import dev.deploy4j.configuration.Configuration;

import static dev.deploy4j.Commands.*;

public class Docker {

  private final Configuration config;

  public Docker(Configuration config) {
    this.config = config;
  }

  // Install Docker using the https://github.com/docker/docker-install convenience script.
  public Cmd install() {
    return pipe(getDocker(), Cmd.cmd("sh"));
  }

  // Checks the Docker client version. Fails if Docker is not installed.
  public Cmd installed() {
    return Cmd.cmd("docker", "-v");
  }

  // Checks the Docker server version. Fails if Docker is not running.
  public Cmd running() {
    return Cmd.cmd("docker", "version");
  }

  // Do we have superuser access to install Docker and start system services?
  public Cmd superUser() {
    return Cmd.cmd("[ \"${EUID:-$(id -u)}\" -eq 0 ] || command -v sudo >/dev/null || command -v su >/dev/null");
  }

  private Cmd getDocker() {
    return shell(
      any(
        Cmd.cmd("curl", "-fsSL", "https://get.docker.com"),
        Cmd.cmd("wget", "-O -", "https://get.docker.com"),
        Cmd.cmd("echo", "\"exit 1\"")
      )
    );
  }

}
