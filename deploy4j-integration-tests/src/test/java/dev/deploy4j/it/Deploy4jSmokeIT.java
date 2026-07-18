package dev.deploy4j.it;

import dev.deploy4j.init.Initializer;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.GeneralSecurityException;
import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
class Deploy4jSmokeIT {

  private static final String APP_VERSION = "latest";
  private static final HttpClient HTTP_CLIENT = HttpClient.newHttpClient();
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
    Initializer initializer = new Initializer();
    String originalUserDir = System.getProperty("user.dir");
    System.setProperty("user.dir", tempDir.toString());

    try {
      initializer.init(false);

      assertThat(tempDir.resolve("config/deploy.yml")).exists();
      assertThat(tempDir.resolve(".deploy4j/secrets")).exists();
      assertThat(Files.readString(tempDir.resolve("config/deploy.yml"))).contains("service: deploy4j-demo");
      assertThat(Files.readString(tempDir.resolve(".deploy4j/secrets"))).contains("DOCKER_USERNAME=");
    } finally {
      System.setProperty("user.dir", originalUserDir);
    }
  }

  @Test
  @DisplayName("setup deploys and manages a Spring Boot application")
  void setupDeploysAndExercisesLifecycle() throws Exception {
    try (DeployConfigHelper.TestDeployment deployment = DeployConfigHelper.create(DROPLET, SSH_KEY_PAIR.privateKeyPath(), APP_VERSION)) {
      deployment.applicationContext().deploy().setup(deployment.deployContext());

      awaitHealth(deployment.actuatorUri());

      assertThat(deployment.capture(deployment.appCommands().listContainers()))
        .contains(deployment.role().containerName(APP_VERSION));

      deployment.applicationContext().app().stop(deployment.deployContext());

      Awaitility.await()
        .atMost(Duration.ofSeconds(30))
        .pollInterval(Duration.ofSeconds(2))
        .untilAsserted(() -> assertThat(deployment.capture(deployment.appCommands().currentRunningContainerId()).trim()).isEmpty());

      deployment.applicationContext().app().start(deployment.deployContext());
      awaitHealth(deployment.actuatorUri());

      String logs = deployment.capture(deployment.appCommands().logs(null, false, null, "200", null, null));
      assertThat(logs).isNotBlank();
      assertThat(logs).contains("Started");

      HttpResponse<String> response = get(deployment.actuatorUri());
      assertThat(response.statusCode()).isEqualTo(200);
      assertThat(response.body()).contains("\"status\":\"UP\"");
    }
  }

  private static void awaitHealth(URI actuatorUri) {
    Awaitility.await()
      .atMost(Duration.ofMinutes(3))
      .pollInterval(Duration.ofSeconds(5))
      .untilAsserted(() -> {
        HttpResponse<String> response = get(actuatorUri);
        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.body()).contains("\"status\":\"UP\"");
      });
  }

  private static HttpResponse<String> get(URI uri) throws IOException, InterruptedException {
    HttpRequest request = HttpRequest.newBuilder(uri)
      .timeout(Duration.ofSeconds(10))
      .GET()
      .build();
    return HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
  }

  private static SshKeyHelper.GeneratedKeyPair createKeyPair() {
    try {
      return SshKeyHelper.generate();
    } catch (IOException | GeneralSecurityException e) {
      throw new ExceptionInInitializerError(e);
    }
  }
}
