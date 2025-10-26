package dev.deploy4j.deploy.configuration.raw;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("RoleConfig")
class RoleConfigTest {

    private final ObjectMapper mapper = new ObjectMapper(new YAMLFactory());

    @Test
    @DisplayName("should create config from list of string hosts")
    void shouldCreateConfigFromListOfStringHosts() {
        List<String> hosts = List.of("192.168.1.10", "192.168.1.11", "192.168.1.12");
        
        RoleConfig config = new RoleConfig(hosts);

        assertThat(config.isAList()).isTrue();
        assertThat(config.list()).hasSize(3);
        assertThat(config.list().get(0).host()).isEqualTo("192.168.1.10");
        assertThat(config.list().get(1).host()).isEqualTo("192.168.1.11");
        assertThat(config.list().get(2).host()).isEqualTo("192.168.1.12");
        assertThat(config.customRole()).isNull();
    }

    @Test
    @DisplayName("should create config from custom role")
    void shouldCreateConfigFromCustomRole() {
        CustomRoleConfig customRole = new CustomRoleConfig();
        
        RoleConfig config = new RoleConfig(customRole);

        assertThat(config.isAList()).isFalse();
        assertThat(config.list()).isNull();
        assertThat(config.customRole()).isNotNull();
    }

    @Test
    @DisplayName("should create empty config with default constructor")
    void shouldCreateEmptyConfigWithDefaultConstructor() {
        RoleConfig config = new RoleConfig();

        assertThat(config.isAList()).isFalse();
        assertThat(config.list()).isNull();
        assertThat(config.customRole()).isNull();
    }

    @Test
    @DisplayName("should deserialize list of plain hosts from YAML")
    void shouldDeserializeListOfPlainHostsFromYaml() throws Exception {
        String yaml = """
            - 192.168.1.10
            - 192.168.1.11
            """;

        RoleConfig config = mapper.readValue(yaml, RoleConfig.class);

        assertThat(config.isAList()).isTrue();
        assertThat(config.list()).hasSize(2);
        assertThat(config.list().get(0).host()).isEqualTo("192.168.1.10");
        assertThat(config.list().get(1).host()).isEqualTo("192.168.1.11");
    }

    @Test
    @DisplayName("should deserialize list of hosts with tags from YAML")
    void shouldDeserializeListOfHostsWithTagsFromYaml() throws Exception {
        String yaml = """
            - 192.168.1.10
            - 192.168.1.11: web
            - 192.168.1.12:
                - web
                - primary
            """;

        RoleConfig config = mapper.readValue(yaml, RoleConfig.class);

        assertThat(config.isAList()).isTrue();
        assertThat(config.list()).hasSize(3);
        assertThat(config.list().get(0).host()).isEqualTo("192.168.1.10");
        assertThat(config.list().get(0).tags()).isEmpty();
        assertThat(config.list().get(1).host()).isEqualTo("192.168.1.11");
        assertThat(config.list().get(1).tags()).containsExactly("web");
        assertThat(config.list().get(2).host()).isEqualTo("192.168.1.12");
        assertThat(config.list().get(2).tags()).containsExactly("web", "primary");
    }
}
