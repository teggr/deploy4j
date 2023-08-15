package com.robintegg.deploy4j.deploy;

import com.robintegg.deploy4j.ssh.SshCommandResult;
import com.robintegg.deploy4j.ssh.SshConnection;
import lombok.extern.slf4j.Slf4j;

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

  public static void build(SshConnection sshConnection, String buildDirectory, String image) {

    SshCommandResult buildResult = sshConnection.executeCommand("docker build -t " + image + " --file " + buildDirectory + "/Dockerfile " + buildDirectory);
    if (buildResult.status() != 0) {
      log.info("could not build docker image: {}", buildResult.err());
      throw new RuntimeException("could not build docker image");
    } else {
      log.info("docker image built: {}", buildResult.out());
    }

  }

  public static void startOrRun(SshConnection sshConnection, String image, String name) {

    SshCommandResult runResult = sshConnection.executeCommand(
        "docker start " + name +
            " || docker run --detach --restart unless-stopped --name " + name + " -e SERVER_SERVLET_CONTEXT_PATH=/my-app --label traefik.http.routers.my-app.rule=PathPrefix\\(\\`/my-app\\`\\) " + image);
    if (runResult.status() != 0) {
      log.info("could not run docker image: {}", runResult.err());
      throw new RuntimeException("could not run docker image");
    } else {
      log.info("docker image run: {}", runResult.out());
    }

  }

}
