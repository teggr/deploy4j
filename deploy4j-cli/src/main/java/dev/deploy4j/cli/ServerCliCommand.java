package dev.deploy4j.cli;

import dev.deploy4j.deploy.cli.Cli;
import picocli.CommandLine;

@CommandLine.Command(
  name = "server",
  description = "Boostrap servers with curl and Docker",
  subcommands = {
    ServerCliCommand.ExecCliCommand.class,
    ServerCliCommand.BootstrapCliCommand.class
  }
)
public class ServerCliCommand {

  @CommandLine.Command(
    name = "exec",
    description = "Run a custom command on the server (use --help to show options)")
  public static class ExecCliCommand extends BaseCliCommand {

    @CommandLine.Option(names = "-i", description = "Run the command interactively (use for console/bash)", defaultValue = "false")
    private boolean interactive;

    @CommandLine.Parameters(index = "0")
    private String cmd;

    @Override
    protected void execute(Cli cli) {
      cli.server().exec(interactive, cmd);
    }

  }

  @CommandLine.Command(
    name = "bootstrap",
    description = "Set up Docker to run Kamal apps")
  public static class BootstrapCliCommand extends BaseCliCommand {

    @Override
    protected void execute(Cli cli) {
      cli.server().bootstrap();
    }

  }

}
