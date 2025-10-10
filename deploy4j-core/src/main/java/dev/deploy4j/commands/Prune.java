package dev.deploy4j.commands;

import dev.deploy4j.Cmd;
import dev.deploy4j.configuration.Configuration;

import java.util.List;
import java.util.stream.Stream;

import static dev.deploy4j.Commands.pipe;

public class Prune {
  private final Configuration config;

  public Prune(Configuration config) {
    this.config = config;
  }

  public Cmd appContainers(int retain) {
    return pipe(
      Cmd.cmd("docker", "ps", "-q", "-a")
        .args(serviceFilter())
        .args(stoppedContainersFilters()),
      Cmd.cmd("tail", "-n", "+" + (retain + 1)),
      Cmd.cmd("while read container_id; do docker rm $container_id; done")
    ).description("app containers");
  }

  private List<String> stoppedContainersFilters() {
    return Stream.of(
      "created", "exited", "dead"
    ).flatMap(
      status -> Stream.of("--filter", "status=" + status)
    ).toList();
  }

  private String[] serviceFilter() {
    return new String[]{
      "--filter", "label=service=" + config.service()
    };
  }

  public Cmd healthcheckContainers() {
    return Cmd.cmd("docker", "container")
      .args("prune", "--force")
      .args(healthCheckServiceFilter())
      .description("healthcheck containers");
  }

  private String[] healthCheckServiceFilter() {
    return new String[]{
      "--filter", "label=service=" + config.healthcheckService()
    };
  }
}
