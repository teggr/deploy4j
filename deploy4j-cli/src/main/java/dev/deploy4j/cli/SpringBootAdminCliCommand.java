package dev.deploy4j.cli;

import dev.deploy4j.deploy.DeployApplicationContext;
import picocli.CommandLine;

import java.util.concurrent.Callable;

@CommandLine.Command(
  name = "spring_boot_admin",
  description = "Manage Spring Boot Admin dashboard",
  subcommands = {
    SpringBootAdminCliCommand.BootCliCommand.class,
    SpringBootAdminCliCommand.RebootCliCommand.class,
    SpringBootAdminCliCommand.StartCliCommand.class,
    SpringBootAdminCliCommand.StopCliCommand.class,
    SpringBootAdminCliCommand.RestartCliCommand.class,
    SpringBootAdminCliCommand.DetailsCliCommand.class,
    SpringBootAdminCliCommand.LogsCliCommand.class,
    SpringBootAdminCliCommand.RemoveCliCommand.class,
    SpringBootAdminCliCommand.RemoveContainerCliCommand.class,
    SpringBootAdminCliCommand.RemoveImageCliCommand.class
  }
)
public class SpringBootAdminCliCommand implements Callable<Integer> {

  @CommandLine.Mixin
  private HelpOptions helpOptions = new HelpOptions();

  @Override
  public Integer call() throws Exception {
    CommandLine.usage(this, System.out);
    return 0;
  }

  @CommandLine.Command(
    name = "boot",
    description = "Boot Spring Boot Admin on servers")
  public static class BootCliCommand extends BaseCliCommand {

    @Override
    protected void execute(DeployApplicationContext deployApplicationContext) {
      deployApplicationContext.springBootAdmin().boot(deployApplicationContext.deployContext());
    }

  }

  @CommandLine.Command(
    name = "reboot",
    description = "Reboot Spring Boot Admin on servers (stop container, remove container, start new container)")
  public static class RebootCliCommand extends BaseCliCommand {

    @CommandLine.Option(names = "--rolling", description = "Reboot spring-boot-admin on hosts in sequence, rather than in parallel", defaultValue = "false")
    private boolean rolling;

    @CommandLine.Option(names = "-y", description = "Proceed without confirmation question", defaultValue = "false")
    private boolean confirmed;

    @Override
    protected void execute(DeployApplicationContext deployApplicationContext) {
      // TODO: confirmation prompt
      deployApplicationContext.springBootAdmin().reboot(deployApplicationContext.deployContext(), rolling);
    }

  }

  @CommandLine.Command(
    name = "start",
    description = "Start existing Spring Boot Admin container on servers")
  public static class StartCliCommand extends BaseCliCommand {

    @Override
    protected void execute(DeployApplicationContext deployApplicationContext) {
      deployApplicationContext.springBootAdmin().start(deployApplicationContext.deployContext());
    }

  }

  @CommandLine.Command(
    name = "stop",
    description = "Stop existing Spring Boot Admin container on servers")
  public static class StopCliCommand extends BaseCliCommand {

    @Override
    protected void execute(DeployApplicationContext deployApplicationContext) {
      deployApplicationContext.springBootAdmin().stop(deployApplicationContext.deployContext());
    }

  }

  @CommandLine.Command(
    name = "restart",
    description = "Restart existing Spring Boot Admin container on servers")
  public static class RestartCliCommand extends BaseCliCommand {

    @Override
    protected void execute(DeployApplicationContext deployApplicationContext) {
      deployApplicationContext.springBootAdmin().restart(deployApplicationContext.deployContext());
    }

  }

  @CommandLine.Command(
    name = "details",
    description = "Show details about Spring Boot Admin container from servers")
  public static class DetailsCliCommand extends BaseCliCommand {

    @Override
    protected void execute(DeployApplicationContext deployApplicationContext) {
      deployApplicationContext.springBootAdmin().details(deployApplicationContext.deployContext());
    }

  }

  @CommandLine.Command(
    name = "logs",
    description = "Show log lines from Spring Boot Admin on servers")
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
      deployApplicationContext.springBootAdmin().logs(deployApplicationContext.deployContext(), since, lines, grep, grepOptions, follow);
    }

  }

  @CommandLine.Command(
    name = "remove",
    description = "Remove Spring Boot Admin container and image from servers")
  public static class RemoveCliCommand extends BaseCliCommand {

    @Override
    protected void execute(DeployApplicationContext deployApplicationContext) {
      deployApplicationContext.springBootAdmin().remove(deployApplicationContext.deployContext());
    }

  }

  @CommandLine.Command(
    name = "remove_container",
    description = "Remove Spring Boot Admin container from servers")
  public static class RemoveContainerCliCommand extends BaseCliCommand {

    @Override
    protected void execute(DeployApplicationContext deployApplicationContext) {
      deployApplicationContext.springBootAdmin().removeContainer(deployApplicationContext.deployContext());
    }

  }

  @CommandLine.Command(
    name = "remove_image",
    description = "Remove Spring Boot Admin image from servers")
  public static class RemoveImageCliCommand extends BaseCliCommand {

    @Override
    protected void execute(DeployApplicationContext deployApplicationContext) {
      deployApplicationContext.springBootAdmin().removeImage(deployApplicationContext.deployContext());
    }

  }

}
