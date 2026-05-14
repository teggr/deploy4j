package dev.deploy4j.cli;

import dev.deploy4j.deploy.DeployApplicationContext;
import picocli.CommandLine;

import java.util.concurrent.Callable;

@CommandLine.Command(
  name = "gateway",
  description = "Manage Gateway load balancer",
  subcommands = {
    GatewayCliCommand.BootCliCommand.class,
    GatewayCliCommand.RebootCliCommand.class,
    GatewayCliCommand.StartCliCommand.class,
    GatewayCliCommand.StopCliCommand.class,
    GatewayCliCommand.RestartCliCommand.class,
    GatewayCliCommand.DetailsCliCommand.class,
    GatewayCliCommand.LogsCliCommand.class,
    GatewayCliCommand.RemoveCliCommand.class,
    GatewayCliCommand.RemoveContainerCliCommand.class,
    GatewayCliCommand.RemoveImageCliCommand.class
  }
)
public class GatewayCliCommand implements Callable<Integer> {

  @CommandLine.Mixin
  private HelpOptions helpOptions = new HelpOptions();

  @Override
  public Integer call() throws Exception {
    CommandLine.usage(this, System.out);
    return 0;
  }

  @CommandLine.Command(
    name = "boot",
    description = "Boot Gateway on servers")
  public static class BootCliCommand extends BaseCliCommand {

    @Override
    protected void execute(DeployApplicationContext deployApplicationContext) {
      deployApplicationContext.gateway().boot(deployApplicationContext.deployContext());
    }

  }

  @CommandLine.Command(
    name = "reboot",
    description = "Reboot Gateway on servers (stop container, remove container, start new container)")
  public static class RebootCliCommand extends BaseCliCommand {

    @CommandLine.Option(names = "--rolling", description = "Reboot gateway on hosts in sequence, rather than in parallel", defaultValue = "false")
    private boolean rolling;

    @CommandLine.Option(names = "-y", description = "Proceed without confirmation question", defaultValue = "false")
    private boolean confirmed;

    @Override
    protected void execute(DeployApplicationContext deployApplicationContext) {
      // TODO: confirmation prompt
      deployApplicationContext.gateway().reboot(deployApplicationContext.deployContext(), rolling);
    }

  }

  @CommandLine.Command(
    name = "start",
    description = "Start existing Gateway container on servers")
  public static class StartCliCommand extends BaseCliCommand {

    @Override
    protected void execute(DeployApplicationContext deployApplicationContext) {
      deployApplicationContext.gateway().start(deployApplicationContext.deployContext());
    }

  }

  @CommandLine.Command(
    name = "stop",
    description = "Stop existing Gateway container on servers")
  public static class StopCliCommand extends BaseCliCommand {

    @Override
    protected void execute(DeployApplicationContext deployApplicationContext) {
      deployApplicationContext.gateway().stop(deployApplicationContext.deployContext());
    }

  }

  @CommandLine.Command(
    name = "restart",
    description = "Restart existing Gateway container on servers")
  public static class RestartCliCommand extends BaseCliCommand {

    @Override
    protected void execute(DeployApplicationContext deployApplicationContext) {
      deployApplicationContext.gateway().restart(deployApplicationContext.deployContext());
    }

  }

  @CommandLine.Command(
    name = "details",
    description = "Show details about Gateway container from servers")
  public static class DetailsCliCommand extends BaseCliCommand {

    @Override
    protected void execute(DeployApplicationContext deployApplicationContext) {
      deployApplicationContext.gateway().details(deployApplicationContext.deployContext());
    }

  }

  @CommandLine.Command(
    name = "logs",
    description = "Show log lines from Gateway on servers")
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
      deployApplicationContext.gateway().logs(deployApplicationContext.deployContext(), since,lines, grep, grepOptions, follow);
    }

  }

  @CommandLine.Command(
    name = "remove",
    description = "Remove Gateway container and image from servers")
  public static class RemoveCliCommand extends BaseCliCommand {

    @Override
    protected void execute(DeployApplicationContext deployApplicationContext) {
      deployApplicationContext.gateway().remove(deployApplicationContext.deployContext());
    }

  }

  @CommandLine.Command(
    name = "remove_container",
    description = "Remove Gateway container from servers")
  public static class RemoveContainerCliCommand extends BaseCliCommand {

    @Override
    protected void execute(DeployApplicationContext deployApplicationContext) {
      deployApplicationContext.gateway().removeContainer(deployApplicationContext.deployContext());
    }

  }

  @CommandLine.Command(
    name = "remove_image",
    description = "Remove Gateway image from servers")
  public static class RemoveImageCliCommand extends BaseCliCommand {

    @Override
    protected void execute(DeployApplicationContext deployApplicationContext) {
      deployApplicationContext.gateway().removeImage(deployApplicationContext.deployContext());
    }

  }

}
