package dev.deploy4j.cli;

import dev.deploy4j.Commander;
import dev.deploy4j.configuration.Configuration;
import dev.deploy4j.raw.Deploy4jConfig;
import dev.deploy4j.raw.Deploy4jConfigReader;
import io.github.cdimascio.dotenv.Dotenv;
import picocli.CommandLine;

import java.util.concurrent.Callable;

@CommandLine.Command(
  name = "setup",
  mixinStandardHelpOptions = false,
  description = "Setup all accessories, push the env, and deploy app to servers")
public class SetupCommand extends Deploy4jCommand implements Callable<Integer> {

  @CommandLine.Option(names = "-P", description = "Skip image build and push", defaultValue = "false")
  private boolean skipPush;

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

      Main main = cli.main();

      main.setup(skipPush);

    } catch (Exception e) {

      throw new RuntimeException(e);

    }

    return 0;

  }

}
