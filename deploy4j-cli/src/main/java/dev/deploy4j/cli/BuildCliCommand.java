package dev.deploy4j.cli;

import dev.deploy4j.deploy.cli.Cli;
import picocli.CommandLine;

@CommandLine.Command(
  name = "build",
  description = "Build application image",
  subcommands = {
    BuildCliCommand.PullCliCommand.class
  }
)
public class BuildCliCommand {

  @CommandLine.Command(
    name = "pull",
    description = "Pull app image from registry onto servers")
  public static class PullCliCommand extends BaseCliCommand {

    @Override
    protected void execute(Cli cli) {
      cli.build().pull();
    }

  }

}
