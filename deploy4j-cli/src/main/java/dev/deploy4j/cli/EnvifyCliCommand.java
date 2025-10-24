package dev.deploy4j.cli;

import dev.deploy4j.deploy.DeployApplicationContext;
import picocli.CommandLine;

@CommandLine.Command(
  name = "envify",
  description = "Create .env by evaluating .env.thyme (or .env.staging.thyme -> .env.staging when using -d staging)")
public class EnvifyCliCommand extends BaseCliCommand {

  @CommandLine.Option(names = "-P", description = "Skip .env file push", defaultValue = "false")
  private boolean skipPush;

  @Override
  protected void execute(DeployApplicationContext deployApplicationContext) {
    deployApplicationContext.env().envify(deployApplicationContext.commander(), skipPush, destination);
  }

}
