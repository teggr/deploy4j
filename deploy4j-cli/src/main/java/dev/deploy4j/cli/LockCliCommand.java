package dev.deploy4j.cli;

import dev.deploy4j.deploy.DeployApplicationContext;
import picocli.CommandLine;

import java.util.concurrent.Callable;

@CommandLine.Command(
  name = "lock",
  description = "Manage the deploy lock",
  subcommands = {
    LockCliCommand.StatusCliCommand.class,
    LockCliCommand.AcquireCliCommand.class,
    LockCliCommand.ReleaseCliCommand.class
  }
)
public class LockCliCommand implements Callable<Integer> {

  @CommandLine.Mixin
  private HelpOptions helpOptions = new HelpOptions();

  @Override
  public Integer call() throws Exception {
    CommandLine.usage(this, System.out);
    return 0;
  }

  @CommandLine.Command(
    name = "status",
    description = "Report lock status")
  public static class StatusCliCommand extends BaseCliCommand {

    @Override
    protected void execute(DeployApplicationContext deployApplicationContext) {
      deployApplicationContext.lock().status(deployApplicationContext.deployContext());
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
      deployApplicationContext.lock().acquire(deployApplicationContext.deployContext(), message);
    }

  }

  @CommandLine.Command(
    name = "release",
    description = "Release the deploy lock")
  public static class ReleaseCliCommand extends BaseCliCommand {

    @Override
    protected void execute(DeployApplicationContext deployApplicationContext) {
      deployApplicationContext.lock().release(deployApplicationContext.deployContext());
    }

  }

}
