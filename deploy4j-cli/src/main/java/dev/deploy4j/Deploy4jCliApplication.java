package dev.deploy4j;


import dev.deploy4j.cli.MainCommand;
import picocli.CommandLine;

public class Deploy4jCliApplication {

  public static void main(String[] args) {

    MainCommand app = new MainCommand();

    int exitCode = new CommandLine(app)
      .setExecutionStrategy(app::executionStrategy)
      .execute(args);
    System.exit(exitCode);

  }

}
