package dev.deploy4j.deploy.host.commands;

import dev.deploy4j.deploy.configuration.Configuration;
import dev.rebelcraft.cmd.Cmd;
import dev.rebelcraft.cmd.Cmds;

import java.util.List;
import java.util.stream.Stream;

import static dev.rebelcraft.cmd.Cmds.*;
import static dev.rebelcraft.cmd.Cmds.pipe;
import static dev.rebelcraft.cmd.pkgs.Docker.docker;

public class PruneHostCommands extends BaseHostCommands {

  public PruneHostCommands(Configuration config) {
    super(config);
  }

  public Cmd danglingImages() {
    return docker().image()
      .args("prune", "--force", "--filter", "label=service=" + config().service())
      .description("dangling images");
  }

  public Cmd taggedImages() {
    return pipe(
      docker().image().args("ls")
        .args(serviceFilter())
        .args("--format", "'{{.ID}} {{.Repository}}:{{.Tag}}'")
        .description("tagged images"),
      Cmd.cmd("grep", "-v", "-w")
        .args("\"" + activeImageList() + "\""),
      Cmd.cmd("while read image tag; do docker rmi $tag; done")
    );
  }

  public Cmd appContainers(int retain) {
    return pipe(
      docker().ps().args("-q", "-a")
        .args(serviceFilter())
        .args(stoppedContainersFilters()),
      Cmd.cmd("tail", "-n", "+" + (retain + 1)),
      Cmd.cmd("while read container_id; do docker rm $container_id; done")
    ).description("app containers");
  }

  public Cmd healthcheckContainers() {
    return docker().container()
      .args("prune", "--force")
      .args(healthCheckServiceFilter())
      .description("healthcheck containers");
  }

  // private

  private List<String> stoppedContainersFilters() {
    return Stream.of(
      "created", "exited", "dead"
    ).flatMap(
      status -> Stream.of("--filter", "status=" + status)
    ).toList();
  }

  private String activeImageList() {
    // Pull the images that are used by any containers
    // Append repo:latest - to avoid deleting the latest tag
    // Append repo:<none> - to avoid deleting dangling images that are in use. Unused dangling images are deleted separately
    return "$(docker container ls -a --format '{{.Image}}\\|' --filter label=service=" + config.service() + " | tr -d '\\n')" + config.latestImage() + "\\|" + config.repository() + ":<none>";
  }

  private String[] serviceFilter() {
    return new String[]{
      "--filter", "label=service=" + config().service()
    };
  }

  private String[] healthCheckServiceFilter() {
    return new String[]{
      "--filter", "label=service=" + config().healthcheckService()
    };
  }

}
