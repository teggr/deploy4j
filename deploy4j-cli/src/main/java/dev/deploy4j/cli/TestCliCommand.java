package dev.deploy4j.cli;

import dev.deploy4j.deploy.DeployApplicationContext;
import picocli.CommandLine;

@CommandLine.Command(
  name = "test",
  description = "Test connectivity to servers"
)
public class TestCliCommand extends BaseCliCommand {

  @Override
  protected void execute(DeployApplicationContext deployApplicationContext) {

    deployApplicationContext.deploy().test(deployApplicationContext.deployContext());

  }

}
