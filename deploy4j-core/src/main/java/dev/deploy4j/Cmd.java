package dev.deploy4j;

import java.util.*;

public class Cmd {

  public static Cmd cmd(String... base) {
    return new Cmd(base);
  }

  private final List<String> cmd;

  private Cmd(String... base) {
    this.cmd = new ArrayList<>();
    Collections.addAll(this.cmd, base);
  }

  // Add one or more args
  public Cmd args(String... args) {
    if (args != null && args.length > 0) {
      Collections.addAll(cmd, args);
    }
    return this;
  }

  // Add list of args
  public Cmd args(List<String> args) {
    if (args != null && !args.isEmpty()) {
      cmd.addAll(args);
    }
    return this;
  }

  // Add map of key/value pairs as repeated argument
  // Example: mapArgs("-e", {"USER"="root"}) -> ["-e", "USER=root"]
  public Cmd mapArgs(String option, Map<String, String> map) {
    if (map != null) {
      map.forEach((k, v) -> {
        cmd.add(option);
        cmd.add(k + "=" + v);
      });
    }
    return this;
  }

  // Build final immutable list
  public List<String> build() {
    return Collections.unmodifiableList(cmd);
  }

}
