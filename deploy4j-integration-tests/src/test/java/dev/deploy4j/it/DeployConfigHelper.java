package dev.deploy4j.it;

import dev.deploy4j.deploy.DeployApplicationContext;
import dev.deploy4j.deploy.DeployContext;
import dev.deploy4j.deploy.Hooks;
import dev.deploy4j.deploy.configuration.Configuration;
import dev.deploy4j.deploy.configuration.Role;
import dev.deploy4j.deploy.host.commands.AppHostCommands;
import dev.deploy4j.deploy.host.commands.AppHostCommandsFactory;
import dev.deploy4j.deploy.host.commands.SpringBootHostCommands;
import dev.deploy4j.deploy.host.commands.SpringBootHostCommandsFactory;
import dev.deploy4j.deploy.host.ssh.SshHost;
import dev.deploy4j.deploy.host.ssh.SshHosts;
import dev.deploy4j.deploy.local.LocalHost;
import dev.rebelcraft.cmd.Cmd;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.UUID;

final class DeployConfigHelper {

  private static final String TEST_IMAGE = "deploy4j-it-app";

  private DeployConfigHelper() {
  }

  static TestDeployment create(DropletContainer droplet, Path privateKeyPath, String version) throws Exception {
    Path projectDirectory = Files.createTempDirectory("deploy4j-it-project");
    Path configDirectory = Files.createDirectories(projectDirectory.resolve("config"));
    String serviceName = "deploy4j-it-" + UUID.randomUUID().toString().substring(0, 8);
    buildTestImage(projectDirectory.resolve("test-app"));
    Files.createDirectories(projectDirectory.resolve(".deploy4j"));
    Files.writeString(projectDirectory.resolve(".deploy4j/secrets"), "");
    Files.writeString(configDirectory.resolve("deploy.yml"), configYaml(serviceName, droplet, privateKeyPath));

    String originalUserDir = System.getProperty("user.dir");
    System.setProperty("user.dir", projectDirectory.toString());

    try {
      Configuration configuration = Configuration.createFrom(configDirectory.resolve("deploy.yml").toString(), null, version);
      DeployContext deployContext = new DeployContext(configuration, null, null, null);
      LocalHost localHost = new LocalHost();
      Hooks hooks = new Hooks(localHost, configuration, false);
      SshHosts sshHosts = new SshHosts(configuration);
      DeployApplicationContext applicationContext = new DeployApplicationContext(sshHosts, hooks, localHost, deployContext);
      return new TestDeployment(projectDirectory, originalUserDir, droplet, configuration, deployContext, sshHosts, applicationContext);
    } catch (Exception e) {
      System.setProperty("user.dir", originalUserDir);
      deleteRecursively(projectDirectory);
      throw e;
    }
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
          System.out.println("Started Deploy4jIntegrationApp");
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
      throw new IllegalStateException("Failed to build local test image:%n%s".formatted(output));
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

  static final class TestDeployment implements AutoCloseable {

    private final Path projectDirectory;
    private final String originalUserDir;
    private final DropletContainer droplet;
    private final Configuration configuration;
    private final DeployContext deployContext;
    private final SshHosts sshHosts;
    private final DeployApplicationContext applicationContext;

    private TestDeployment(
      Path projectDirectory,
      String originalUserDir,
      DropletContainer droplet,
      Configuration configuration,
      DeployContext deployContext,
      SshHosts sshHosts,
      DeployApplicationContext applicationContext
    ) {
      this.projectDirectory = projectDirectory;
      this.originalUserDir = originalUserDir;
      this.droplet = droplet;
      this.configuration = configuration;
      this.deployContext = deployContext;
      this.sshHosts = sshHosts;
      this.applicationContext = applicationContext;
    }

    DeployApplicationContext applicationContext() {
      return applicationContext;
    }

    DeployContext deployContext() {
      return deployContext;
    }

    AppHostCommands appCommands() {
      return new AppHostCommandsFactory(configuration).app(role(), primaryHost());
    }

    SpringBootHostCommands springBootCommands() {
      return new SpringBootHostCommandsFactory(configuration).forContainer(role().containerName(configuration.version()));
    }

    String primaryHost() {
      return deployContext.primaryHost();
    }

    Role role() {
      return deployContext.primaryRole();
    }

    SshHost sshHost() {
      return sshHosts.host(primaryHost());
    }

    String capture(Cmd cmd) {
      return sshHost().capture(cmd, false);
    }

    String capture(String command) {
      return sshHost().capture(command, false);
    }

    String containerIp() {
      return capture("docker inspect -f '{{range.NetworkSettings.Networks}}{{.IPAddress}}{{end}}' " + role().containerName(configuration.version())).trim();
    }

    @Override
    public void close() throws Exception {
      try {
        sshHosts.close();
      } finally {
        System.setProperty("user.dir", originalUserDir);
        deleteRecursively(projectDirectory);
      }
    }
  }
}
