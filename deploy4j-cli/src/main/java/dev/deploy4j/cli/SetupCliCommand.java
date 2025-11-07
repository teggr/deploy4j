package dev.deploy4j.cli;

import dev.deploy4j.deploy.DeployApplicationContext;
import picocli.CommandLine;

@CommandLine.Command(
  name = "setup",
  description = "Setup all accessories, push the env, and deploy app to servers")
public class SetupCliCommand extends BaseCliCommand {

  @Override
  protected void execute(DeployApplicationContext deployApplicationContext) {

    printRuntime(() -> {

      deployApplicationContext.lockManager().withLock(deployApplicationContext.deployContext(), () -> {

        deployApplicationContext.deploy().setup(deployApplicationContext.deployContext());

      });

    });

  }

}
