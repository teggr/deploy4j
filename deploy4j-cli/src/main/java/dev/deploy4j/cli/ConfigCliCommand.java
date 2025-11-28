package dev.deploy4j.cli;

import dev.deploy4j.deploy.DeployApplicationContext;
import picocli.CommandLine;

@CommandLine.Command(
  name = "config",
  description = "Show combined config (including secrets!)")
public class ConfigCliCommand extends BaseCliCommand {

  @CommandLine.Option(names = {"--format"}, description = "Format for the output [yaml]")
  String format;

  @Override
  protected void execute(DeployApplicationContext deployApplicationContext) {
    deployApplicationContext.deploy().config(deployApplicationContext.deployContext(), format);
  }

}
