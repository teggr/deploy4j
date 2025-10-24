package dev.deploy4j.cli;

import dev.deploy4j.deploy.DeployApplicationContext;
import picocli.CommandLine;

@CommandLine.Command(
  name = "remove",
  description = "Remove Traefik, app, accessories, and registry session from servers")
public class RemoveCliCommand extends BaseCliCommand {

  @Override
  protected void execute(DeployApplicationContext deployApplicationContext) {
    deployApplicationContext.deploy().remove(deployApplicationContext.deployContext());
  }

}
