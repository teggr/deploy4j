package dev.deploy4j.cli;

import dev.deploy4j.deploy.Version;
import picocli.CommandLine;

import java.util.concurrent.Callable;

@CommandLine.Command(
  name = "version",
  description = "Show Deploy4j version")
public class VersionCliCommand implements Callable<Integer> {

  @CommandLine.Mixin
  private HelpOptions helpOptions = new HelpOptions();

  @Override
  public Integer call() throws Exception {
    Version version = new Version();
    version.version();
    return 0;
  }

}
