package dev.deploy4j.cli;

import dev.deploy4j.deploy.DeployApplicationContext;
import picocli.CommandLine;

import java.util.Map;
import java.util.concurrent.Callable;

@CommandLine.Command(
  name = "secrets",
  description = "Helpers for extracting secrets",
  subcommands = {
    SecretsCliCommand.PrintCliCommand.class
  })
public class SecretsCliCommand implements Callable<Integer> {

  @CommandLine.Mixin
  private HelpOptions helpOptions = new HelpOptions();

  @Override
  public Integer call() throws Exception {
    CommandLine.usage(this, System.out);
    return 0;
  }

  @CommandLine.Command(
    name = "print",
    mixinStandardHelpOptions = true,
    description = "Print the secrets (for debugging)"
  )
  public static class PrintCliCommand extends BaseCliCommand {

    @Override
    protected void execute(DeployApplicationContext deployApplicationContext) {
      Map<String, String> all = deployApplicationContext.deployContext().config().secrets().getAll();
      all.forEach((key, value) -> {
        System.out.println("%s=%s".formatted(key, value));
      });
    }

  }

}
