package dev.deploy4j.cli;

import picocli.CommandLine;

class HelpOptions {

  @CommandLine.Option(names = { "--help" }, usageHelp = true, description = "Display help about a command")
  private boolean help;

}
