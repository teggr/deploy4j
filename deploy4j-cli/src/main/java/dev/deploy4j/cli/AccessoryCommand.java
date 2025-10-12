package dev.deploy4j.cli;

import picocli.CommandLine;

@CommandLine.Command(
  name = "accessory",
  description = "Manage accessories (db/redis/search)",
  mixinStandardHelpOptions = false,
  subcommands = {
    AccessoryCommand.BootCommand.class
  })
public class AccessoryCommand {

  @CommandLine.Command(
    name = "boot",
    mixinStandardHelpOptions = true,
    description = "Boot new accessory service on host (use NAME=all to boot all accessories)"
  )
  public static class BootCommand extends BaseCommand {

    @CommandLine.Parameters(index = "0", description = "Accessory name", defaultValue = "all", paramLabel = "NAME")
    private String name;

    @CommandLine.Option(names = "login", defaultValue = "true")
    private boolean login;

    @Override
    protected void execute(Cli cli) {
      cli.accesssory().boot(name, login);
    }

  }

}
