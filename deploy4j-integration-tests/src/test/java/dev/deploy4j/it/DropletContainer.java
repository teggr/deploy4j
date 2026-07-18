package dev.deploy4j.it;

import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.MountableFile;
import org.testcontainers.containers.wait.strategy.Wait;

import java.net.URI;
import java.nio.file.Path;
import java.time.Duration;

final class DropletContainer extends GenericContainer<DropletContainer> {

  private static final int SSH_PORT = 22;
  private static final int HTTP_PORT = 80;

  DropletContainer(Path authorizedKeysPath) {
    super("teggr/deploy4j-docker-droplet:latest");
    withCopyFileToContainer(MountableFile.forHostPath(authorizedKeysPath), "/tmp/authorized_keys");
    withFileSystemBind("/var/run/docker.sock", "/var/run/docker.sock", BindMode.READ_WRITE);
    withExposedPorts(SSH_PORT, HTTP_PORT);
    withEnv("DOCKER_TLS_CERTDIR", "");
    withEnv("DOCKERD_ARGS", "--mtu=1400");
    withCreateContainerCmdModifier(command -> {
      command.withPrivileged(true);
      command.getHostConfig().withDns("8.8.8.8", "1.1.1.1");
    });
    waitingFor(Wait.forListeningPort().withStartupTimeout(Duration.ofMinutes(2)));
  }

  int sshPort() {
    return getMappedPort(SSH_PORT);
  }

  URI actuatorUri() {
    return URI.create("http://%s:%d/actuator/health".formatted(getHost(), getMappedPort(HTTP_PORT)));
  }
}
