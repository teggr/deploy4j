package dev.deploy4j.cli;

import picocli.CommandLine;

@CommandLine.Command(
  name = "traefik",
  description = "Manage Traefik load balancer",
  subcommands = {
    TraefikCommand.BootCommand.class,
    TraefikCommand.RebootCommand.class,
    TraefikCommand.StartCommand.class,
    TraefikCommand.StopCommand.class,
    TraefikCommand.RestartCommand.class,
    TraefikCommand.DetailsCommand.class,
    TraefikCommand.LogsCommand.class,
    TraefikCommand.RemoveCommand.class,
    TraefikCommand.RemoveContainerCommand.class,
    TraefikCommand.RemoveImageCommand.class
  }
)
public class TraefikCommand {

  @CommandLine.Command(
    name = "boot",
    description = "Boot Traefik on servers")
  public static class BootCommand extends BaseCommand {

    @Override
    protected void execute(Cli cli) {
      cli.traefik().boot();
    }

  }

  @CommandLine.Command(
    name = "reboot",
    description = "Reboot Traefik on servers (stop container, remove container, start new container)")
  public static class RebootCommand extends BaseCommand {

    @CommandLine.Option(names = "--rolling", description = "Reboot traefik on hosts in sequence, rather than in parallel", defaultValue = "false")
    private boolean rolling;

    @CommandLine.Option(names = "-y", description = "Proceed without confirmation question", defaultValue = "false")
    private boolean confirmed;

    @Override
    protected void execute(Cli cli) {
      // TODO: confirmation prompt
      cli.traefik().reboot(rolling);
    }

  }

  @CommandLine.Command(
    name = "start",
    description = "Start existing Traefik container on servers")
  public static class StartCommand extends BaseCommand {

    @Override
    protected void execute(Cli cli) {
      cli.traefik().start();
    }

  }

  @CommandLine.Command(
    name = "stop",
    description = "Stop existing Traefik container on servers")
  public static class StopCommand extends BaseCommand {

    @Override
    protected void execute(Cli cli) {
      cli.traefik().stop();
    }

  }

  @CommandLine.Command(
    name = "restart",
    description = "Restart existing Traefik container on servers")
  public static class RestartCommand extends BaseCommand {

    @Override
    protected void execute(Cli cli) {
      cli.traefik().restart();
    }

  }

  @CommandLine.Command(
    name = "details",
    description = "Show details about Traefik container from servers")
  public static class DetailsCommand extends BaseCommand {

    @Override
    protected void execute(Cli cli) {
      cli.traefik().details();
    }

  }

  @CommandLine.Command(
    name = "logs",
    description = "Show log lines from Traefik on servers")
  public static class LogsCommand extends BaseCommand {

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
    protected void execute(Cli cli) {
      cli.traefik().logs(since,lines, grep, grepOptions, follow);
    }

  }

  @CommandLine.Command(
    name = "remove",
    description = "Remove Traefik container and image from servers")
  public static class RemoveCommand extends BaseCommand {

    @Override
    protected void execute(Cli cli) {
      cli.traefik().remove();
    }

  }

  @CommandLine.Command(
    name = "remove_container",
    description = "Remove Traefik container from servers")
  public static class RemoveContainerCommand extends BaseCommand {

    @Override
    protected void execute(Cli cli) {
      cli.traefik().removeContainer();
    }

  }

  @CommandLine.Command(
    name = "remove_image",
    description = "Remove Traefik image from servers")
  public static class RemoveImageCommand extends BaseCommand {

    @Override
    protected void execute(Cli cli) {
      cli.traefik().removeImage();
    }

  }

}
