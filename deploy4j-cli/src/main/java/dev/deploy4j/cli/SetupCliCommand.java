package dev.deploy4j.cli;

import dev.deploy4j.deploy.DeployApplicationContext;
import picocli.CommandLine;

@CommandLine.Command(
  name = "setup",
  description = "Setup all accessories, push the env, and deploy app to servers")
public class SetupCliCommand extends BaseCliCommand {

  @CommandLine.Option(names = "-P", description = "Skip image build and push", defaultValue = "false")
  private boolean skipPush;

  @Override
  protected void execute(DeployApplicationContext deployApplicationContext) {

    printRuntime(() -> {

      deployApplicationContext.lockManager().withLock(deployApplicationContext.commander(), () -> {

        deployApplicationContext.deploy().setup(deployApplicationContext.commander(), skipPush);

      });

    });

  }

}
