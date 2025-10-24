package dev.deploy4j.cli;

import picocli.CommandLine;

@CommandLine.Command(
  name = "env",
  description = "Manage environment files"
)
public class EnvCliCommand {

  @CommandLine.Mixin
  private HelpOptions helpOptions = new HelpOptions();

}
