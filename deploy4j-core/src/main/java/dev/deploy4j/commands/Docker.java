package dev.deploy4j.commands;

import dev.deploy4j.Cmd;
import dev.deploy4j.configuration.Configuration;

public class Docker extends Base {

  public Docker(Configuration config) {
    super(config);
  }

  // Install Docker using the https://github.com/docker/docker-install convenience script.
  public Cmd install() {
    return pipe(getDocker(), Cmd.cmd("sh")).description("install");
  }

  // Checks the Docker client version. Fails if Docker is not installed.
  public Cmd installed() {
    return Cmd.cmd("docker", "-v").description("installed");
  }

  // Checks the Docker server version. Fails if Docker is not running.
  public Cmd running() {
    return Cmd.cmd("docker", "version").description("running");
  }

  // Do we have superuser access to install Docker and start system services?
  public Cmd superUser() {
    return Cmd.cmd("[ \"${EUID:-$(id -u)}\" -eq 0 ] || command -v sudo >/dev/null || command -v su >/dev/null").description("superuser");
  }

  private Cmd getDocker() {
    return shell(
      any(
        Cmd.cmd("curl", "-fsSL", "https://get.docker.com"),
        Cmd.cmd("wget", "-O -", "https://get.docker.com"),
        Cmd.cmd("echo", "\"exit 1\"")
      )
    ).description("get docker");
  }

}
