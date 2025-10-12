package dev.deploy4j.cli;

import dev.deploy4j.Commander;
import dev.deploy4j.configuration.Configuration;
import dev.deploy4j.raw.Deploy4jConfig;
import dev.deploy4j.raw.Deploy4jConfigReader;
import io.github.cdimascio.dotenv.Dotenv;
import picocli.CommandLine;

@CommandLine.Command(
  name = "accessory",
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
  public static class BootCommand extends Deploy4jCommand {

    @CommandLine.Parameters(index = "0", description = "Accessory name", defaultValue = "all", paramLabel = "NAME")
    private String name;

    @CommandLine.Option(names = "login", defaultValue = "true")
    private boolean login;

    @Override
    public Integer call() throws Exception {

      Deploy4jConfig deploy4jConfig = Deploy4jConfigReader.readYaml(configFile);

      Configuration config = new Configuration(
        deploy4jConfig,
        destination,
        version
      );

      Dotenv dotenv = Dotenv.configure()
        .load();

      try ( Commander commander = new Commander(config) ) {

//    commander.setVerbosity();
//    commander.configure( configFile, destination, version );
//    commander.specificHosts();
//    commander.specificRoles();
//    commander.specificPrimary();

        Cli cli = new Cli(commander);

        Accessory accesssory = cli.accesssory();

        accesssory.boot(name, login);

      } catch (Exception e) {

        throw new RuntimeException(e);

      }

      return 0;

    }

  }

}
