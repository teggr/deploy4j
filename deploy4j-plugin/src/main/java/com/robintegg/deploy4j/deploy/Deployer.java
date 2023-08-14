package com.robintegg.deploy4j.deploy;

import com.robintegg.deploy4j.ssh.SshConnection;
import com.robintegg.deploy4j.ssh.SshConnectionFactory;
import lombok.SneakyThrows;

import java.nio.file.Path;
import java.util.List;

public class Deployer {

  @SneakyThrows
  public static void deploy(List<Path> files, SshConnectionFactory sshConnectionFactory) {

    // what's in a deployment?

    // connect
    try (SshConnection sshConnection = sshConnectionFactory.open()) {

      // install docker
      Docker.install(sshConnection);

      // start traefik
      Traefik.startTraefik(sshConnection);

      // push files
      UploadedFiles uploadedFiles = Uploader.uploadFiles(sshConnection, files);

      // build image
      // DockerImage dockerImage = dockerClient.build(uploadedFiles);

      // start container
      // dockerClient.run(dockerImage);

    }

  }

}
