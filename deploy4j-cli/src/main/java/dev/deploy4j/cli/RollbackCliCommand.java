package dev.deploy4j.cli;

import dev.deploy4j.deploy.DeployApplicationContext;
import picocli.CommandLine;

@CommandLine.Command(
  name = "rollback",
  description = "Rollback app to VERSION")
public class RollbackCliCommand extends BaseCliCommand {

  @Override
  protected void execute(DeployApplicationContext deployApplicationContext) {

    printRuntime(() -> {

      deployApplicationContext.deploy().rollback(deployApplicationContext.deployContext(), version);

    });

  }

}
