package dev.deploy4j.cli;

import dev.deploy4j.deploy.DeployApplicationContext;
import picocli.CommandLine;

import java.util.concurrent.Callable;

@CommandLine.Command(
  name = "build",
  description = "Build application image",
  subcommands = {
    BuildCliCommand.PullCliCommand.class
  }
)
public class BuildCliCommand implements Callable<Integer> {

  @CommandLine.Mixin
  private HelpOptions helpOptions = new HelpOptions();

  @Override
  public Integer call() throws Exception {
    CommandLine.usage(this, System.out);
    return 0;
  }

  @CommandLine.Command(
    name = "pull",
    description = "Pull app image from registry onto servers")
  public static class PullCliCommand extends BaseCliCommand {

    @Override
    protected void execute(DeployApplicationContext deployApplicationContext) {
      deployApplicationContext.build().pull(deployApplicationContext.deployContext());
    }

  }

}
