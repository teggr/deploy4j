package dev.deploy4j.cli;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import dev.deploy4j.deploy.DeployApplicationContext;
import dev.deploy4j.deploy.DeployContext;
import dev.deploy4j.deploy.Environment;
import dev.deploy4j.deploy.configuration.Configuration;
import dev.deploy4j.deploy.host.ssh.SshHosts;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;

import java.util.concurrent.Callable;

public abstract class BaseCliCommand implements Callable<Integer> {

  @CommandLine.Mixin
  private HelpOptions helpOptions = new HelpOptions();

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

    // configure Logback root logger level based on CLI flags
    Logger root = (Logger) LoggerFactory.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME);
    if (( quiet != null && quiet) || (verbose != null && !verbose)) {
      root.setLevel(Level.ERROR);
    } else if ((verbose != null && verbose) || ( quiet != null && !quiet)) {
      root.setLevel(Level.DEBUG);
    } else {
      root.setLevel(Level.INFO);
    }

    Configuration configuration = Configuration.createFrom(configFile, destination, version);

    DeployContext deployContext = new DeployContext(configuration, hosts, roles, primary);

    try (SshHosts sshHosts = new SshHosts(deployContext.config())) {

      DeployApplicationContext deployApplicationContext = new DeployApplicationContext(environment, sshHosts, deployContext);

      execute(deployApplicationContext);

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

  protected abstract void execute(DeployApplicationContext deployApplicationContext);


}
