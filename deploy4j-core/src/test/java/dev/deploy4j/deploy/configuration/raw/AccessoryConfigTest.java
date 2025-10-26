package dev.deploy4j.deploy.configuration.raw;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class AccessoryConfigTest {

    @Test
    void shouldCreateAccessoryConfigWithAllFields() {
        EnvironmentConfig env = new EnvironmentConfig();
        List<String> files = List.of("file1.txt", "file2.txt");
        List<String> directories = List.of("/opt/data", "/opt/logs");
        List<String> volumes = List.of("data:/data", "logs:/logs");
        Map<String, String> labels = Map.of("app", "redis");
        Map<String, String> options = Map.of("memory", "512m");
        
        AccessoryConfig config = new AccessoryConfig(
            "redis-service",
            "redis:7.0",
            "192.168.1.10",
            List.of("192.168.1.10", "192.168.1.11"),
            List.of("cache", "db"),
            "redis-server",
            "6379",
            labels,
            options,
            env,
            files,
            directories,
            volumes
        );
        
        assertThat(config.service()).isEqualTo("redis-service");
        assertThat(config.image()).isEqualTo("redis:7.0");
        assertThat(config.host()).isEqualTo("192.168.1.10");
        assertThat(config.hosts()).containsExactly("192.168.1.10", "192.168.1.11");
        assertThat(config.roles()).containsExactly("cache", "db");
        assertThat(config.cmd()).isEqualTo("redis-server");
        assertThat(config.port()).isEqualTo("6379");
        assertThat(config.labels()).isEqualTo(labels);
        assertThat(config.options()).isEqualTo(options);
        assertThat(config.env()).isEqualTo(env);
        assertThat(config.files()).isEqualTo(files);
        assertThat(config.directories()).isEqualTo(directories);
        assertThat(config.volumes()).isEqualTo(volumes);
    }

    @Test
    void shouldHandleMinimalConfiguration() {
        AccessoryConfig config = new AccessoryConfig(
            "my-service",
            "nginx:latest",
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null
        );
        
        assertThat(config.service()).isEqualTo("my-service");
        assertThat(config.image()).isEqualTo("nginx:latest");
        assertThat(config.host()).isNull();
        assertThat(config.hosts()).isNull();
        assertThat(config.roles()).isNull();
        assertThat(config.cmd()).isNull();
        assertThat(config.port()).isNull();
        assertThat(config.labels()).isNull();
        assertThat(config.options()).isNull();
        assertThat(config.env()).isNull();
        assertThat(config.files()).isNull();
        assertThat(config.directories()).isNull();
        assertThat(config.volumes()).isNull();
    }

    @Test
    void shouldHandleSingleHost() {
        AccessoryConfig config = new AccessoryConfig(
            "postgres",
            "postgres:14",
            "192.168.1.20",
            null,
            null,
            null,
            "5432",
            null,
            null,
            null,
            null,
            null,
            null
        );
        
        assertThat(config.service()).isEqualTo("postgres");
        assertThat(config.host()).isEqualTo("192.168.1.20");
        assertThat(config.port()).isEqualTo("5432");
    }

    @Test
    void shouldHandleMultipleHosts() {
        List<String> hosts = List.of("host1", "host2", "host3");
        
        AccessoryConfig config = new AccessoryConfig(
            "cluster-service",
            "app:latest",
            null,
            hosts,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null
        );
        
        assertThat(config.hosts()).hasSize(3);
        assertThat(config.hosts()).containsExactly("host1", "host2", "host3");
    }

    @Test
    void shouldHandleEmptyCollections() {
        AccessoryConfig config = new AccessoryConfig(
            "service",
            "image:latest",
            null,
            List.of(),
            List.of(),
            null,
            null,
            Map.of(),
            Map.of(),
            null,
            List.of(),
            List.of(),
            List.of()
        );
        
        assertThat(config.hosts()).isEmpty();
        assertThat(config.roles()).isEmpty();
        assertThat(config.labels()).isEmpty();
        assertThat(config.options()).isEmpty();
        assertThat(config.files()).isEmpty();
        assertThat(config.directories()).isEmpty();
        assertThat(config.volumes()).isEmpty();
    }
}
