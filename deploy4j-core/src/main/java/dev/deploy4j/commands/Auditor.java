package dev.deploy4j.commands;

import dev.deploy4j.Cmd;
import dev.deploy4j.configuration.Configuration;

import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static dev.deploy4j.Commands.append;

public class Auditor {

  private final Configuration config;

  public Auditor(Configuration config) {
    this.config = config;
  }


  public Cmd reveal() {
    return Cmd.cmd("tail", "-n", "50", auditLogFile());
  }

  private String auditLogFile() {
    String file = Stream.of(
      config.service(),
      config.destination(),
      "audit.log"
    ).filter(Objects::nonNull)
      .collect(Collectors.joining("-"));
    return config.runDirectory() + "/" + file;
  }

  public Cmd record(String line) {
    return append(
      Cmd.cmd("echo", line), // TODO: audit tags + details
      Cmd.cmd( auditLogFile() )
    );
  }
}
