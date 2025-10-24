package dev.deploy4j.cli;

import dev.deploy4j.deploy.DeployApplicationContext;
import picocli.CommandLine;

@CommandLine.Command(
  name = "traefik",
  description = "Manage Traefik load balancer",
  subcommands = {
    TraefikCliCommand.BootCliCommand.class,
    TraefikCliCommand.RebootCliCommand.class,
    TraefikCliCommand.StartCliCommand.class,
    TraefikCliCommand.StopCliCommand.class,
    TraefikCliCommand.RestartCliCommand.class,
    TraefikCliCommand.DetailsCliCommand.class,
    TraefikCliCommand.LogsCliCommand.class,
    TraefikCliCommand.RemoveCliCommand.class,
    TraefikCliCommand.RemoveContainerCliCommand.class,
    TraefikCliCommand.RemoveImageCliCommand.class
  }
)
public class TraefikCliCommand {

  @CommandLine.Mixin
  private HelpOptions helpOptions = new HelpOptions();

  @CommandLine.Command(
    name = "boot",
    description = "Boot Traefik on servers")
  public static class BootCliCommand extends BaseCliCommand {

    @Override
    protected void execute(DeployApplicationContext deployApplicationContext) {
      deployApplicationContext.traefik().boot(deployApplicationContext.commander());
    }

  }

  @CommandLine.Command(
    name = "reboot",
    description = "Reboot Traefik on servers (stop container, remove container, start new container)")
  public static class RebootCliCommand extends BaseCliCommand {

    @CommandLine.Option(names = "--rolling", description = "Reboot traefik on hosts in sequence, rather than in parallel", defaultValue = "false")
    private boolean rolling;

    @CommandLine.Option(names = "-y", description = "Proceed without confirmation question", defaultValue = "false")
    private boolean confirmed;

    @Override
    protected void execute(DeployApplicationContext deployApplicationContext) {
      // TODO: confirmation prompt
      deployApplicationContext.traefik().reboot(deployApplicationContext.commander(), rolling);
    }

  }

  @CommandLine.Command(
    name = "start",
    description = "Start existing Traefik container on servers")
  public static class StartCliCommand extends BaseCliCommand {

    @Override
    protected void execute(DeployApplicationContext deployApplicationContext) {
      deployApplicationContext.traefik().start(deployApplicationContext.commander());
    }

  }

  @CommandLine.Command(
    name = "stop",
    description = "Stop existing Traefik container on servers")
  public static class StopCliCommand extends BaseCliCommand {

    @Override
    protected void execute(DeployApplicationContext deployApplicationContext) {
      deployApplicationContext.traefik().stop(deployApplicationContext.commander());
    }

  }

  @CommandLine.Command(
    name = "restart",
    description = "Restart existing Traefik container on servers")
  public static class RestartCliCommand extends BaseCliCommand {

    @Override
    protected void execute(DeployApplicationContext deployApplicationContext) {
      deployApplicationContext.traefik().restart(deployApplicationContext.commander());
    }

  }

  @CommandLine.Command(
    name = "details",
    description = "Show details about Traefik container from servers")
  public static class DetailsCliCommand extends BaseCliCommand {

    @Override
    protected void execute(DeployApplicationContext deployApplicationContext) {
      deployApplicationContext.traefik().details(deployApplicationContext.commander());
    }

  }

  @CommandLine.Command(
    name = "logs",
    description = "Show log lines from Traefik on servers")
  public static class LogsCliCommand extends BaseCliCommand {

    @CommandLine.Option(names = "-s", description = "Show logs since timestamp (e.g. 2013-01-02T13:23:37Z) or relative (e.g. 42m for 42 minutes)")
    private String since;

    @CommandLine.Option(names = "-n", description = "Number of log lines to pull from each server")
    private Integer lines;

    @CommandLine.Option(names = "-g", description = "Show lines with grep match only (use this to fetch specific requests by id)")
    private String grep;

    @CommandLine.Option(names = "-o", description = "Additional options supplied to grep")
    private String grepOptions;

    @CommandLine.Option(names = "-f", description = "Follow logs on primary server (or specific host set by --hosts)", defaultValue = "false")
    private boolean follow;

    @Override
    protected void execute(DeployApplicationContext deployApplicationContext) {
      deployApplicationContext.traefik().logs(deployApplicationContext.commander(), since,lines, grep, grepOptions, follow);
    }

  }

  @CommandLine.Command(
    name = "remove",
    description = "Remove Traefik container and image from servers")
  public static class RemoveCliCommand extends BaseCliCommand {

    @Override
    protected void execute(DeployApplicationContext deployApplicationContext) {
      deployApplicationContext.traefik().remove(deployApplicationContext.commander());
    }

  }

  @CommandLine.Command(
    name = "remove_container",
    description = "Remove Traefik container from servers")
  public static class RemoveContainerCliCommand extends BaseCliCommand {

    @Override
    protected void execute(DeployApplicationContext deployApplicationContext) {
      deployApplicationContext.traefik().removeContainer(deployApplicationContext.commander());
    }

  }

  @CommandLine.Command(
    name = "remove_image",
    description = "Remove Traefik image from servers")
  public static class RemoveImageCliCommand extends BaseCliCommand {

    @Override
    protected void execute(DeployApplicationContext deployApplicationContext) {
      deployApplicationContext.traefik().removeImage(deployApplicationContext.commander());
    }

  }

}
