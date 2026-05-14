package dev.deploy4j.cli;

import dev.deploy4j.deploy.DeployApplicationContext;
import picocli.CommandLine;

@CommandLine.Command(
  name = "redeploy",
  description = "Deploy app to servers without bootstrapping servers, starting Gateway, pruning, and registry login")
public class RedeployCliCommand extends BaseCliCommand {

  @CommandLine.Option(names = "-P", description = "Skip image pull", defaultValue = "false")
  private boolean skipPull;

  @Override
  protected void execute(DeployApplicationContext deployApplicationContext) {

    printRuntime(() -> {

      deployApplicationContext.deploy().redeploy(deployApplicationContext.deployContext(), skipPull);

    });

  }

}
