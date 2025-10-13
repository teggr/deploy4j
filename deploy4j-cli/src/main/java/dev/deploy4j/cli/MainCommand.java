package dev.deploy4j.cli;

import picocli.CommandLine;

@CommandLine.Command(
  name = "deploy4j",
  description = "Deploy web apps anywhere. From bare metal to cloud VMs.",
  mixinStandardHelpOptions = true,
  subcommands = {
    MainCommand.SetupCommand.class,
    MainCommand.DeployCommand.class,
    MainCommand.RedeployCommand.class,
    MainCommand.RollbackCommand.class,
    MainCommand.DetailsCommand.class,
    MainCommand.AuditCommand.class,
    MainCommand.ConfigCommand.class,
    MainCommand.InitCommand.class,
    MainCommand.RemoveCommand.class,
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
    description = "Setup all accessories, push the env, and deploy app to servers")
  public static class SetupCommand extends BaseCommand {

    @CommandLine.Option(names = "-P", description = "Skip image build and push", defaultValue = "false")
    private boolean skipPush;

    @Override
    protected void execute(Cli cli) {

      printRuntime(() -> {

        cli.main().setup(skipPush);

      });

    }

  }

  @CommandLine.Command(
    name = "deploy",
    description = "Deploy app to servers")
  public static class DeployCommand extends BaseCommand {

    @CommandLine.Option(names = "-P", description = "Skip image build and push", defaultValue = "false")
    private boolean skipPush;

    @Override
    protected void execute(Cli cli) {

      printRuntime(() -> {

        cli.main().deploy(skipPush);

      });

    }

  }

  @CommandLine.Command(
    name = "redeploy",
    description = "Deploy app to servers without bootstrapping servers, starting Traefik, pruning, and registry login")
  public static class RedeployCommand extends BaseCommand {

    @CommandLine.Option(names = "-P", description = "Skip image build and push", defaultValue = "false")
    private boolean skipPush;

    @Override
    protected void execute(Cli cli) {

      printRuntime(() -> {

        cli.main().redeploy(skipPush);

      });

    }

  }

  @CommandLine.Command(
    name = "rollback",
    description = "Rollback app to VERSION")
  public static class RollbackCommand extends BaseCommand {

    @Override
    protected void execute(Cli cli) {

      printRuntime(() -> {

        cli.main().rollback(version);

      });

    }

  }

  @CommandLine.Command(
    name = "details",
    description = "Show details about all containers")
  public static class DetailsCommand extends BaseCommand {

    @Override
    protected void execute(Cli cli) {
      cli.main().details();
    }

  }

  @CommandLine.Command(
    name = "audit",
    description = "Show audit log from servers")
  public static class AuditCommand extends BaseCommand {

    @Override
    protected void execute(Cli cli) {
      cli.main().audit();
    }

  }

  @CommandLine.Command(
    name = "config",
    description = "Show combined config (including secrets!)")
  public static class ConfigCommand extends BaseCommand {

    @Override
    protected void execute(Cli cli) {
      cli.main().config();
    }

  }

  @CommandLine.Command(
    name = "init",
    description = "Create config stub in config/deploy.yml and env stub in .env")
  public static class InitCommand extends BaseCommand {

    @CommandLine.Option(names = "--bundle", description = "Add Deploy4j to the maven file", defaultValue = "false")
    private boolean bundle;

    @Override
    protected void execute(Cli cli) {
      cli.main().init(bundle);
    }

  }

  @CommandLine.Command(
    name = "remove",
    description = "Remove Traefik, app, accessories, and registry session from servers")
  public static class RemoveCommand extends BaseCommand {

    @Override
    protected void execute(Cli cli) {
      cli.main().remove();
    }

  }

  @CommandLine.Command(
    name = "version",
    description = "Show Deploy4j version")
  public static class VersionCommand extends BaseCommand {

    @Override
    protected void execute(Cli cli) {
      cli.main().version();
    }

  }

}
