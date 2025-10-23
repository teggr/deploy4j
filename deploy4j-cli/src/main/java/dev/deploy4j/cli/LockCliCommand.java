package dev.deploy4j.cli;

import dev.deploy4j.deploy.DeployApplicationContext;
import picocli.CommandLine;

@CommandLine.Command(
  name = "lock",
  description = "Manage the deploy lock",
  subcommands = {
    LockCliCommand.StatusCliCommand.class,
    LockCliCommand.AcquireCliCommand.class,
    LockCliCommand.ReleaseCliCommand.class
  }
)
public class LockCliCommand {

  @CommandLine.Command(
    name = "status",
    description = "Report lock status")
  public static class StatusCliCommand extends BaseCliCommand {

    @Override
    protected void execute(DeployApplicationContext deployApplicationContext) {
      deployApplicationContext.lock().status(deployApplicationContext.commander());
    }

  }

  @CommandLine.Command(
    name = "acquire",
    description = "Acquire the deploy lock")
  public static class AcquireCliCommand extends BaseCliCommand {

    @CommandLine.Option(names = "-m", description = "A lock message", required = true)
    private String message;

    @Override
    protected void execute(DeployApplicationContext deployApplicationContext) {
      deployApplicationContext.lock().acquire(deployApplicationContext.commander(), message);
    }

  }

  @CommandLine.Command(
    name = "release",
    description = "Release the deploy lock")
  public static class ReleaseCliCommand extends BaseCliCommand {

    @Override
    protected void execute(DeployApplicationContext deployApplicationContext) {
      deployApplicationContext.lock().release(deployApplicationContext.commander());
    }

  }

}
