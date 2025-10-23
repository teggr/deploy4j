package dev.deploy4j.cli;

import dev.deploy4j.deploy.DeployApplicationContext;
import picocli.CommandLine;

@CommandLine.Command(
  name = "deploy4j",
  description = "Deploy web apps anywhere. From bare metal to cloud VMs.",
  mixinStandardHelpOptions = true,
  subcommands = {
    MainCliCommand.SetupCliCommand.class,
    MainCliCommand.DeployCliCommand.class,
    MainCliCommand.RedeployCliCommand.class,
    MainCliCommand.RollbackCliCommand.class,
    MainCliCommand.DetailsCliCommand.class,
    MainCliCommand.AuditCliCommand.class,
    MainCliCommand.ConfigCliCommand.class,
    MainCliCommand.InitCliCommand.class,
    MainCliCommand.EnvifyCliCommand.class,
    MainCliCommand.RemoveCliCommand.class,
    MainCliCommand.VersionCliCommand.class,
    // subcommands
    AccessoryCliCommand.class,
    AppCliCommand.class,
    BuildCliCommand.class,
    EnvCliCommand.class,
    LockCliCommand.class,
    PruneCliCommand.class,
    RegistryCliCommand.class,
    ServerCliCommand.class,
    TraefikCliCommand.class
  }
)
public class MainCliCommand extends BaseCliCommand {

  @Override
  protected void execute(DeployApplicationContext deployApplicationContext) {
    CommandLine.usage(this, System.out);
  }

  @CommandLine.Command(
    name = "setup",
    description = "Setup all accessories, push the env, and deploy app to servers")
  public static class SetupCliCommand extends BaseCliCommand {

    @CommandLine.Option(names = "-P", description = "Skip image build and push", defaultValue = "false")
    private boolean skipPush;

    @Override
    protected void execute(DeployApplicationContext deployApplicationContext) {

      printRuntime(() -> {

        deployApplicationContext.lockManager().withLock( deployApplicationContext.commander(), () -> {

          deployApplicationContext.main().setup(deployApplicationContext.commander(), skipPush);

        });

      });

    }

  }

  @CommandLine.Command(
    name = "deploy",
    description = "Deploy app to servers")
  public static class DeployCliCommand extends BaseCliCommand {

    @CommandLine.Option(names = "-P", description = "Skip image build and push", defaultValue = "false")
    private boolean skipPush;

    @Override
    protected void execute(DeployApplicationContext deployApplicationContext) {

      printRuntime(() -> {

        deployApplicationContext.main().deploy(deployApplicationContext.commander(), skipPush);

      });

    }

  }

  @CommandLine.Command(
    name = "redeploy",
    description = "Deploy app to servers without bootstrapping servers, starting Traefik, pruning, and registry login")
  public static class RedeployCliCommand extends BaseCliCommand {

    @CommandLine.Option(names = "-P", description = "Skip image build and push", defaultValue = "false")
    private boolean skipPush;

    @Override
    protected void execute(DeployApplicationContext deployApplicationContext) {

      printRuntime(() -> {

        deployApplicationContext.main().redeploy(deployApplicationContext.commander(), skipPush);

      });

    }

  }

  @CommandLine.Command(
    name = "rollback",
    description = "Rollback app to VERSION")
  public static class RollbackCliCommand extends BaseCliCommand {

    @Override
    protected void execute(DeployApplicationContext deployApplicationContext) {

      printRuntime(() -> {

        deployApplicationContext.main().rollback(deployApplicationContext.commander(), version);

      });

    }

  }

  @CommandLine.Command(
    name = "details",
    description = "Show details about all containers")
  public static class DetailsCliCommand extends BaseCliCommand {

    @Override
    protected void execute(DeployApplicationContext deployApplicationContext) {
      deployApplicationContext.main().details(deployApplicationContext.commander());
    }

  }

  @CommandLine.Command(
    name = "audit",
    description = "Show audit log from servers")
  public static class AuditCliCommand extends BaseCliCommand {

    @Override
    protected void execute(DeployApplicationContext deployApplicationContext) {
      deployApplicationContext.main().audit(deployApplicationContext.commander());
    }

  }

  @CommandLine.Command(
    name = "config",
    description = "Show combined config (including secrets!)")
  public static class ConfigCliCommand extends BaseCliCommand {

    @Override
    protected void execute(DeployApplicationContext deployApplicationContext) {
      deployApplicationContext.main().config(deployApplicationContext.commander());
    }

  }

  @CommandLine.Command(
    name = "init",
    description = "Create config stub in config/deploy.yml and env stub in .env")
  public static class InitCliCommand extends BaseCliCommand {

    @CommandLine.Option(names = "--bundle", description = "Add Deploy4j to the maven file", defaultValue = "false")
    private boolean bundle;

    @Override
    protected void execute(DeployApplicationContext deployApplicationContext) {
      deployApplicationContext.main().init(bundle);
    }

  }

  @CommandLine.Command(
    name = "envify",
    description = "Create .env by evaluating .env.thyme (or .env.staging.thyme -> .env.staging when using -d staging)")
  public static class EnvifyCliCommand extends BaseCliCommand {

    @CommandLine.Option(names = "-P", description = "Skip .env file push", defaultValue = "false")
    private boolean skipPush;

    @Override
    protected void execute(DeployApplicationContext deployApplicationContext) {
      deployApplicationContext.main().envify(deployApplicationContext.commander(), skipPush, destination);
    }

  }

  @CommandLine.Command(
    name = "remove",
    description = "Remove Traefik, app, accessories, and registry session from servers")
  public static class RemoveCliCommand extends BaseCliCommand {

    @Override
    protected void execute(DeployApplicationContext deployApplicationContext) {
      deployApplicationContext.main().remove(deployApplicationContext.commander());
    }

  }

  @CommandLine.Command(
    name = "version",
    description = "Show Deploy4j version")
  public static class VersionCliCommand extends BaseCliCommand {

    @Override
    protected void execute(DeployApplicationContext deployApplicationContext) {
      deployApplicationContext.main().version();
    }

  }

}
