package dev.deploy4j.cli;

import dev.deploy4j.deploy.DeployApplicationContext;
import picocli.CommandLine;

@CommandLine.Command(
  name = "details",
  description = "Show details about all containers")
public class DetailsCliCommand extends BaseCliCommand {

  @Override
  protected void execute(DeployApplicationContext deployApplicationContext) {
    deployApplicationContext.deploy().details(deployApplicationContext.deployContext());
  }

}
