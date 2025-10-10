package dev.deploy4j.cli;

import picocli.CommandLine;

import java.util.concurrent.Callable;

@CommandLine.Command(
  name = "deploy4j",
  mixinStandardHelpOptions = true,
  subcommands = {
    Setup.class
  }
)
public class Deploy4jApplicationCommand implements Callable<Integer> {

  @Override
  public Integer call() throws Exception {
    CommandLine.usage(this, System.out);
    return 0;
  }

}
