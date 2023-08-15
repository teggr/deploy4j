package com.robintegg.deploy4j.deploy;

import com.robintegg.deploy4j.ssh.SshConnection;
import com.robintegg.deploy4j.ssh.SshConnectionFactory;
import lombok.SneakyThrows;

public class Deployer {

  @SneakyThrows
  public static void deploy(BuildFiles buildFiles, SshConnectionFactory sshConnectionFactory) {

    // what's in a deployment?

    // connect
    try (SshConnection sshConnection = sshConnectionFactory.open()) {

      // install docker
      Docker.install(sshConnection);

      // start traefik
      Traefik.startTraefik(sshConnection);

      // push files
      // map of paths to upload locations
      String uploadDirectory = Uploader.uploadFiles(sshConnection, buildFiles);

      // build image
      String image = "my-app:latest";
      Docker.build(sshConnection, uploadDirectory, image);

      // start or run container
      String name = "my-app";
      Docker.startOrRun(sshConnection, image, name);

    }

  }

}
