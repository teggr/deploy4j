package dev.deploy4j.cli;

import picocli.CommandLine;

@CommandLine.Command(
  name = "prune",
  description = "Prune old application images and containers"
)
public class PruneCommand {
}
