package dev.deploy4j;


import dev.deploy4j.cli.MainCliCommand;
import picocli.CommandLine;

public class Deploy4jCliApplication {

  public static void main(String[] args) {

    MainCliCommand app = new MainCliCommand();

    int exitCode = new CommandLine(app)
      .execute(args);
    System.exit(exitCode);

  }

}
