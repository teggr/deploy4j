package dev.deploy4j.deploy.host.commands;

import dev.deploy4j.deploy.Tags;
import dev.deploy4j.deploy.configuration.Configuration;
import dev.deploy4j.deploy.utils.file.File;
import dev.rebelcraft.cmd.Cmd;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static dev.rebelcraft.cmd.Cmds.append;

public class AuditorHostCommands extends BaseHostCommands {

  private final Map<String, String> details;

  public AuditorHostCommands(Configuration config, Map<String, String> details) {
    super(config);
    this.details = details;
  }

  public Cmd record(String line) {
    return record(line, null);
  }

  public Cmd record(String line, Map<String, String> details) {
    return append(
      Cmd.cmd("echo")
        .args(auditTags(details).except("version", "service_version", "service").toTagString())
        .args(line),
      Cmd.cmd(auditLogFile())
    );
  }

  public Cmd reveal() {
    return Cmd.cmd("tail", "-n", "50", auditLogFile());
  }

  // private

  private String auditLogFile() {
    String file = Stream.of(
        config.service(),
        config.destination(),
        "audit.log"
      ).filter(Objects::nonNull)
      .collect(Collectors.joining("-"));
    return File.join(config.runDirectory(), file);
  }

  private Tags auditTags(Map<String, String> details) {
    Map<String, String> merged = new HashMap<>();
    if (details() != null) merged.putAll(details());
    if (details != null) merged.putAll(details);
    return tags(merged);
  }

  // attributes

  public Map<String, String> details() {
    return details;
  }


}
