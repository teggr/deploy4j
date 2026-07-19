package dev.deploy4j.it;

import dev.deploy4j.cli.Deploy4jApplicationCommand;
import picocli.CommandLine;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.UUID;
import java.util.concurrent.locks.ReentrantLock;

final class DeployConfigHelper {

  private static final String TEST_IMAGE = "deploy4j-it-app";
  private static final ReentrantLock CLI_LOCK = new ReentrantLock();

  private DeployConfigHelper() {
  }

  static TestDeployment create(DropletContainer droplet, Path privateKeyPath) throws Exception {
    Path projectDirectory = Files.createTempDirectory("deploy4j-it-project");
    Path configDirectory = Files.createDirectories(projectDirectory.resolve("config"));
    String testServiceName = "deploy4j-it-" + UUID.randomUUID().toString().replace("-", "");
    buildTestImage(projectDirectory.resolve("test-app"));
    Files.createDirectories(projectDirectory.resolve(".deploy4j"));
    Files.writeString(projectDirectory.resolve(".deploy4j/secrets"), "");
    Files.writeString(configDirectory.resolve("deploy.yml"), configYaml(testServiceName, droplet, privateKeyPath));
    return new TestDeployment(projectDirectory, droplet, privateKeyPath, testServiceName);
  }

  private static String configYaml(String serviceName, DropletContainer droplet, Path privateKeyPath) {
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
        path: /actuator/health
      env:
        clear:
          MANAGEMENT_ENDPOINTS_WEB_EXPOSURE_INCLUDE: "health,info"
          MANAGEMENT_ENDPOINT_HEALTH_SHOW_DETAILS: "always"
      """.formatted(serviceName, TEST_IMAGE, droplet.getHost(), droplet.sshPort(), privateKeyPath);
  }

  private static void buildTestImage(Path buildDirectory) throws IOException, InterruptedException {
    Files.createDirectories(buildDirectory);
    Files.writeString(buildDirectory.resolve("ActuatorApp.java"), """
      import com.sun.net.httpserver.HttpServer;
      import java.net.InetSocketAddress;
      import java.nio.charset.StandardCharsets;

      public class ActuatorApp {
        public static void main(String[] args) throws Exception {
          HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
          server.createContext("/", exchange -> respond(exchange, 200, "text/plain", "ok"));
          server.createContext("/actuator/health", exchange -> respond(exchange, 200, "application/json", "{\\"status\\":\\"UP\\"}"));
          server.start();
          System.out.println("Started ActuatorApp");
          Thread.currentThread().join();
        }

        private static void respond(com.sun.net.httpserver.HttpExchange exchange, int status, String contentType, String body) throws java.io.IOException {
          byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
          exchange.getResponseHeaders().add("Content-Type", contentType);
          exchange.sendResponseHeaders(status, bytes.length);
          try (var output = exchange.getResponseBody()) {
            output.write(bytes);
          }
        }
      }
      """);
    Files.writeString(buildDirectory.resolve("Dockerfile"), """
      FROM eclipse-temurin:21-jdk
      RUN apt-get update && apt-get install -y --no-install-recommends curl && rm -rf /var/lib/apt/lists/*
      WORKDIR /app
      COPY ActuatorApp.java /app/ActuatorApp.java
      RUN javac ActuatorApp.java
      EXPOSE 8080
      CMD ["java", "ActuatorApp"]
      """);

    Process process = new ProcessBuilder(
      "docker", "build", "-t", TEST_IMAGE + ":latest", "."
    )
      .directory(buildDirectory.toFile())
      .redirectErrorStream(true)
      .start();

    String output = new String(process.getInputStream().readAllBytes());
    int exitCode = process.waitFor();
    if (exitCode != 0) {
      throw new IllegalStateException("Failed to build local test image:\n%s".formatted(output));
    }
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
    ByteArrayOutputStream output = new ByteArrayOutputStream();
    PrintStream printStream = new PrintStream(output, true);
    PrintStream originalOut = System.out;
    PrintStream originalErr = System.err;
    String originalUserDir = System.getProperty("user.dir");

    CLI_LOCK.lock();
    try {
      System.setOut(printStream);
      System.setErr(printStream);
      System.setProperty("user.dir", workingDirectory.toString());

      CommandLine commandLine = new CommandLine(new Deploy4jApplicationCommand());
      commandLine.setOut(new java.io.PrintWriter(printStream, true));
      commandLine.setErr(new java.io.PrintWriter(printStream, true));

      int exitCode = commandLine.execute(args);
      printStream.flush();
      return new CliResult(exitCode, output.toString());
    } finally {
      System.setOut(originalOut);
      System.setErr(originalErr);
      System.setProperty("user.dir", originalUserDir);
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

    private TestDeployment(
      Path projectDirectory,
      DropletContainer droplet,
      Path privateKeyPath,
      String serviceName
    ) {
      this.projectDirectory = projectDirectory;
      this.droplet = droplet;
      this.privateKeyPath = privateKeyPath;
      this.serviceName = serviceName;
    }

    CliResult executeCli(String... args) {
      return DeployConfigHelper.executeCli(projectDirectory, args);
    }

    String capture(String command) {
      try {
        Process process = new ProcessBuilder(
          "ssh",
          "-i", privateKeyPath.toString(),
          "-o", "StrictHostKeyChecking=no",
          "-o", "UserKnownHostsFile=/dev/null",
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
      return capture("docker ps --format '{{.Names}}' | grep " + shellQuote("^" + serviceName + "-") + " | head -n1 || true").trim();
    }

    String containerIp() {
      return capture(
        "docker inspect -f '{{range.NetworkSettings.Networks}}{{.IPAddress}}{{end}}' " + shellQuote(runningContainerName())
      ).trim();
    }

    @Override
    public void close() throws Exception {
      deleteRecursively(projectDirectory);
    }
  }
}
