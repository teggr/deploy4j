package dev.deploy4j.cli;

import dev.deploy4j.Commander;
import dev.deploy4j.configuration.Configuration;
import dev.deploy4j.env.ENV;
import dev.deploy4j.raw.Deploy4jConfig;
import dev.deploy4j.raw.Deploy4jConfigReader;
import io.github.cdimascio.dotenv.Dotenv;
import picocli.CommandLine;

import java.util.concurrent.Callable;

public abstract class BaseCommand implements Callable<Integer> {

  @CommandLine.Option(names = {"-v", "--verbose"}, description = "Detailed logging", negatable = true)
  boolean verbose;
  @CommandLine.Option(names = {"-q", "--quiet"}, description = "Minimal logging", negatable = true)
  boolean quiet;
  @CommandLine.Option(names = {"--version"}, paramLabel = "VERSION", description = "Run commands against a specific app version")
  String version;
  @CommandLine.Option(names = {"-p", "--primary"}, description = "Run commands only on primary host instead of all", negatable = true)
  boolean primary;
  @CommandLine.Option(names = {"-h", "--hosts"}, paramLabel = "HOSTS", description = "Run commands on these hosts instead of all (separate by comma, supports wildcards with *)")
  String[] hosts;
  @CommandLine.Option(names = {"-r", "--roles"}, paramLabel = "ROLES", description = "Run commands on these roles instead of all (separate by comma, supports wildcards with *)")
  String[] roles;
  @CommandLine.Option(names = {"-c", "--config-file"}, paramLabel = "CONFIG_FILE", description = "Path to config file. Default: config/deploy.yml", defaultValue = "config/deploy.yml" )
  String configFile;
  @CommandLine.Option(names = {"-d", "--destination"}, paramLabel = "DESTINATION", description = "Specify destination to be used for config file (staging -> deploy.staging.yml)")
  String destination;
  @CommandLine.Option(names = {"-H", "--skip-hooks"}, description = "Don't run hooks, Default: false")
  boolean skipHooks;

  /**
   * This ensures the init method is called after the command line is parsed (so all options and positional parameters are assigned) but before the user-specified subcommand is executed.
   */
  public int executionStrategy(CommandLine.ParseResult parseResult) {
    initialize();
    return new CommandLine.RunLast().execute(parseResult);
  }

  public void initialize() {
    loadEnv();
    initializeCommander();
  }

  private void initializeCommander() {
    // delegated to the call() method
  }

  private void loadEnv() {

    if( destination != null ) {
      ENV.overload(".env", ".env." + destination);
    } else {
      ENV.overload(".env");
    }

  }

  @Override
  public Integer call() throws Exception {

    try {

      Deploy4jConfig deploy4jConfig = Deploy4jConfigReader.readYaml(configFile);

      Configuration config = new Configuration(
        deploy4jConfig,
        destination,
        version
      );

      try (Commander commander = new Commander(config)) {

//    commander.setVerbosity();
//    commander.configure( configFile, destination, version );
//    commander.specificHosts();
//    commander.specificRoles();
//    commander.specificPrimary();

        Cli cli = new Cli(commander);

        execute(cli);

      } catch (Exception e) {

        throw new RuntimeException(e);

      }

      return 0;

    } catch (Exception e) {
      throw new RuntimeException(e);
    }

  }

  protected abstract void execute(Cli cli);

}
