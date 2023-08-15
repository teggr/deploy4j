package com.robintegg.deploy4j.deploy;

import com.robintegg.deploy4j.ssh.SshCommandResult;
import com.robintegg.deploy4j.ssh.SshConnection;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public class Docker {

  public static void install(SshConnection sshConnection) {

    SshCommandResult versionResult = sshConnection.executeCommand("docker -v");

    if (versionResult.status() != 0) {

      // unable to get version indicates that it's not installed
      log.info("docker not installed: {}", versionResult.err());

      SshCommandResult installResult = sshConnection.executeCommand("curl -fsSL https://get.docker.com | sh");

      if (installResult.status() != 0) {
        log.info("could not install docker: {}", installResult.err());
        throw new RuntimeException("could not installed docker");
      } else {
        log.info("docker installed: {}", installResult.out());
      }

    } else {

      log.info("docker already installed: {}", versionResult.out());

    }

  }

  public static void build(SshConnection sshConnection, String buildDirectory, String latestImage, String versionedImage) {

    SshCommandResult buildResult = sshConnection.executeCommand("docker build -t " + latestImage + " -t " + versionedImage + " --file " + buildDirectory + "/Dockerfile " + buildDirectory);
    if (buildResult.status() != 0) {
      log.info("could not build docker image: {}", buildResult.err());
      throw new RuntimeException("could not build docker image");
    } else {
      log.info("docker image built: {}", buildResult.out());
    }

  }

  public static void startOrRun(SshConnection sshConnection, String image, String containerName, String route) {

    SshCommandResult runResult = sshConnection.executeCommand(
        "docker start " + containerName +
            " || docker run --detach --restart unless-stopped --name " + containerName +
            " -e SERVER_SERVLET_CONTEXT_PATH=/" + route + " " +
            "--label traefik.http.routers." + route + ".rule=PathPrefix\\(\\`/" + route + "\\`\\) " + image);
    if (runResult.status() != 0) {
      log.info("could not run docker image: {}", runResult.err());
      throw new RuntimeException("could not run docker image");
    } else {
      log.info("docker image run: {}", runResult.out());
    }

  }

  public static List<String> getRunningContainerNamesByName(SshConnection sshConnection, String namePrefix) {

    SshCommandResult runResult = sshConnection.executeCommand(
        "docker ps --filter status=running --filter status=restarting --filter name=" + namePrefix + " --format '{{.Names}}'");
    if (runResult.status() != 0) {
      log.info("could not get running container id: {}", runResult.err());
      throw new RuntimeException("could not get running container id");
    } else {
      log.info("running container ids: {}", runResult.out());
    }

    if (runResult.out().isBlank()) {
      return List.of();
    } else {
      return Stream.of(runResult.out().split("\n")).collect(Collectors.toList());
    }

  }

  public static void renameContainer(SshConnection sshConnection, String containerName, String newContainerName) {

    SshCommandResult runResult = sshConnection.executeCommand(
        "docker rename " + containerName + " " + newContainerName);
    if (runResult.status() != 0) {
      log.info("could not rename container: {}", runResult.err());
      throw new RuntimeException("could not rename container");
    } else {
      log.info("container renamed: {}", runResult.out());
    }

  }

  public static void stop(SshConnection sshConnection, String containerName) {
    SshCommandResult runResult = sshConnection.executeCommand(
        "docker stop " + containerName);
    if (runResult.status() != 0) {
      log.info("could not stop container: {}", runResult.err());
      throw new RuntimeException("could not stop container");
    } else {
      log.info("container stopped: {}", runResult.out());
    }
  }

  public static void stopAll(SshConnection sshConnection, List<String> containerNames) {
    containerNames.forEach(name -> Docker.stop(sshConnection, name));
  }

  public static void removeAllStoppedContainers(SshConnection sshConnection, String namePrefix) {


    SshCommandResult runResult = sshConnection.executeCommand(
        "docker ps -q -a --filter name=" + namePrefix + " --filter status=created --filter status=exited --filter status=dead " +
            "| tail -n +2 " +
            "| while read container_id; do docker rm $container_id; done");
    if (runResult.status() != 0) {
      log.info("could not remove containers: {}", runResult.err());
      throw new RuntimeException("could not stop container");
    } else {
      log.info("containers removed: {}", runResult.out());
    }

  }
}
