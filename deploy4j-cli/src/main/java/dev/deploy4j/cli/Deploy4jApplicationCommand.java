package dev.deploy4j.cli;

import picocli.CommandLine;

import java.util.concurrent.Callable;

@CommandLine.Command(
  name = "deploy4j",
  description = "Deploy web apps anywhere. From bare metal to cloud VMs.",
  subcommands = {
    AccessoryCliCommand.class,
    AppCliCommand.class,
    AuditCliCommand.class,
    BuildCliCommand.class,
    ConfigCliCommand.class,
    DeployCliCommand.class,
    DetailsCliCommand.class,
    InitCliCommand.class,
    LockCliCommand.class,
    PruneCliCommand.class,
    RedeployCliCommand.class,
    RegistryCliCommand.class,
    RemoveCliCommand.class,
    RollbackCliCommand.class,
    SecretsCliCommand.class,
    ServerCliCommand.class,
    SetupCliCommand.class,
    SpringBootCliCommand.class,
    TestCliCommand.class,
    GatewayCliCommand.class,
    VersionCliCommand.class
  }
)
public class Deploy4jApplicationCommand implements Callable<Integer> {

  @CommandLine.Mixin
  private HelpOptions helpOptions = new HelpOptions();

  @Override
  public Integer call() throws Exception {
    CommandLine.usage(this, System.out);
    return 0;
  }

  public static void main(String[] args) {

    Deploy4jApplicationCommand app = new Deploy4jApplicationCommand();

    int exitCode = new CommandLine(app)
      .execute(args);
    System.exit(exitCode);

  }

}
