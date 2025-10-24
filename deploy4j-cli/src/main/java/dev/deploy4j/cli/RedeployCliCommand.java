package dev.deploy4j.cli;

import dev.deploy4j.deploy.DeployApplicationContext;
import picocli.CommandLine;

@CommandLine.Command(
  name = "redeploy",
  description = "Deploy app to servers without bootstrapping servers, starting Traefik, pruning, and registry login")
public class RedeployCliCommand extends BaseCliCommand {

  @CommandLine.Option(names = "-P", description = "Skip image build and push", defaultValue = "false")
  private boolean skipPush;

  @Override
  protected void execute(DeployApplicationContext deployApplicationContext) {

    printRuntime(() -> {

      deployApplicationContext.deploy().redeploy(deployApplicationContext.commander(), skipPush);

    });

  }

}
