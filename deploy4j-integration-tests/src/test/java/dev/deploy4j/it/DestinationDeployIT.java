package dev.deploy4j.it;

import org.awaitility.Awaitility;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.Duration;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
class DestinationDeployIT {

  private static final String LOCAL_DESTINATION = "local";
  private static final String DIGITALOCEAN_DESTINATION = "digitalocean";
  private static final SshKeyHelper.GeneratedKeyPair SSH_KEY_PAIR = createKeyPair();

  @Container
  private static final DropletContainer LOCAL_DROPLET = new DropletContainer(SSH_KEY_PAIR.authorizedKeysPath());

  @Container
  private static final DropletContainer DIGITALOCEAN_DROPLET = new DropletContainer(SSH_KEY_PAIR.authorizedKeysPath());

  @AfterAll
  static void tearDownKeys() throws Exception {
    SSH_KEY_PAIR.close();
  }

  @Test
  @DisplayName("deploys destinations to separate SSH hosts")
  void deploysDestinationsToSeparateHosts() throws Exception {
    try (DeployConfigHelper.DestinationTestDeployment deployment =
           DeployConfigHelper.createForDestinations(LOCAL_DROPLET, DIGITALOCEAN_DROPLET, SSH_KEY_PAIR.privateKeyPath())) {
      for (String destination : List.of(LOCAL_DESTINATION, DIGITALOCEAN_DESTINATION)) {
        DeployConfigHelper.CliResult bootstrap = deployment.executeCli(destination, "server", "bootstrap");
        assertThat(bootstrap.exitCode()).describedAs(bootstrap.output()).isZero();

        deployment.loadImage(destination, deployment.demoImageRef());
        DeployConfigHelper.CliResult bootAccessories =
          deployment.executeCli(destination, "accessory", "boot", "all");
        assertThat(bootAccessories.exitCode()).describedAs(bootAccessories.output()).isZero();

        deployment.updateDatabaseHost(destination, deployment.databaseIp(destination));
        DeployConfigHelper.CliResult deploy = deployment.executeCli(
          destination,
          "deploy",
          "--version",
          deployment.version(),
          "-P"
        );
        assertThat(deploy.exitCode()).describedAs(deploy.output()).isZero();
      }

      String localContainer = deployment.runningContainerName(LOCAL_DESTINATION);
      String digitalOceanContainer = deployment.runningContainerName(DIGITALOCEAN_DESTINATION);
      assertThat(localContainer).isNotBlank();
      assertThat(digitalOceanContainer).isNotBlank();
      assertThat(localContainer).isNotEqualTo(digitalOceanContainer);

      assertThat(deployment.capture(LOCAL_DESTINATION, "docker ps --format '{{.Names}}'"))
        .contains(localContainer)
        .doesNotContain(digitalOceanContainer);
      assertThat(deployment.capture(DIGITALOCEAN_DESTINATION, "docker ps --format '{{.Names}}'"))
        .contains(digitalOceanContainer)
        .doesNotContain(localContainer);

      for (String destination : List.of(LOCAL_DESTINATION, DIGITALOCEAN_DESTINATION)) {
        DeployConfigHelper.CliResult stop = deployment.executeCli(destination, "app", "stop");
        assertThat(stop.exitCode()).describedAs(stop.output()).isZero();

        Awaitility.await()
          .atMost(Duration.ofSeconds(30))
          .pollInterval(Duration.ofSeconds(2))
          .untilAsserted(() -> assertThat(deployment.runningContainerName(destination)).isBlank());
      }
    }
  }

  private static SshKeyHelper.GeneratedKeyPair createKeyPair() {
    try {
      return SshKeyHelper.generate();
    } catch (IOException | GeneralSecurityException e) {
      throw new ExceptionInInitializerError(e);
    }
  }
}
