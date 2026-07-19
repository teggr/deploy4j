package dev.deploy4j.it;

import org.awaitility.Awaitility;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.GeneralSecurityException;
import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
class Deploy4jSmokeIT {

  private static final SshKeyHelper.GeneratedKeyPair SSH_KEY_PAIR = createKeyPair();

  @Container
  private static final DropletContainer DROPLET = new DropletContainer(SSH_KEY_PAIR.authorizedKeysPath());

  @AfterAll
  static void tearDownKeys() throws Exception {
    SSH_KEY_PAIR.close();
  }

  @TempDir
  Path tempDir;

  @Test
  @DisplayName("init creates deploy config and secrets stubs")
  void initCreatesDeployFiles() throws Exception {
    DeployConfigHelper.CliResult result = DeployConfigHelper.executeCli(tempDir, "init");

    assertThat(result.exitCode()).describedAs(result.output()).isZero();
    assertThat(tempDir.resolve("config/deploy.yml")).exists();
    assertThat(tempDir.resolve(".deploy4j/secrets")).exists();
    assertThat(Files.readString(tempDir.resolve("config/deploy.yml"))).contains("service: deploy4j-demo");
    assertThat(Files.readString(tempDir.resolve(".deploy4j/secrets"))).contains("DOCKER_USERNAME=");
  }

  @Test
  @DisplayName("setup deploys and manages a Spring Boot application")
  void setupDeploysAndExercisesLifecycle() throws Exception {
    try (DeployConfigHelper.TestDeployment deployment = DeployConfigHelper.create(DROPLET, SSH_KEY_PAIR.privateKeyPath())) {
      DeployConfigHelper.CliResult bootstrap = deployment.executeCli("server", "bootstrap");
      assertThat(bootstrap.exitCode()).describedAs(bootstrap.output()).isZero();
      DeployConfigHelper.CliResult deploy = deployment.executeCli("deploy", "-P");
      assertThat(deploy.exitCode()).describedAs(deploy.output()).isZero();

      awaitHealth(deployment);

      String runningContainerName = deployment.runningContainerName();
      assertThat(runningContainerName).isNotBlank();
      assertThat(deployment.capture("docker ps --format '{{.Names}}'"))
        .contains(runningContainerName);

      DeployConfigHelper.CliResult stop = deployment.executeCli("app", "stop");
      assertThat(stop.exitCode()).describedAs(stop.output()).isZero();

      Awaitility.await()
        .atMost(Duration.ofSeconds(30))
        .pollInterval(Duration.ofSeconds(2))
        .untilAsserted(() -> assertThat(deployment.runningContainerName()).isBlank());

      DeployConfigHelper.CliResult start = deployment.executeCli("app", "start");
      assertThat(start.exitCode()).describedAs(start.output()).isZero();
      awaitHealth(deployment);

      String logs = deployment.capture("docker logs " + deployment.runningContainerName());
      assertThat(logs).isNotBlank();
      assertThat(logs).contains("Started ActuatorApp");

      String health = actuatorHealth(deployment);
      assertThat(health).contains("\"status\":\"UP\"");
    }
  }

  private static void awaitHealth(DeployConfigHelper.TestDeployment deployment) {
    Awaitility.await()
      .atMost(Duration.ofMinutes(3))
      .pollInterval(Duration.ofSeconds(5))
      .untilAsserted(() -> assertThat(actuatorHealth(deployment)).contains("\"status\":\"UP\""));
  }

  private static String actuatorHealth(DeployConfigHelper.TestDeployment deployment) {
    return deployment.capture(
      "docker run --rm --network deploy4j curlimages/curl -s http://%s:8080/actuator/health"
        .formatted(deployment.containerIp())
    );
  }

  private static SshKeyHelper.GeneratedKeyPair createKeyPair() {
    try {
      return SshKeyHelper.generate();
    } catch (IOException | GeneralSecurityException e) {
      throw new ExceptionInInitializerError(e);
    }
  }
}
