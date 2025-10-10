package dev.deploy4j.cli;

import picocli.CommandLine;

import java.util.concurrent.Callable;

public abstract class Deploy4jCommand implements Callable<Integer> {

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


}
