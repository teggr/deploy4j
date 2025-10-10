package dev.deploy4j.cli;


import picocli.CommandLine;

public class Deploy4jCliApplication {

  public static void main(String[] args) {

    int exitCode = new CommandLine(new Deploy4jApplicationCommand()).execute(args);
    System.exit(exitCode);

  }

}
