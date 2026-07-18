package dev.deploy4j.it;

import dev.deploy4j.deploy.DeployApplicationContext;
import dev.deploy4j.deploy.DeployContext;
import dev.deploy4j.deploy.Hooks;
import dev.deploy4j.deploy.configuration.Configuration;
import dev.deploy4j.deploy.configuration.Role;
import dev.deploy4j.deploy.host.commands.AppHostCommands;
import dev.deploy4j.deploy.host.commands.AppHostCommandsFactory;
import dev.deploy4j.deploy.host.ssh.SshHost;
import dev.deploy4j.deploy.host.ssh.SshHosts;
import dev.deploy4j.deploy.local.LocalHost;
import dev.rebelcraft.cmd.Cmd;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;

final class DeployConfigHelper {

  private DeployConfigHelper() {
  }

  static TestDeployment create(DropletContainer droplet, Path privateKeyPath, String version) throws Exception {
    Path projectDirectory = Files.createTempDirectory("deploy4j-it-project");
    Path configDirectory = Files.createDirectories(projectDirectory.resolve("config"));
    Files.createDirectories(projectDirectory.resolve(".deploy4j"));
    Files.writeString(projectDirectory.resolve(".deploy4j/secrets"), "");
    Files.writeString(configDirectory.resolve("deploy.yml"), configYaml(droplet, privateKeyPath));

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

  private static String configYaml(DropletContainer droplet, Path privateKeyPath) {
    return """
      service: deploy4j-demo
      image: teggr/deploy4j-demo
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
      """.formatted(droplet.getHost(), droplet.sshPort(), privateKeyPath);
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

    URI actuatorUri() {
      return droplet.actuatorUri();
    }

    AppHostCommands appCommands() {
      return new AppHostCommandsFactory(configuration).app(role(), primaryHost());
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
