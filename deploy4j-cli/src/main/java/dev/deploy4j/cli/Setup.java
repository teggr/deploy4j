package dev.deploy4j.cli;

import dev.deploy4j.Commander;
import dev.deploy4j.configuration.Configuration;
import dev.deploy4j.raw.Deploy4jConfig;
import dev.deploy4j.raw.Deploy4jConfigReader;
import picocli.CommandLine;

import java.util.concurrent.Callable;

@CommandLine.Command(
  name = "setup",
  mixinStandardHelpOptions = false,
  description = "Setup deploy4j on the target hosts")
public class Setup extends Deploy4jCommand implements Callable<Integer> {

  @Override
  public Integer call() throws Exception {

    Deploy4jConfig deploy4jConfig = Deploy4jConfigReader.readYaml(configFile);

    Configuration config = new Configuration(
      deploy4jConfig,
      destination,
      version
    );

    // TODO: CLI OPTIONS TO INFLUENCE THESE
    try ( Commander commander = new Commander(config) ) {

//    commander.setVerbosity();
//    commander.configure( configFile, destination, version );
//    commander.specificHosts();
//    commander.specificRoles();
//    commander.specificPrimary();

      Cli cli = new Cli(commander);

      Main main = cli.main();

      main.setup();

    } catch (Exception e) {

      throw new RuntimeException(e);

    }

    return 0;

  }

}
