package dev.deploy4j.cli;

import dev.deploy4j.deploy.DeployApplicationContext;
import picocli.CommandLine;

import java.util.Map;

@CommandLine.Command(
  name = "app",
  description = "Manage application",
  subcommands = {
    AppCliCommand.BootCliCommand.class,
    AppCliCommand.StartCliCommand.class,
    AppCliCommand.StopCliCommand.class,
    AppCliCommand.DetailsCliCommand.class,
    AppCliCommand.ExecCliCommand.class,
    AppCliCommand.ContainersCliCommand.class,
    AppCliCommand.StaleContainersCliCommand.class,
    AppCliCommand.ImagesCliCommand.class,
    AppCliCommand.LogsCliCommand.class,
    AppCliCommand.RemoveCliCommand.class,
    AppCliCommand.RemoveContainerCliCommand.class,
    AppCliCommand.RemoveContainersCliCommand.class,
    AppCliCommand.RemoveImagesCliCommand.class,
    AppCliCommand.VersionCliCommand.class
  }
)
public class AppCliCommand {

  @CommandLine.Mixin
  private HelpOptions helpOptions = new HelpOptions();

  @CommandLine.Command(
    name = "boot",
    description = "Boot app on servers (or reboot app if already running)")
  public static class BootCliCommand extends BaseCliCommand {

    @Override
    protected void execute(DeployApplicationContext deployApplicationContext) {
      deployApplicationContext.app().boot(deployApplicationContext.commander());
    }

  }

  @CommandLine.Command(
    name = "start",
    description = "Start existing app container on servers")
  public static class StartCliCommand extends BaseCliCommand {

    @Override
    protected void execute(DeployApplicationContext deployApplicationContext) {
      deployApplicationContext.app().start(deployApplicationContext.commander());
    }

  }

  @CommandLine.Command(
    name = "stop",
    description = "Stop app container on servers")
  public static class StopCliCommand extends BaseCliCommand {

    @Override
    protected void execute(DeployApplicationContext deployApplicationContext) {
      deployApplicationContext.app().stop(deployApplicationContext.commander());
    }

  }

  @CommandLine.Command(
    name = "details",
    description = "Show details about app containers")
  public static class DetailsCliCommand extends BaseCliCommand {

    @Override
    protected void execute(DeployApplicationContext deployApplicationContext) {
      deployApplicationContext.app().details(deployApplicationContext.commander());
    }

  }

  @CommandLine.Command(
    name = "exec",
    description = "Execute a custom command on servers within the app container (use --help to show options)")
  public static class ExecCliCommand extends BaseCliCommand {

    @CommandLine.Option(names = "-i", description = "Execute command over ssh for an interactive shell (use for console/bash)", defaultValue = "false")
    private boolean interactive;

    @CommandLine.Option(names = "--reuse", description = "Reuse currently running container instead of starting a new one", defaultValue = "false")
    private boolean reuse;

    @CommandLine.Option(names = "-e", description = "Set environment variables for the command")
    private Map<String, String> env;

    @CommandLine.Parameters(index = "0")
    private String cmd;

    @Override
    protected void execute(DeployApplicationContext deployApplicationContext) {
      deployApplicationContext.app().exec(deployApplicationContext.commander(), interactive, reuse, env, cmd);
    }

  }

  @CommandLine.Command(
    name = "containers",
    description = "Show app containers on servers")
  public static class ContainersCliCommand extends BaseCliCommand {

    @Override
    protected void execute(DeployApplicationContext deployApplicationContext) {
      deployApplicationContext.app().containers(deployApplicationContext.commander());
    }

  }

  @CommandLine.Command(
    name = "stale_containers",
    description = "Detect app stale containers")
  public static class StaleContainersCliCommand extends BaseCliCommand {

    @CommandLine.Option(names = "-s", description = "Stop the stale containers found", defaultValue = "false")
    private boolean stop;

    @Override
    protected void execute(DeployApplicationContext deployApplicationContext) {
      deployApplicationContext.app().staleContainers(deployApplicationContext.commander(), stop);
    }

  }

  @CommandLine.Command(
    name = "images",
    description = "Show app images on servers")
  public static class ImagesCliCommand extends BaseCliCommand {

    @Override
    protected void execute(DeployApplicationContext deployApplicationContext) {
      deployApplicationContext.app().images(deployApplicationContext.commander());
    }

  }

  @CommandLine.Command(
    name = "logs",
    description = "Show log lines from app on servers (use --help to show options)")
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
      deployApplicationContext.app().logs(deployApplicationContext.commander(), since,lines, grep, grepOptions, follow);
    }

  }

  @CommandLine.Command(
    name = "remove",
    description = "Remove app containers and images from servers")
  public static class RemoveCliCommand extends BaseCliCommand {

    @Override
    protected void execute(DeployApplicationContext deployApplicationContext) {
      deployApplicationContext.app().containers(deployApplicationContext.commander());
    }

  }

  @CommandLine.Command(
    name = "remove_container",
    description = "Remove app container with given version from servers")
  public static class RemoveContainerCliCommand extends BaseCliCommand {

    @CommandLine.Parameters(index = "0")
    private String version;

    @Override
    protected void execute(DeployApplicationContext deployApplicationContext) {
      deployApplicationContext.app().removeContainer(deployApplicationContext.commander(), version);
    }

  }

  @CommandLine.Command(
    name = "remove_containers",
    description = "Remove all app containers from servers")
  public static class RemoveContainersCliCommand extends BaseCliCommand {

    @Override
    protected void execute(DeployApplicationContext deployApplicationContext) {
      deployApplicationContext.app().removeContainers(deployApplicationContext.commander());
    }

  }

  @CommandLine.Command(
    name = "remove_images",
    description = "Remove all app images from servers")
  public static class RemoveImagesCliCommand extends BaseCliCommand {

    @Override
    protected void execute(DeployApplicationContext deployApplicationContext) {
      deployApplicationContext.app().removeImages(deployApplicationContext.commander());
    }

  }

  @CommandLine.Command(
    name = "version",
    description = "Show app version currently running on servers")
  public static class VersionCliCommand extends BaseCliCommand {

    @Override
    protected void execute(DeployApplicationContext deployApplicationContext) {
      deployApplicationContext.app().version(deployApplicationContext.commander());
    }

  }

}
