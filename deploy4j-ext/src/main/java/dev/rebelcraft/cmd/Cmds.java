package dev.rebelcraft.cmd;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.apache.commons.io.FilenameUtils.getFullPathNoEndSeparator;

public class Cmds {

  public static Cmd makeDirectoryFor(String remoteFile) {
    return makeDirectory(getFullPathNoEndSeparator(remoteFile));
  }

  public static Cmd makeDirectory(String path) {
    return Cmd.cmd("mkdir", "-p", path);
  }

  public static Cmd removeDirectory(String path) {
    return Cmd.cmd("rm", "-r", path);
  }

  public static Cmd combine(String by, Cmd... commands) {

    List<String> list = Stream.of(commands)
      .filter(Objects::nonNull)
      .map(cmd -> cmd.args(by))
      .flatMap(cmd -> cmd.build().stream())
      .toList();

    if (!list.isEmpty()) {
      list = new ArrayList<>(list);
      list.removeLast();
    }

    return Cmd.cmd(list);

  }

  public static Cmd combine(Cmd... commands) {
    return combine("&&", commands);
  }

  public static Cmd chain(Cmd... commands) {
    return combine(";", commands);
  }

  public static Cmd pipe(Cmd... commands) {
    return combine("|", commands);
  }

  public static Cmd append(Cmd... commands) {
    return combine(">>", commands);
  }

  public static Cmd write(Cmd... commands) {
    return combine(">", commands);
  }

  public static Cmd any(Cmd... commands) {
    return combine("||", commands);
  }

  public static Cmd xargs(Cmd cmd) {
    return Cmd.cmd("xargs")
      .args(cmd.build());
  }

  public static Cmd shell(Cmd command) {
    return Cmd.cmd(
      "sh", "-c",
      "'" + escapeCommand(command) + "'"
    );
  }

  private static String escapeCommand(Cmd command) {
    return commandString(command)
      .replace("'", "'\\''");
  }

  private static String commandString(Cmd command) {
    return Stream.of(command)
      .flatMap(Cmd -> command.build().stream())
      .filter(Objects::nonNull)
      .collect(Collectors.joining(" "));
  }

}
