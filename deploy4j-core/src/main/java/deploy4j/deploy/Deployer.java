package deploy4j.deploy;

import deploy4j.ssh.SshConnection;
import deploy4j.ssh.SshConnectionFactory;
import lombok.SneakyThrows;

import java.security.SecureRandom;
import java.util.List;

public class Deployer {

  @SneakyThrows
  public static void deploy(Deployable deployable, SshConnectionFactory sshConnectionFactory) {

    // what's in a deployment?

    // connect
    try (SshConnection sshConnection = sshConnectionFactory.open()) {

      // install docker
      Docker.install(sshConnection);

      // TODO: load other images (postgresql) + gather urls etc.

      // start traefik
      // TODO: enable/disable dashboard --insecure option
      String traefikRoute = deployable.serviceName();
      Traefik.startTraefik(sshConnection);

      // push files
      // map of paths to upload locations
      String uploadDirectory = Uploader.uploadFiles(sshConnection, deployable);

      // docker related vars
      String imagePrefix = deployable.serviceName();
      String latestImage = imagePrefix + ":latest";
      String versionedImage = imagePrefix + ":" + deployable.version();
      String containerNamePrefix = deployable.serviceName();
      String containerName = containerNamePrefix + "-" + deployable.version();

      // build image
      Docker.build(sshConnection, uploadDirectory, latestImage, versionedImage);

      // rename existing versions
      List<String> renamedExistingContainerNames = Docker.getRunningContainerNamesByName(sshConnection, containerNamePrefix).stream()
          .map(name -> {
            String tmpContainerName = name + "_replaced_" + Integer.toHexString(new SecureRandom().nextInt());
            Docker.renameContainer(sshConnection, name, tmpContainerName);
            return tmpContainerName;
          })
          .toList();

      // start or run container
      Docker.startOrRun(sshConnection, versionedImage, containerName, traefikRoute);

      // TODO: wait for healthy...

      // stop containers
      Docker.stopAll(sshConnection, renamedExistingContainerNames);

      // prune containers
      // TODO: prune images
      Docker.removeAllStoppedContainers(sshConnection, containerNamePrefix);

    }

  }

}
