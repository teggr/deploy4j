package dev.deploy4j.cli;

import picocli.CommandLine;

@CommandLine.Command(
  name = "prune",
  description = "Prune old application images and containers",
  subcommands = {
    PruneCliCommand.AllCliCommand.class,
    PruneCliCommand.ImagesCliCommand.class,
    PruneCliCommand.ContainersCliCommand.class
  }
)
public class PruneCliCommand {

  @CommandLine.Command(
    name = "all",
    description = "Prune unused images and stopped containers")
  public static class AllCliCommand extends BaseCliCommand {

    @Override
    protected void execute(Cli cli) {
      cli.prune().all();
    }

  }

  @CommandLine.Command(
    name = "images",
    description = "Prune unused images")
  public static class ImagesCliCommand extends BaseCliCommand {

    @Override
    protected void execute(Cli cli) {
      cli.prune().images();
    }

  }

  @CommandLine.Command(
    name = "container",
    description = "Prune all stopped containers, except the last n (default 5)")
  public static class ContainersCliCommand extends BaseCliCommand {

    @CommandLine.Option(names = "--retain", description = "Number of containers to retain")
    private Integer retain;

    @Override
    protected void execute(Cli cli) {
      cli.prune().containers(retain);
    }

  }

}
