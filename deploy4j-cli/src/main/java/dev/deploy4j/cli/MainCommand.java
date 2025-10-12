package dev.deploy4j.cli;

import dev.deploy4j.Deploy4j;
import picocli.CommandLine;

@CommandLine.Command(
  name = "deploy4j",
  description = "Deploy web apps anywhere. From bare metal to cloud VMs.",
  mixinStandardHelpOptions = true,
  subcommands = {
    MainCommand.SetupCommand.class,
    MainCommand.DeployCommand.class,
    MainCommand.VersionCommand.class,
    // subcommands
    AccessoryCommand.class,
    AppCommand.class,
    BuildCommand.class,
    EnvCommand.class,
    LockCommand.class,
    PruneCommand.class,
    RegistryCommand.class,
    ServerCommand.class,
    TraefikCommand.class
  }
)
public class MainCommand extends BaseCommand {

  @Override
  protected void execute(Cli cli) {
    CommandLine.usage(this, System.out);
  }

  @CommandLine.Command(
    name = "setup",
    mixinStandardHelpOptions = false,
    description = "Setup all accessories, push the env, and deploy app to servers")
  public static class SetupCommand extends BaseCommand {

    @CommandLine.Option(names = "-P", description = "Skip image build and push", defaultValue = "false")
    private boolean skipPush;

    @Override
    protected void execute(Cli cli) {
      cli.main().setup(skipPush);
    }

  }

  @CommandLine.Command(
    name = "deploy",
    mixinStandardHelpOptions = false,
    description = "Deploy app to servers")
  public static class DeployCommand extends BaseCommand {

    @Override
    protected void execute(Cli cli) {
      cli.main().deploy();
    }

  }

  @CommandLine.Command(
    name = "version",
    mixinStandardHelpOptions = false,
    description = "Show Deploy4j version")
  public static class VersionCommand extends BaseCommand {

    @Override
    protected void execute(Cli cli) {
      System.out.println( Deploy4j.VERSION );
    }

  }

}
