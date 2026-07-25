package dev.deploy4j.it;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.locks.ReentrantLock;

final class DeployConfigHelper {

  private static final String DEMO_IMAGE = System.getProperty("deploy4j.demo.image", "teggr/deploy4j-demo");
  private static final String DEMO_VERSION = System.getProperty("deploy4j.demo.version");
  private static final ReentrantLock CLI_LOCK = new ReentrantLock();

  private DeployConfigHelper() {
  }

  static TestDeployment create(DropletContainer droplet, Path privateKeyPath) throws Exception {
    Path projectDirectory = Files.createTempDirectory("deploy4j-it-project");
    Path configDirectory = Files.createDirectories(projectDirectory.resolve("config"));
    String testServiceName = "deploy4j-it-" + UUID.randomUUID().toString().replace("-", "");
    Files.createDirectories(projectDirectory.resolve(".deploy4j"));
    Files.writeString(projectDirectory.resolve(".deploy4j/secrets"), "");
    Files.writeString(configDirectory.resolve("deploy.yml"), configYaml(testServiceName, droplet, privateKeyPath, testServiceName + "-db"));
    return new TestDeployment(projectDirectory, droplet, privateKeyPath, testServiceName, DEMO_VERSION);
  }

  private static String configYaml(String serviceName, DropletContainer droplet, Path privateKeyPath, String databaseHost) {
    return """
      service: %s
      image: %s
      registry: {}
      servers:
        - "%s"
      ssh:
        user: root
        port: %d
        key_path: "%s"
        strict_host_key_checking: false
      traefik:
       host_port: 80
      healthcheck:
       port: 8080
       path: /
      env:
       clear:
         SPRING_DATASOURCE_URL: jdbc:postgresql://%s:5432/testdb
         SPRING_DATASOURCE_USERNAME: testuser
         SPRING_DATASOURCE_PASSWORD: testpass
      accessories:
       db:
         image: postgres:18-alpine
         host: %s
         env:
           clear:
             POSTGRES_USER: testuser
             POSTGRES_PASSWORD: testpass
             POSTGRES_DB: testdb
         directories:
           - data:/var/lib/postgresql/18/docker
      """.formatted(
      serviceName,
      DEMO_IMAGE,
      droplet.getHost(),
      droplet.sshPort(),
      privateKeyPath,
      databaseHost,
      droplet.getHost()
    );
  }

  private static void deleteRecursively(Path root) throws IOException {
    if (!Files.exists(root)) {
      return;
    }
    try (var stream = Files.walk(root)) {
      stream.sorted(Comparator.reverseOrder()).forEach(path -> {
        try {
          Files.deleteIfExists(path);
        } catch (IOException e) {
          throw new RuntimeException(e);
        }
      });
    } catch (RuntimeException e) {
      if (e.getCause() instanceof IOException ioException) {
        throw ioException;
      }
      throw e;
    }
  }

  static CliResult executeCli(Path workingDirectory, String... args) {
    CLI_LOCK.lock();
    try {
      List<String> command = new ArrayList<>();
      command.add(Path.of(System.getProperty("java.home"), "bin", "java").toString());
      command.add("-cp");
      command.add(System.getProperty("java.class.path"));
      command.add("dev.deploy4j.cli.Deploy4jApplicationCommand");
      command.addAll(Arrays.asList(args));

      Process process = new ProcessBuilder(command)
        .directory(workingDirectory.toFile())
        .redirectErrorStream(true)
        .start();

      String output = new String(process.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
      int exitCode = process.waitFor();
      return new CliResult(exitCode, output);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new IllegalStateException("CLI command failed: " + String.join(" ", args), e);
    } catch (IOException e) {
      throw new IllegalStateException("CLI command failed: " + String.join(" ", args), e);
    } finally {
      CLI_LOCK.unlock();
    }
  }

  record CliResult(int exitCode, String output) {
  }

  private static String shellQuote(String value) {
    return "'" + value.replace("'", "'\"'\"'") + "'";
  }

  static final class TestDeployment implements AutoCloseable {

    private final Path projectDirectory;
    private final DropletContainer droplet;
    private final Path privateKeyPath;
    private final String serviceName;
    private final String version;

    private TestDeployment(
      Path projectDirectory,
      DropletContainer droplet,
      Path privateKeyPath,
      String serviceName,
      String version
    ) {
      this.projectDirectory = projectDirectory;
      this.droplet = droplet;
      this.privateKeyPath = privateKeyPath;
      this.serviceName = serviceName;
      this.version = version;
    }

    CliResult executeCli(String... args) {
      return DeployConfigHelper.executeCli(projectDirectory, args);
    }

    String version() {
      return version;
    }

    void updateDatabaseHost(String databaseHost) throws IOException {
      Files.writeString(
        projectDirectory.resolve("config/deploy.yml"),
        configYaml(serviceName, droplet, privateKeyPath, databaseHost)
      );
    }

    String databaseIp() {
      return capture(
        "docker inspect -f '{{range.NetworkSettings.Networks}}{{.IPAddress}}{{end}}' " + shellQuote(serviceName + "-db")
      ).trim();
    }

    String capture(String command) {
      try {
        Process process = new ProcessBuilder(
          "ssh",
          "-i", privateKeyPath.toString(),
          "-o", "StrictHostKeyChecking=no",
          "-o", "UserKnownHostsFile=/dev/null",
          "-o", "LogLevel=ERROR",
          "-p", Integer.toString(droplet.sshPort()),
          "root@" + droplet.getHost(),
          command
        )
          .redirectErrorStream(true)
          .start();

        String output = new String(process.getInputStream().readAllBytes());
        int exitCode = process.waitFor();
        if (exitCode != 0) {
          throw new IllegalStateException("SSH command failed (%s):\n%s".formatted(command, output));
        }
        return output;
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        throw new IllegalStateException("SSH command failed: " + command, e);
      } catch (IOException e) {
        throw new IllegalStateException("SSH command failed: " + command, e);
      }
    }

    String runningContainerName() {
      return capture("docker ps --format '{{.Names}}' | grep " + shellQuote("^" + serviceName + "-web-") + " | head -n1 || true").trim();
    }

    String containerIp() {
      String runningContainerName = runningContainerName();
      if (runningContainerName.isBlank()) {
        return "";
      }
      return capture(
        "docker inspect -f '{{range.NetworkSettings.Networks}}{{.IPAddress}}{{end}}' " + shellQuote(runningContainerName)
      ).trim();
    }

    @Override
    public void close() throws Exception {
      deleteRecursively(projectDirectory);
    }
  }
}
