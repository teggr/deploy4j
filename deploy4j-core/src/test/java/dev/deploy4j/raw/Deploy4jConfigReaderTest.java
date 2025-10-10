package dev.deploy4j.raw;

import org.junit.jupiter.api.Test;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

class Deploy4jConfigReaderTest {

  @Test
  void readYaml() throws Exception {
    String yamlPath = Paths.get("src", "test", "resources", "deployment", "example-deployment.yml").toString();
    Deploy4jConfig config = Deploy4jConfigReader.readYaml(yamlPath);

    assertNotNull(config);
    assertEquals("deploy4j-demo", config.service());
    assertEquals("teggr/deploy4j-demo", config.image());
    assertNotNull(config.registry());
    assertEquals("DOCKER_USERNAME", config.registry().username());
    assertEquals("DOCKER_PASSWORD", config.registry().password());
    assertNotNull(config.servers());
    assertFalse(config.servers().isEmpty());
    assertEquals("localhost", config.servers().get(0).host());
    assertNotNull(config.ssh());
    assertEquals("root", config.ssh().user());
    assertEquals("2222", config.ssh().port());
    assertEquals("PRIVATE_KEY", config.ssh().privateKey());
    assertEquals("PRIVATE_KEY_PASSPHRASE", config.ssh().privateKeyPassphrase());
    assertFalse(config.ssh().strictHostChecking());
    assertNotNull(config.env());
    assertEquals("mysql-db", config.env().get("DATABASE_HOST"));
    assertNotNull(config.traefik());
    assertNotNull(config.traefik().args());
    assertEquals("true", config.traefik().args().get("api.insecure").toString());
    assertNotNull(config.traefik().options());
    assertEquals("8080:8080", config.traefik().options().get("publish"));
  }
}