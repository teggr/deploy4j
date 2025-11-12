package dev.deploy4j.deploy.configuration;

import dev.deploy4j.deploy.configuration.raw.*;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class RoleTest {

  @Test
  void basicNamesAndLabelsAndContainer() {

    // build a simple deploy config with a service and servers map (role "web")
    ServersConfig servers = new ServersConfig(Map.of(
      "web", new RoleConfig(List.of("host1", "host2"))
    ));

    EnvironmentConfig env = new EnvironmentConfig(Map.of("K", "V"), null, null, null);

    DeployConfig raw = DeployConfigBuilder.minimal()
      .service("svc")
      .labels(Map.of("custom", "val"))
      .servers(servers)
      .env(env)
      .registry(new RegistryConfig(null, null, null))
      .build();

    Configuration config = new Configuration(raw, "dest", "v1");
    Role role = config.role("web");
    assertNotNull(role);

    // name and toString
    assertEquals("web", role.name());
    assertEquals("web", role.toString());

    // container prefix and name
    assertEquals("svc-web-dest", role.containerPrefix());
    assertEquals("svc-web-dest-abc", role.containerName("abc"));
    // when version is null it should use config.version()
    assertEquals("svc-web-dest-v1", role.containerName(null));

    // labels should contain defaults and custom
    Map<String, String> labels = role.labels();
    assertEquals("svc", labels.get("service"));
    assertEquals("web", labels.get("role"));
    assertEquals("dest", labels.get("destination"));
    assertEquals("val", labels.get("custom"));

    // labelArgs should produce an array (non-empty)
    String[] labelArgs = role.labelArgs();
    assertNotNull(labelArgs);
    assertTrue(labelArgs.length > 0);
  }

  @Test
  void hostsAndEnvArgsAndPrimaryHost() {
    ServersConfig servers = new ServersConfig(Map.of(
      "web", new RoleConfig(List.of("hostA", "hostB"))
    ));

    EnvironmentConfig env = new EnvironmentConfig(Map.of("FOO", "BAR"), null, null, null);

    DeployConfig raw = DeployConfigBuilder.minimal()
      .service("svc")
      .servers(servers)
      .env(env)
      .build();

    Configuration config = new Configuration(raw, "dest", "ver");
    Role role = config.role("web");

    List<String> hosts = role.hosts();
    assertEquals(2, hosts.size());
    assertEquals("hostA", role.primaryHost());

    // envArgs should include the --env-file entry (at minimum)
    List<String> envArgs = role.envArgs("hostA");
    assertNotNull(envArgs);
    assertTrue(envArgs.contains("--env-file"));

    // env() should cache per-host and return consistent object
    Env env1 = role.env("hostA");
    Env env2 = role.env("hostA");
    assertSame(env1, env2);
  }

  @Test
  void healthcheckAndCordBehaviour() {
    // Build a custom role specialization that includes a healthcheck with port/path and a cord
    HealthCheckConfig roleHealth = new HealthCheckConfig(
      null, // cmd
      "2s", // interval
      7, // maxAttempts
      9090, // port
      "/hc",
      "/tmp/cord", // cord path inside container
      50 // logLines
    );

    CustomRoleConfig custom = new CustomRoleConfig(
      List.of("host1"),
      null,
      null,
      null,
      null,
      roleHealth,
      null,
      null,
      null
    );

    RoleConfig roleConfig = new RoleConfig(custom);
    ServersConfig servers = new ServersConfig(Map.of("web", roleConfig));

    DeployConfig raw = DeployConfigBuilder.minimal()
      .service("svc")
      .servers(servers)
      .build();

    Configuration config = new Configuration(raw, "dst", "rv");
    Role role = config.role("web");

    // healthcheck should reflect the specialized config
    HealthCheck hc = role.healthcheck();
    assertEquals(9090, hc.port());
    assertEquals("/hc", hc.path());
    assertTrue(hc.setPortOrPath());

    // cord volume should be created
    assertNotNull(role.cordVolume());
    assertTrue(role.usesCord());

    // cord host/container file names
    assertTrue(role.cordHostFile().endsWith("/cord"));
    assertTrue(role.cordContainerFile().endsWith("/cord"));

    // healthCheckCmdWithCord should include the health cmd and cord container file
    String cmdWithCord = role.healthCheckCmdWithCord();
    assertNotNull(cmdWithCord);
    assertTrue(cmdWithCord.contains(role.cordContainerFile()));
  }

  @Test
  void assetsAndAssetVolumePaths() {
    // role should report assets when an assetPath is provided and traefik is running (primary)
    ServersConfig servers = new ServersConfig(Map.of(
      "web", new RoleConfig(List.of("host1"))
    ));

    DeployConfig raw = DeployConfigBuilder.minimal()
      .service("svc")
      .servers(servers)
      .assetPath("/var/www")
      .build();

    Configuration config = new Configuration(raw, "dest", "1.2.3");
    Role role = config.role("web");

    assertEquals("/var/www", role.assetPath());
    assertTrue(role.assets());

    Volume vol = role.assetVolume("1.2.3");
    assertNotNull(vol);
    assertEquals(role.assetPath(), vol.containerPath());
    assertEquals(role.assetVolumePath("1.2.3"), vol.hostPath());

    // extracted path
    String extracted = role.assetExtractedPath("1.2.3");
    assertTrue(extracted.contains("assets"));
    assertTrue(extracted.contains(role.containerName("1.2.3")));
  }

  @Test
  void attributeAccessors() {
    ServersConfig servers = new ServersConfig(Map.of(
      "web", new RoleConfig(List.of("a"))
    ));
    DeployConfig raw = DeployConfigBuilder.minimal()
      .service("svc")
      .servers(servers)
      .build();

    Configuration config = new Configuration(raw, null, null);
    Role role = config.role("web");

    assertEquals("web", role.name());
    assertSame(config, role.config());
    assertNotNull(role.specializedEnv());
    assertNotNull(role.specializedLogging());
    assertNotNull(role.specializedHealthCheck());
  }

}