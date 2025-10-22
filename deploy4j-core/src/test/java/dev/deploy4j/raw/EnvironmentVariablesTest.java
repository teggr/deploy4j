package dev.deploy4j.raw;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import dev.deploy4j.deploy.raw.EnvironmentConfig;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EnvironmentVariablesTest {

  private ObjectMapper mapper = new ObjectMapper(new YAMLFactory());

  @Test
  void shouldReadInEnvironmentVariablesSetDirectly() throws JsonProcessingException {

    // given
    String yml = """
      env:
        DATABASE_HOST: mysql-db1
        DATABASE_PORT: 3306
      """;

    // when
    TestConfig testConfig = mapper.readValue(yml, TestConfig.class);

    // then
    assertTrue(testConfig.env.isAMap());
    assertEquals(2, testConfig.env.map().size());

    assertEquals("mysql-db1", testConfig.env.map().get("DATABASE_HOST"));
    assertEquals("3306", testConfig.env.map().get("DATABASE_PORT"));

  }

  @Test
  void shouldReadInEnvironmentVariablesWithClearAndSecret() throws JsonProcessingException {

    // given
    String yml = """
      env:
        clear:
          DB_USER: app
        secret:
          - DB_PASSWORD
      """;

    // when
    TestConfig testConfig = mapper.readValue(yml, TestConfig.class);

    // then
    assertTrue(testConfig.env.isClearAndSecrets());
    assertEquals(1, testConfig.env.clear().size());
    assertEquals(1, testConfig.env.secrets().size());

    assertEquals("app", testConfig.env.clear().get("DB_USER"));
    assertEquals("DB_PASSWORD", testConfig.env.secrets().get(0));

  }

  @Test
  void shouldReadInEnvironmentVariablesForTags() throws JsonProcessingException {

    // given
    String yml = """
      env:
        tags:
          tag1:
            MYSQL_USER: monitoring
          tag2:
            clear:
              MYSQL_USER: readonly
            secret:
              - MYSQL_PASSWORD
      """;

    // when
    TestConfig testConfig = mapper.readValue(yml, TestConfig.class);

    // then
    assertTrue(testConfig.env.isTags());
    assertEquals(2, testConfig.env.tags().size());

    EnvironmentConfig tag1 = testConfig.env.tags().get("tag1");
    assertTrue(tag1.isAMap());
    assertEquals(1, tag1.map().size());
    assertEquals("monitoring", tag1.map().get("MYSQL_USER"));

    EnvironmentConfig tag2 = testConfig.env.tags().get("tag2");
    assertTrue(tag2.isClearAndSecrets());
    assertEquals(1, tag2.clear().size());
    assertEquals("readonly", tag2.clear().get("MYSQL_USER"));
    assertEquals(1, tag2.secrets().size());
    assertEquals("MYSQL_PASSWORD", tag2.secrets().get(0));

  }

  @Test
  void shouldReadInEnvironmentVariablesFromExampleConfiguration() throws JsonProcessingException {

    // given
    String yml = """
      env:
        clear:
          MYSQL_USER: app
        secret:
          - MYSQL_PASSWORD
        tags:
          monitoring:
            MYSQL_USER: monitoring
          replica:
            clear:
              MYSQL_USER: readonly
            secret:
              - READONLY_PASSWORD
      """;

    // when
    TestConfig testConfig = mapper.readValue(yml, TestConfig.class);

    // then
    assertTrue(testConfig.env.isClearAndSecrets());
    assertTrue(testConfig.env.isTags());

    assertEquals(1, testConfig.env.clear().size());
    assertEquals("app", testConfig.env.clear().get("MYSQL_USER"));
    assertEquals(1, testConfig.env.secrets().size());
    assertEquals("MYSQL_PASSWORD", testConfig.env.secrets().get(0));

    assertEquals(2, testConfig.env.tags().size());

    EnvironmentConfig monitoringTag = testConfig.env.tags().get("monitoring");
    assertTrue(monitoringTag.isAMap());
    assertEquals(1, monitoringTag.map().size());
    assertEquals("monitoring", monitoringTag.map().get("MYSQL_USER"));

    EnvironmentConfig replicaTag = testConfig.env.tags().get("replica");
    assertTrue(replicaTag.isClearAndSecrets());
    assertEquals(1, replicaTag.clear().size());
    assertEquals("readonly", replicaTag.clear().get("MYSQL_USER"));
    assertEquals(1, replicaTag.secrets().size());
    assertEquals("READONLY_PASSWORD", replicaTag.secrets().get(0));

  }

  private record TestConfig(
    EnvironmentConfig env
  ) {
  }

}
