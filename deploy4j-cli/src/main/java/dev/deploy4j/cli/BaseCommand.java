package dev.deploy4j.cli;

import dev.deploy4j.Commander;
import dev.deploy4j.Environment;
import picocli.CommandLine;

import java.util.concurrent.Callable;

public abstract class BaseCommand implements Callable<Integer> {

  @CommandLine.Option(names = {"-v", "--verbose"}, description = "Detailed logging", negatable = true)
  Boolean verbose;
  @CommandLine.Option(names = {"-q", "--quiet"}, description = "Minimal logging", negatable = true)
  Boolean quiet;
  @CommandLine.Option(names = {"--version"}, paramLabel = "VERSION", description = "Run commands against a specific app version")
  String version;
  @CommandLine.Option(names = {"-p", "--primary"}, description = "Run commands only on primary host instead of all", negatable = true)
  Boolean primary;
  @CommandLine.Option(names = {"-h", "--hosts"}, paramLabel = "HOSTS", description = "Run commands on these hosts instead of all (separate by comma, supports wildcards with *)")
  String[] hosts;
  @CommandLine.Option(names = {"-r", "--roles"}, paramLabel = "ROLES", description = "Run commands on these roles instead of all (separate by comma, supports wildcards with *)")
  String[] roles;
  @CommandLine.Option(names = {"-c", "--config-file"}, paramLabel = "CONFIG_FILE", description = "Path to config file. Default: config/deploy.yml", defaultValue = "config/deploy.yml")
  String configFile;
  @CommandLine.Option(names = {"-d", "--destination"}, paramLabel = "DESTINATION", description = "Specify destination to be used for config file (staging -> deploy.staging.yml)")
  String destination;
  @CommandLine.Option(names = {"-H", "--skip-hooks"}, description = "Don't run hooks, Default: false")
  Boolean skipHooks;

  @Override
  public Integer call() throws Exception {

    Environment environment = new Environment(destination);

    try (Commander commander = new Commander()) {

      // local logging
      if (quiet) {
        System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "error");
      } else if (verbose) {
        System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "debug");
      } else {
        System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "info");
      }

      commander.configure(configFile, destination, version);
      commander.specificHosts(hosts);
      commander.specificRoles(roles);
      if (primary != null) commander.specificPrimary(primary);

      Cli cli = new Cli(environment, commander);

      execute(cli);

    } catch (Exception e) {

      throw new RuntimeException(e);

    }

    return 0;

  }

  protected void printRuntime(Runnable function) {

    long start = System.currentTimeMillis();

    try {

      function.run();

    } catch (Exception e) {
      throw new RuntimeException(e);
    } finally {

      long end = System.currentTimeMillis();

      System.out.println("=================================");
      System.out.println("Finished all in  in " + (end - start) / 1000 + " seconds");

    }

  }

  protected abstract void execute(Cli cli);


}
