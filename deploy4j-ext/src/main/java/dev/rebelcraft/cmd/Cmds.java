package dev.rebelcraft.cmd;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Cmds {

  public static Cmd makeDirectoryFor(String remoteFile) {
    return makeDirectory(new File(remoteFile).getParent());
  }

  public static  Cmd makeDirectory(String path) {
    return Cmd.cmd("mkdir", "-p", path);
  }

  public static  Cmd removeDirectory(String path) {
    return Cmd.cmd("rm", "-r", path);
  }

  public static  Cmd combine(Cmd[] commands, String by) {

    List<String> list = Stream.of(commands)
      .filter(Objects::nonNull)
      .map(cmd -> cmd.args( by ) )
      .flatMap(cmd -> cmd.build().stream() )
      .toList();

    if(!list.isEmpty()) {
      list = new ArrayList<>(list);
      list.removeLast();
    }

    return Cmd.cmd( list.toArray( new String[0] ) );

  }

  public static  Cmd combine(Cmd... commands) {
    return combine(commands, "&&");
  }

  public static  Cmd chain(Cmd... commands) {
    return combine(commands, ";");
  }

  public static  Cmd pipe(Cmd... commands) {
    return combine(commands, "|");
  }

  public static  Cmd append(Cmd... commands) {
    return combine(commands, ">>");
  }

  public static  Cmd write(Cmd... commands) {
    return combine(commands, ">");
  }

  public static  Cmd any(Cmd... commands) {
    return combine(commands, "||");
  }

  public static  Cmd xargs(Cmd cmd) {
    return Cmd.cmd("xargs")
      .args(cmd.build());
  }

  public static  Cmd shell(Cmd command) {
    return Cmd.cmd(
      "sh", "-c",
      "'" + Stream.of(command).flatMap( Cmd -> command.build().stream() ).collect(Collectors.joining(" ")).replace("'", "'\\''") + "'"
    );
  }

  public static Cmd substitute(Cmd... commands) {
    return Cmd.cmd( "\\$\\(" + Stream.of( commands ).map( cmd ->cmd.build().get(0) ).collect(Collectors.joining(" ")) + "\\)" );
  }

}
