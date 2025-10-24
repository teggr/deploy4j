package dev.deploy4j.cli;

import dev.deploy4j.deploy.DeployApplicationContext;
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

  @CommandLine.Mixin
  private HelpOptions helpOptions = new HelpOptions();

  @CommandLine.Command(
    name = "all",
    description = "Prune unused images and stopped containers")
  public static class AllCliCommand extends BaseCliCommand {

    @Override
    protected void execute(DeployApplicationContext deployApplicationContext) {
      deployApplicationContext.prune().all(deployApplicationContext.deployContext());
    }

  }

  @CommandLine.Command(
    name = "images",
    description = "Prune unused images")
  public static class ImagesCliCommand extends BaseCliCommand {

    @Override
    protected void execute(DeployApplicationContext deployApplicationContext) {
      deployApplicationContext.prune().images(deployApplicationContext.deployContext());
    }

  }

  @CommandLine.Command(
    name = "containers",
    description = "Prune all stopped containers, except the last n (default 5)")
  public static class ContainersCliCommand extends BaseCliCommand {

    @CommandLine.Option(names = "--retain", description = "Number of containers to retain")
    private Integer retain;

    @Override
    protected void execute(DeployApplicationContext deployApplicationContext) {
      deployApplicationContext.prune().containers(deployApplicationContext.deployContext(), retain);
    }

  }

}
