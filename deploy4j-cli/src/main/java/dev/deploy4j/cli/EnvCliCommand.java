package dev.deploy4j.cli;

import dev.deploy4j.deploy.DeployApplicationContext;
import picocli.CommandLine;

@CommandLine.Command(
  name = "env",
  description = "Manage environment files",
  subcommands = {
    EnvCliCommand.PushCliCommand.class,
  }
)
public class EnvCliCommand {

  @CommandLine.Mixin
  private HelpOptions helpOptions = new HelpOptions();

  @CommandLine.Command(
    name = "push",
    description = "Push the env file to the remote hosts")
  public static class PushCliCommand extends BaseCliCommand {

    @Override
    protected void execute(DeployApplicationContext deployApplicationContext) {
      deployApplicationContext.env().push(deployApplicationContext.deployContext());
    }

  }

}
