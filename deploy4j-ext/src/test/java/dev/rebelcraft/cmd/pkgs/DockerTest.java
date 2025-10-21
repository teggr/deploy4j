package dev.rebelcraft.cmd.pkgs;

import dev.rebelcraft.cmd.Cmd;
import org.junit.jupiter.api.Test;

import java.util.List;

import static dev.rebelcraft.cmd.pkgs.Docker.docker;
import static org.assertj.core.api.Assertions.assertThat;

class DockerTest {

  @Test
  void shouldCreateDockerBaseCommand() {

    Cmd cmd = docker().v();

    List<String> build = cmd.build();

    assertThat(build)
      .containsExactly("docker", "-v");

  }

  @Test
  void shouldCreateSubCommand() {

    Cmd cmd = docker().ps().args("--all").args("--filter", "status=running");

    List<String> build = cmd.build();

    assertThat(build)
      .containsExactly("docker", "ps", "--all", "--filter", "status=running");

  }

  @Test
  void shouldCreateSubCommandWithDockerOptions() {

    Cmd cmd = docker().config("/root/.docker")
      .ps()
      .args("--all").args("--filter", "status=running");

    List<String> build = cmd.build();

    assertThat(build)
      .containsExactly("docker", "--config", "/root/.docker", "ps", "--all", "--filter", "status=running");

  }

}