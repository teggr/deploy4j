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

  private static final String APP_VERSION = "latest";
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
    Process process = new ProcessBuilder()
      .directory(tempDir.toFile())
      .command(
        Path.of(System.getProperty("java.home"), "bin", "java").toString(),
        "-cp",
        System.getProperty("java.class.path"),
        InitializerRunner.class.getName()
      )
      .redirectErrorStream(true)
      .start();

    String output = new String(process.getInputStream().readAllBytes());
    int exitCode = process.waitFor();

    assertThat(exitCode).describedAs(output).isZero();
    assertThat(tempDir.resolve("config/deploy.yml")).exists();
    assertThat(tempDir.resolve(".deploy4j/secrets")).exists();
    assertThat(Files.readString(tempDir.resolve("config/deploy.yml"))).contains("service: deploy4j-demo");
    assertThat(Files.readString(tempDir.resolve(".deploy4j/secrets"))).contains("DOCKER_USERNAME=");
  }

  @Test
  @DisplayName("setup deploys and manages a Spring Boot application")
  void setupDeploysAndExercisesLifecycle() throws Exception {
    try (DeployConfigHelper.TestDeployment deployment = DeployConfigHelper.create(DROPLET, SSH_KEY_PAIR.privateKeyPath(), APP_VERSION)) {
      deployment.applicationContext().server().bootstrap(deployment.deployContext());
      deployment.applicationContext().deploy().deploy(deployment.deployContext(), true, false);

      awaitHealth(deployment);

      assertThat(deployment.capture("docker ps --format '{{.Names}}'"))
        .contains(deployment.role().containerName(APP_VERSION));

      deployment.applicationContext().app().stop(deployment.deployContext());

      Awaitility.await()
        .atMost(Duration.ofSeconds(30))
        .pollInterval(Duration.ofSeconds(2))
        .untilAsserted(() -> assertThat(deployment.capture(deployment.appCommands().currentRunningContainerId()).trim()).isEmpty());

      deployment.applicationContext().app().start(deployment.deployContext());
      awaitHealth(deployment);

      String logs = deployment.capture(deployment.appCommands().logs(null, false, null, "200", null, null));
      assertThat(logs).isNotBlank();
      assertThat(logs).contains("Started Deploy4jIntegrationApp");

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
