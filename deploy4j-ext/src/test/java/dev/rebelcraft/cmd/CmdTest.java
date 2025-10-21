package dev.rebelcraft.cmd;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CmdTest {

  @Test
  void shouldOutputListOfArgs() {

    Cmd cmd = Cmd.cmd("docker", "ps", "--all");

    List<String> build = cmd.build();

    assertThat(build)
      .containsExactly("docker", "ps", "--all");

  }

  @Test
  void shouldOutputListOfArgsFromList() {

    Cmd cmd = Cmd.cmd(List.of("docker", "ps", "--all"));

    List<String> build = cmd.build();

    assertThat(build)
      .containsExactly("docker", "ps", "--all");

  }

  @Test
  void shouldAppendArgsToOutputList() {

    Cmd cmd = Cmd.cmd("docker", "ps", "--all")
      .args("--last", "5")
      .args(List.of("--filter", "status=running"));

    List<String> build = cmd.build();

    assertThat(build)
      .containsExactly("docker", "ps", "--all", "--last", "5", "--filter", "status=running");

  }

  @Test
  void shouldIgnoreNullArgsToOutputList() {

    List<String> listWithNull = new ArrayList<>();
    listWithNull.add("--filter");
    listWithNull.add(null);
    listWithNull.add("status=running");

    Cmd cmd = Cmd.cmd("docker", "ps", null, "--all")
      .args("--last", null, "5")
      .args(listWithNull);

    List<String> build = cmd.build();

    assertThat(build)
      .containsExactly("docker", "ps", "--all", "--last", "5", "--filter", "status=running");

  }

  @Test
  void shouldSupportADescription() {

    Cmd cmd = Cmd.cmd("docker", "ps", "--all")
      .description("list all docker containers");

    List<String> build = cmd.build();

    assertThat(build)
      .containsExactly("docker", "ps", "--all");
    assertThat(cmd.description()).isEqualTo("list all docker containers");

  }


}