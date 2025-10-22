package dev.deploy4j.raw;

import dev.deploy4j.deploy.raw.DeployConfig;
import dev.deploy4j.deploy.raw.DeployConfigYamlReader;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class Deploy4JYamlConfigReaderTest {

  @Test
  void readYaml() throws Exception {

    // given
    String yaml = """
      service: hey
      image: 37s/hey
      servers:
        - 192.168.0.1
        - 192.168.0.2
      registry:
        username: registry-user-name
        password:
          - KAMAL_REGISTRY_PASSWORD
      env:
        secret:
          - RAILS_MASTER_KEY
      """;

    // when
    DeployConfig config = DeployConfigYamlReader.readYamlFromString(yaml);

    // then
    assertEquals("hey", config.service());
    assertEquals("37s/hey", config.image());
    assertEquals(2, config.servers().list().size());
    assertEquals("registry-user-name", config.registry().username().value());
    assertEquals("KAMAL_REGISTRY_PASSWORD", config.registry().password().key());
    assertEquals("RAILS_MASTER_KEY", config.env().secrets().get(0));

  }

}