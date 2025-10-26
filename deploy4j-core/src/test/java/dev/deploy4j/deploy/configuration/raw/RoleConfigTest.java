package dev.deploy4j.deploy.configuration.raw;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class RoleConfigTest {

    @Test
    void shouldCreateEmptyRoleConfig() {
        RoleConfig config = new RoleConfig();
        
        assertThat(config.isAList()).isFalse();
        assertThat(config.list()).isNull();
        assertThat(config.customRole()).isNull();
    }

    @Test
    void shouldCreateRoleConfigFromStringList() {
        List<String> hosts = List.of("192.168.1.10", "192.168.1.11");
        RoleConfig config = new RoleConfig(hosts);
        
        assertThat(config.isAList()).isTrue();
        assertThat(config.list()).hasSize(2);
        assertThat(config.list().get(0).host()).isEqualTo("192.168.1.10");
        assertThat(config.list().get(1).host()).isEqualTo("192.168.1.11");
        assertThat(config.customRole()).isNull();
    }

    @Test
    void shouldCreateRoleConfigFromMapList() {
        List<Map<String, String>> hosts = List.of(
            Map.of("192.168.1.10", "web"),
            Map.of("192.168.1.11", "api")
        );
        RoleConfig config = new RoleConfig(hosts);
        
        assertThat(config.isAList()).isTrue();
        assertThat(config.list()).hasSize(2);
        assertThat(config.list().get(0).host()).isEqualTo("192.168.1.10");
        assertThat(config.list().get(0).tags()).containsExactly("web");
        assertThat(config.list().get(1).host()).isEqualTo("192.168.1.11");
        assertThat(config.list().get(1).tags()).containsExactly("api");
    }

    @Test
    void shouldCreateRoleConfigFromCustomRole() {
        CustomRoleConfig customRole = new CustomRoleConfig(
            List.of("192.168.1.10"),
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null
        );
        RoleConfig config = new RoleConfig(customRole);
        
        assertThat(config.isAList()).isFalse();
        assertThat(config.list()).isNull();
        assertThat(config.customRole()).isNotNull();
        assertThat(config.customRole()).isEqualTo(customRole);
    }

    @Test
    void shouldCreateRoleConfigFromMixedList() {
        List<Object> hosts = List.of(
            "192.168.1.10",
            Map.of("192.168.1.11", List.of("web", "api"))
        );
        RoleConfig config = new RoleConfig(hosts);
        
        assertThat(config.isAList()).isTrue();
        assertThat(config.list()).hasSize(2);
        assertThat(config.list().get(0).host()).isEqualTo("192.168.1.10");
        assertThat(config.list().get(0).tags()).isEmpty();
        assertThat(config.list().get(1).host()).isEqualTo("192.168.1.11");
        assertThat(config.list().get(1).tags()).containsExactly("web", "api");
    }
}
