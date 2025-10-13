package dev.deploy4j.cli;

import picocli.CommandLine;

@CommandLine.Command(
  name = "registry",
  description = "Login and out of the image registry",
  subcommands = {
    RegistryCommand.LoginCommand.class
  }
)
public class RegistryCommand {

  @CommandLine.Command(
    name = "login",
    description = "Log in to registry locally and remotely")
  public static class LoginCommand extends BaseCommand {

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
  public static class LogoutCommand extends BaseCommand {

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
