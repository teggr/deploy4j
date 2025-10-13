package dev.deploy4j.cli;

import picocli.CommandLine;

@CommandLine.Command(
  name = "build",
  description = "Build application image",
  subcommands = {
    BuildCommand.PullCommand.class
  }
)
public class BuildCommand {

  @CommandLine.Command(
    name = "pull",
    description = "Pull app image from registry onto servers")
  public static class PullCommand extends BaseCommand {

    @Override
    protected void execute(Cli cli) {
      cli.build().pull();
    }

  }

}
