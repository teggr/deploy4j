package dev.deploy4j.raw;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ServersTest {

  private ObjectMapper mapper = new ObjectMapper(new YAMLFactory());

  @Test
  void shouldReadInListOfServers() throws JsonProcessingException {

    // given
    String yml = """
      servers:
        - 172.0.0.1
        - 172.0.0.2
        - 172.0.0.3
      """;

    // when
    TestConfig testConfig = mapper.readValue(yml, TestConfig.class);

    // then
    assertTrue(testConfig.servers.isAList());
    assertEquals(3, testConfig.servers.list().size());

    assertEquals("172.0.0.1", testConfig.servers.list().get(0).host());
    assertTrue(testConfig.servers.list().get(0).tags().isEmpty());
    assertEquals("172.0.0.2", testConfig.servers.list().get(1).host());
    assertTrue(testConfig.servers.list().get(1).tags().isEmpty());
    assertEquals("172.0.0.3", testConfig.servers.list().get(2).host());
    assertTrue(testConfig.servers.list().get(2).tags().isEmpty());

  }

  @Test
  void shouldReadInListOfTaggedServers() throws JsonProcessingException {

    // given
    String yml = """
      servers:
        - 172.0.0.1
        - 172.0.0.2: experiments
        - 172.0.0.3: [ experiments, three ]
      """;

    // when
    TestConfig testConfig = mapper.readValue(yml, TestConfig.class);

    // then
    assertTrue(testConfig.servers.isAList());
    assertEquals(3, testConfig.servers.list().size());

    assertEquals("172.0.0.1", testConfig.servers.list().get(0).host());
    assertEquals(List.of(), testConfig.servers.list().get(0).tags());
    assertEquals("172.0.0.2", testConfig.servers.list().get(1).host());
    assertEquals(List.of("experiments"), testConfig.servers.list().get(1).tags());
    assertEquals("172.0.0.3", testConfig.servers.list().get(2).host());
    assertEquals(List.of("experiments", "three"), testConfig.servers.list().get(2).tags());

  }

  @Test
  void shouldReadInRolesWithSimpleRoleConfigurations() throws JsonProcessingException {

    // given
    String yml = """
      servers:
        web:
          - 172.0.0.1
          - 172.0.0.2: experiments
          - 172.0.0.3: [ experiments, three ]
      """;

    // when
    TestConfig testConfig = mapper.readValue(yml, TestConfig.class);

    // then
    assertFalse(testConfig.servers.isAList());
    assertEquals(1, testConfig.servers.roles().size());

    RoleConfig roleConfig = testConfig.servers().roles().get("web");
    assertTrue(roleConfig.isAList());
    assertEquals(3, roleConfig.list().size());

    assertEquals("172.0.0.1", roleConfig.list().get(0).host());
    assertEquals(List.of(), roleConfig.list().get(0).tags());
    assertEquals("172.0.0.2", roleConfig.list().get(1).host());
    assertEquals(List.of("experiments"), roleConfig.list().get(1).tags());
    assertEquals("172.0.0.3", roleConfig.list().get(2).host());
    assertEquals(List.of("experiments", "three"), roleConfig.list().get(2).tags());

  }

  @Test
  void shouldReadInRolesWithCustomRoleConfigurations() throws JsonProcessingException {

    // given
    String yml = """
       servers:
         workers:
           hosts:
             - 172.1.0.3
             - 172.1.0.4: experiment1
           traefik: true
           cmd: "bin/jobs"
           options:
             memory: 2g
             cpus: 4
           labels:
             my-label: workers
           asset_path: /public
       """;

    // when
    TestConfig testConfig = mapper.readValue(yml, TestConfig.class);

    // then
    assertFalse(testConfig.servers.isAList());
    assertEquals(1, testConfig.servers.roles().size());

    RoleConfig roleConfig = testConfig.servers().roles().get("workers");
    assertFalse(roleConfig.isAList());

    CustomRoleConfig customRoleConfig = roleConfig.customRole();

    assertEquals(2, customRoleConfig.hosts().size());

    assertEquals("172.1.0.3", customRoleConfig.hosts().get(0).host());
    assertEquals(List.of(), customRoleConfig.hosts().get(0).tags());
    assertEquals("172.1.0.4", customRoleConfig.hosts().get(1).host());
    assertEquals(List.of("experiment1"), customRoleConfig.hosts().get(1).tags());

    assertTrue(customRoleConfig.traefik() );
    assertEquals("bin/jobs", customRoleConfig.cmd() );
  }

  @Test
  void shouldReadInCron() throws JsonProcessingException {

    // given
    String yml = """
       servers:
         cron:
           hosts:
             - 192.168.0.1
           cmd:
             bash -c "cat config/crontab | crontab - && cron -f"
       """;

    // when
    TestConfig testConfig = mapper.readValue(yml, TestConfig.class);

    // then
    assertFalse(testConfig.servers.isAList());
    assertEquals(1, testConfig.servers.roles().size());

    RoleConfig roleConfig = testConfig.servers().roles().get("cron");
    assertFalse(roleConfig.isAList());

    CustomRoleConfig customRoleConfig = roleConfig.customRole();

    assertEquals(1, customRoleConfig.hosts().size());

    assertEquals("192.168.0.1", customRoleConfig.hosts().get(0).host());
    assertEquals(List.of(), customRoleConfig.hosts().get(0).tags());

    assertEquals("bash -c \"cat config/crontab | crontab - && cron -f\"", customRoleConfig.cmd() );
  }

  private record TestConfig(
    ServersConfig servers
  ) {
  }

}
