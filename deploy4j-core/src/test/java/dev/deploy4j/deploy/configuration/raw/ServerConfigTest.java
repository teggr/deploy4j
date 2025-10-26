package dev.deploy4j.deploy.configuration.raw;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class ServerConfigTest {

    @Test
    void shouldCreateServerConfigFromString() {
        ServerConfig config = new ServerConfig("192.168.1.10");
        
        assertThat(config.host()).isEqualTo("192.168.1.10");
        assertThat(config.tags()).isEmpty();
    }

    @Test
    void shouldCreateServerConfigFromMapWithSingleTag() {
        Map<String, String> hostWithTag = Map.of("192.168.1.10", "web");
        ServerConfig config = new ServerConfig(hostWithTag);
        
        assertThat(config.host()).isEqualTo("192.168.1.10");
        assertThat(config.tags()).containsExactly("web");
    }

    @Test
    void shouldCreateServerConfigFromMapWithMultipleTags() {
        Map<String, List<String>> hostWithTags = Map.of("192.168.1.10", List.of("web", "api"));
        ServerConfig config = new ServerConfig(hostWithTags);
        
        assertThat(config.host()).isEqualTo("192.168.1.10");
        assertThat(config.tags()).containsExactly("web", "api");
    }

    @Test
    void shouldHandleEmptyTagsList() {
        Map<String, List<String>> hostWithTags = Map.of("192.168.1.10", List.of());
        ServerConfig config = new ServerConfig(hostWithTags);
        
        assertThat(config.host()).isEqualTo("192.168.1.10");
        assertThat(config.tags()).isEmpty();
    }

    @Test
    void shouldHandleMapWithoutTags() {
        Map<String, Object> hostMap = Map.of("192.168.1.10", new Object());
        ServerConfig config = new ServerConfig(hostMap);
        
        assertThat(config.host()).isEqualTo("192.168.1.10");
        assertThat(config.tags()).isEmpty();
    }
}
