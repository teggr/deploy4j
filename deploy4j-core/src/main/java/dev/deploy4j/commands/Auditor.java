package dev.deploy4j.commands;

import dev.deploy4j.Cmd;
import dev.deploy4j.Tags;
import dev.deploy4j.configuration.Configuration;
import dev.deploy4j.file.Deploy4jFile;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static dev.deploy4j.Commands.append;

public class Auditor extends Base{

  private final Map<String, String> details;

  public Auditor(Configuration config, Map<String, String> details) {
    super(config);
    this.details = details;
  }

  public Cmd record(String line, Map<String, String> details) {
    return append(
      Cmd.cmd("echo")
        .args(auditTags(details).except("version", "service_version", "service").toTagString())
        .args(line),
      Cmd.cmd( auditLogFile() )
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
    return Deploy4jFile.join( config.runDirectory() , file );
  }

  private Tags auditTags(Map<String, String> details) {
    return tags(List.of(details(), details));
  }

  // attributes

  public Map<String, String> details() {
    return details;
  }



}
