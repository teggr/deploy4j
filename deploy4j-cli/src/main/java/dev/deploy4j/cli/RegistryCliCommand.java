package dev.deploy4j.cli;

import dev.deploy4j.deploy.DeployApplicationContext;
import picocli.CommandLine;

import java.util.concurrent.Callable;

@CommandLine.Command(
  name = "registry",
  description = "Login and out of the image registry",
  subcommands = {
    RegistryCliCommand.LoginCliCommand.class
  }
)
public class RegistryCliCommand implements Callable<Integer> {

  @CommandLine.Mixin
  private HelpOptions helpOptions = new HelpOptions();

  @Override
  public Integer call() throws Exception {
    CommandLine.usage(this, System.out);
    return 0;
  }

  @CommandLine.Command(
    name = "login",
    description = "Log in to registry locally and remotely")
  public static class LoginCliCommand extends BaseCliCommand {

    @CommandLine.Option(names = "-L", description = "Skip local login", defaultValue = "false")
    private boolean skipLocal;

    @CommandLine.Option(names = "-R", description = "Skip remote login", defaultValue = "false")
    private boolean skipRemote;

    @Override
    protected void execute(DeployApplicationContext deployApplicationContext) {
      // TODO: skips
      deployApplicationContext.registry().login(deployApplicationContext.deployContext());
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
    protected void execute(DeployApplicationContext deployApplicationContext) {
      // TODO: skips
      deployApplicationContext.registry().logout(deployApplicationContext.deployContext());
    }

  }

}
