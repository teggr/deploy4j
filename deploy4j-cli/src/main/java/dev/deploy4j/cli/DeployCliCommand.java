package dev.deploy4j.cli;

import dev.deploy4j.deploy.DeployApplicationContext;
import picocli.CommandLine;

@CommandLine.Command(
  name = "deploy",
  description = "Deploy app to servers")
public class DeployCliCommand extends BaseCliCommand {

  @CommandLine.Option(names = "-P", description = "Skip image pull", defaultValue = "false")
  private boolean skipPull;

  @Override
  protected void execute(DeployApplicationContext deployApplicationContext) {

    printRuntime(() -> {

      deployApplicationContext.deploy().deploy(deployApplicationContext.deployContext(), skipPull, false);

    });

  }

}
