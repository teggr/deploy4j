package dev.rebelcraft.cmd;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CmdsTest {

  @Test
  void shouldMakeDirectoryForRemoteFileWithFileExtension() {

    Cmd cmd = Cmds.makeDirectoryFor("/path/to/remote/file.txt");

    List<String> build = cmd.build();

    assertThat(build)
      .containsExactly("mkdir", "-p", "/path/to/remote");

  }

  @Test
  void shouldMakeDirectoryForRemoteFileNoExtension() {

    Cmd cmd = Cmds.makeDirectoryFor("/path/to/remote/file");

    List<String> build = cmd.build();

    assertThat(build)
      .containsExactly("mkdir", "-p", "/path/to/remote");

  }

  @Test
  void shouldMakeDirectory() {

    Cmd cmd = Cmds.makeDirectory("/path/to/remote");

    List<String> build = cmd.build();

    assertThat(build)
      .containsExactly("mkdir", "-p", "/path/to/remote");

  }

  @Test
  void shouldRemoveDirectory() {

    Cmd cmd = Cmds.removeDirectory("/path/to/remote");

    List<String> build = cmd.build();

    assertThat(build)
      .containsExactly("rm", "-r", "/path/to/remote");

  }

  @Test
  void shouldCombineMultipleCommandsByNamedSeparator() {

    Cmd cmd = Cmds.combine(
      "&&",
      Cmds.makeDirectory("/path/to/remote"),
      Cmds.removeDirectory("/path/to/remote")
    );

    List<String> build = cmd.build();

    assertThat(build)
      .containsExactly("mkdir", "-p", "/path/to/remote", "&&", "rm", "-r", "/path/to/remote");

  }

  @Test
  void shouldCombineMultipleCommandsWithDefaultSeparator() {

    Cmd cmd = Cmds.combine(
      Cmds.makeDirectory("/path/to/remote"),
      Cmds.removeDirectory("/path/to/remote")
    );

    List<String> build = cmd.build();

    assertThat(build)
      .containsExactly("mkdir", "-p", "/path/to/remote", "&&", "rm", "-r", "/path/to/remote");

  }

  @Test
  void shouldChainMultipleCommands() {

    Cmd cmd = Cmds.chain(
      Cmds.makeDirectory("/path/to/remote"),
      Cmds.removeDirectory("/path/to/remote")
    );

    List<String> build = cmd.build();

    assertThat(build)
      .containsExactly("mkdir", "-p", "/path/to/remote", ";", "rm", "-r", "/path/to/remote");

  }

  @Test
  void shouldPipeMultipleCommands() {

    Cmd cmd = Cmds.pipe(
      Cmds.makeDirectory("/path/to/remote"),
      Cmds.removeDirectory("/path/to/remote")
    );

    List<String> build = cmd.build();

    assertThat(build)
      .containsExactly("mkdir", "-p", "/path/to/remote", "|", "rm", "-r", "/path/to/remote");

  }

  @Test
  void shouldAppendMultipleCommands() {

    Cmd cmd = Cmds.append(
      Cmds.makeDirectory("/path/to/remote"),
      Cmds.removeDirectory("/path/to/remote")
    );

    List<String> build = cmd.build();

    assertThat(build)
      .containsExactly("mkdir", "-p", "/path/to/remote", ">>", "rm", "-r", "/path/to/remote");

  }

  @Test
  void shouldWriteMultipleCommands() {

    Cmd cmd = Cmds.write(
      Cmds.makeDirectory("/path/to/remote"),
      Cmds.removeDirectory("/path/to/remote")
    );

    List<String> build = cmd.build();

    assertThat(build)
      .containsExactly("mkdir", "-p", "/path/to/remote", ">", "rm", "-r", "/path/to/remote");

  }

  @Test
  void shouldAnyMultipleCommands() {

    Cmd cmd = Cmds.any(
      Cmds.makeDirectory("/path/to/remote"),
      Cmds.removeDirectory("/path/to/remote")
    );

    List<String> build = cmd.build();

    assertThat(build)
      .containsExactly("mkdir", "-p", "/path/to/remote", "||", "rm", "-r", "/path/to/remote");

  }

  @Test
  void shouldWrapCommandWithXarg() {

    Cmd cmd = Cmds.xargs(
      Cmds.makeDirectory("/path/to/remote")
    );

    List<String> build = cmd.build();

    assertThat(build)
      .containsExactly("xargs", "mkdir", "-p", "/path/to/remote" );

  }

  @Test
  void shouldEscapeCommandForShell() {

    Cmd cmd = Cmds.shell(
      Cmds.makeDirectory("'/path/to/remote'")
    );

    List<String> build = cmd.build();

    assertThat(build)
      .containsExactly("sh", "-c", "'mkdir -p '\\''/path/to/remote'\\'''" );

  }

}