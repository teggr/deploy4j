package dev.deploy4j.cli;

import dev.deploy4j.deploy.cli.Cli;
import picocli.CommandLine;

@CommandLine.Command(
  name = "registry",
  description = "Login and out of the image registry",
  subcommands = {
    RegistryCliCommand.LoginCliCommand.class
  }
)
public class RegistryCliCommand {

  @CommandLine.Command(
    name = "login",
    description = "Log in to registry locally and remotely")
  public static class LoginCliCommand extends BaseCliCommand {

    @CommandLine.Option(names = "-L", description = "Skip local login", defaultValue = "false")
    private boolean skipLocal;

    @CommandLine.Option(names = "-R", description = "Skip remote login", defaultValue = "false")
    private boolean skipRemote;

    @Override
    protected void execute(Cli cli) {
      // TODO: skips
      cli.registry().login();
    }

  }

  @CommandLine.Command(
    name = "logout",
    description = "Log out of registry locally and remotely")
  public static class LogoutCommand extends BaseCliCommand {

    @CommandLine.Option(names = "-L", description = "Skip local login", defaultValue = "false")
    private boolean skipLocal;

    @CommandLine.Option(names = "-R", description = "Skip remote login", defaultValue = "false")
    private boolean skipRemote;

    @Override
    protected void execute(Cli cli) {
      // TODO: skips
      cli.registry().logout();
    }

  }

}
