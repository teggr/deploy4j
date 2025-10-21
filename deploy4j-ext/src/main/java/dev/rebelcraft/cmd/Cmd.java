package dev.rebelcraft.cmd;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Cmd {

  public static Cmd cmd(String... base) {
    return new Cmd(base);
  }

  public static Cmd cmd(List<String> base) {
    return new Cmd(base);
  }

  private final List<String> cmd;
  private String description = "";

  private Cmd(String... base) {
    this.cmd = new ArrayList<>();
    Collections.addAll(this.cmd, base);
  }

  private Cmd(List<String> base) {
    this.cmd = new ArrayList<>();
    this.cmd.addAll(base);
  }

  public Cmd description(String description) {
    this.description = description;
    return this;
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

  // Build final immutable list
  public List<String> build() {
    return cmd.stream().filter(Objects::nonNull).toList();
  }

  public String description() {
    return description;
  }

}
