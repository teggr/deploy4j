package dev.deploy4j.cli;

import dev.deploy4j.deploy.DeployApplicationContext;
import picocli.CommandLine;

@CommandLine.Command(
  name = "accessory",
  description = "Manage accessories (db/redis/search)",
  subcommands = {
    AccessoryCliCommand.BootCliCommand.class,
    AccessoryCliCommand.UploadCliCommand.class,
    AccessoryCliCommand.DirectoriesCliCommand.class,
    AccessoryCliCommand.RebootCliCommand.class,
    AccessoryCliCommand.StartCliCommand.class,
    AccessoryCliCommand.StopCliCommand.class,
    AccessoryCliCommand.RestartCliCommand.class,
    AccessoryCliCommand.DetailsCliCommand.class,
    AccessoryCliCommand.ExecCliCommand.class,
    AccessoryCliCommand.LogsCliCommand.class,
    AccessoryCliCommand.RemoveCliCommand.class,
  })
public class AccessoryCliCommand {

  @CommandLine.Mixin
  private HelpOptions helpOptions = new HelpOptions();

  @CommandLine.Command(
    name = "boot",
    mixinStandardHelpOptions = true,
    description = "Boot new accessory service on host (use NAME=all to boot all accessories)"
  )
  public static class BootCliCommand extends BaseCliCommand {

    @CommandLine.Parameters(index = "0", description = "Accessory name", defaultValue = "all", paramLabel = "NAME")
    private String name;

    @CommandLine.Parameters(index = "1", defaultValue = "true")
    private boolean login;

    @Override
    protected void execute(DeployApplicationContext deployApplicationContext) {
      deployApplicationContext.accessory().boot(deployApplicationContext.commander(), name, login);
    }

  }

  @CommandLine.Command(
    name = "upload",
    mixinStandardHelpOptions = true,
    description = "Upload accessory files to host"
  )
  public static class UploadCliCommand extends BaseCliCommand {

    @CommandLine.Parameters(index = "0", description = "Accessory name", defaultValue = "all", paramLabel = "NAME")
    private String name;

    @Override
    protected void execute(DeployApplicationContext deployApplicationContext) {
      deployApplicationContext.accessory().upload(deployApplicationContext.commander(), name);
    }

  }

  @CommandLine.Command(
    name = "directories",
    mixinStandardHelpOptions = true,
    description = "Create accessory directories on host"
  )
  public static class DirectoriesCliCommand extends BaseCliCommand {

    @CommandLine.Parameters(index = "0", description = "Accessory name", defaultValue = "all", paramLabel = "NAME")
    private String name;

    @Override
    protected void execute(DeployApplicationContext deployApplicationContext) {
      deployApplicationContext.accessory().directories(deployApplicationContext.commander(), name);
    }

  }

  @CommandLine.Command(
    name = "reboot",
    mixinStandardHelpOptions = true,
    description = "Reboot existing accessory on host (stop container, remove container, start new container; use NAME=all to boot all accessories)"
  )
  public static class RebootCliCommand extends BaseCliCommand {

    @CommandLine.Parameters(index = "0", description = "Accessory name", defaultValue = "all", paramLabel = "NAME")
    private String name;

    @Override
    protected void execute(DeployApplicationContext deployApplicationContext) {
      deployApplicationContext.accessory().reboot(deployApplicationContext.commander(), name);
    }

  }

  @CommandLine.Command(
    name = "start",
    mixinStandardHelpOptions = true,
    description = "Start existing accessory container on host"
  )
  public static class StartCliCommand extends BaseCliCommand {

    @CommandLine.Parameters(index = "0", description = "Accessory name", defaultValue = "all", paramLabel = "NAME")
    private String name;

    @Override
    protected void execute(DeployApplicationContext deployApplicationContext) {
      deployApplicationContext.accessory().start(deployApplicationContext.commander(), name);
    }

  }

  @CommandLine.Command(
    name = "stop",
    mixinStandardHelpOptions = true,
    description = "Stop existing accessory container on host"
  )
  public static class StopCliCommand extends BaseCliCommand {

    @CommandLine.Parameters(index = "0", description = "Accessory name", defaultValue = "all", paramLabel = "NAME")
    private String name;

    @Override
    protected void execute(DeployApplicationContext deployApplicationContext) {
      deployApplicationContext.accessory().stop(deployApplicationContext.commander(), name);
    }

  }

  @CommandLine.Command(
    name = "restart",
    mixinStandardHelpOptions = true,
    description = "Restart existing accessory container on host"
  )
  public static class RestartCliCommand extends BaseCliCommand {

    @CommandLine.Parameters(index = "0", description = "Accessory name", defaultValue = "all", paramLabel = "NAME")
    private String name;

    @Override
    protected void execute(DeployApplicationContext deployApplicationContext) {
      deployApplicationContext.accessory().restart(deployApplicationContext.commander(), name);
    }

  }

  @CommandLine.Command(
    name = "details",
    mixinStandardHelpOptions = true,
    description = "Show details about accessory on host (use NAME=all to show all accessories)"
  )
  public static class DetailsCliCommand extends BaseCliCommand {

    @CommandLine.Parameters(index = "0", description = "Accessory name", defaultValue = "all", paramLabel = "NAME")
    private String name;

    @Override
    protected void execute(DeployApplicationContext deployApplicationContext) {
      deployApplicationContext.accessory().details(deployApplicationContext.commander(), name);
    }

  }

  @CommandLine.Command(
    name = "exec",
    description = "Execute a custom command on servers (use --help to show options)")
  public static class ExecCliCommand extends BaseCliCommand {

    @CommandLine.Option(names = "-i", description = "Execute command over ssh for an interactive shell (use for console/bash)", defaultValue = "false")
    private boolean interactive;

    @CommandLine.Option(names = "--reuse", description = "Reuse currently running container instead of starting a new one", defaultValue = "false")
    private boolean reuse;

    @CommandLine.Parameters(index = "0")
    private String name;

    @CommandLine.Parameters(index = "1")
    private String cmd;

    @Override
    protected void execute(DeployApplicationContext deployApplicationContext) {
      deployApplicationContext.accessory().exec(deployApplicationContext.commander(), interactive, reuse, name, cmd);
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

    @CommandLine.Parameters(index = "0")
    private String name;

    @Override
    protected void execute(DeployApplicationContext deployApplicationContext) {
      deployApplicationContext.app().logs(deployApplicationContext.commander(), since,lines, grep, grepOptions, follow);
    }

  }

  @CommandLine.Command(
    name = "remove",
    mixinStandardHelpOptions = true,
    description = "Remove accessory container, image and data directory from host (use NAME=all to remove all accessories)"
  )
  public static class RemoveCliCommand extends BaseCliCommand {

    @CommandLine.Option(names = "-y", description = "Proceed without confirmation question", defaultValue = "false")
    private boolean confirmed;

    @CommandLine.Parameters(index = "0", description = "Accessory name", defaultValue = "all", paramLabel = "NAME")
    private String name;

    @Override
    protected void execute(DeployApplicationContext deployApplicationContext) {
      // TODO: confirmation
      deployApplicationContext.accessory().remove(deployApplicationContext.commander(), name);
    }

  }

  @CommandLine.Command(
    name = "remove_container",
    mixinStandardHelpOptions = true,
    description = "Remove accessory container from host"
  )
  public static class RemoveContainerCommand extends BaseCliCommand {

    @CommandLine.Parameters(index = "0", description = "Accessory name", defaultValue = "all", paramLabel = "NAME")
    private String name;

    @Override
    protected void execute(DeployApplicationContext deployApplicationContext) {
      deployApplicationContext.accessory().removeContainer(deployApplicationContext.commander(), name);
    }

  }

  @CommandLine.Command(
    name = "remove_image",
    mixinStandardHelpOptions = true,
    description = "Remove accessory image from host"
  )
  public static class RemoveImageCommand extends BaseCliCommand {

    @CommandLine.Parameters(index = "0", description = "Accessory name", defaultValue = "all", paramLabel = "NAME")
    private String name;

    @Override
    protected void execute(DeployApplicationContext deployApplicationContext) {
      deployApplicationContext.accessory().removeImage(deployApplicationContext.commander(), name);
    }

  }

  @CommandLine.Command(
    name = "remove_service_directory",
    mixinStandardHelpOptions = true,
    description = "Remove accessory directory used for uploaded files and data directories from host"
  )
  public static class RemoveServiceDirectoryCommand extends BaseCliCommand {

    @CommandLine.Parameters(index = "0", description = "Accessory name", defaultValue = "all", paramLabel = "NAME")
    private String name;

    @Override
    protected void execute(DeployApplicationContext deployApplicationContext) {
      deployApplicationContext.accessory().removeServiceDirectory(deployApplicationContext.commander(), name);
    }

  }

}
