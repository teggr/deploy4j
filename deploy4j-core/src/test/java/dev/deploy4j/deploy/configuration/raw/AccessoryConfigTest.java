package dev.deploy4j.deploy.configuration.raw;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("AccessoryConfig")
class AccessoryConfigTest {

    private final ObjectMapper mapper = new ObjectMapper(new YAMLFactory());

    @Test
    @DisplayName("should deserialize from YAML with all fields")
    void shouldDeserializeFromYamlWithAllFields() throws Exception {
        String yaml = """
            service: redis
            image: redis:7-alpine
            host: 192.168.1.10
            hosts:
              - 192.168.1.10
              - 192.168.1.11
            roles:
              - web
              - worker
            cmd: redis-server --appendonly yes
            port: "6379"
            labels:
              app: cache
              tier: data
            options:
              network: bridge
              restart: always
            env:
              clear:
                REDIS_MODE: standalone
            files:
              - /config/redis.conf
            directories:
              - /data
            volumes:
              - redis-data:/data
            """;

        AccessoryConfig config = mapper.readValue(yaml, AccessoryConfig.class);

        assertThat(config.service()).isEqualTo("redis");
        assertThat(config.image()).isEqualTo("redis:7-alpine");
        assertThat(config.host()).isEqualTo("192.168.1.10");
        assertThat(config.hosts()).containsExactly("192.168.1.10", "192.168.1.11");
        assertThat(config.roles()).containsExactly("web", "worker");
        assertThat(config.cmd()).isEqualTo("redis-server --appendonly yes");
        assertThat(config.port()).isEqualTo("6379");
        assertThat(config.labels()).containsEntry("app", "cache").containsEntry("tier", "data");
        assertThat(config.options()).containsEntry("network", "bridge").containsEntry("restart", "always");
        assertThat(config.env().clear()).containsEntry("REDIS_MODE", "standalone");
        assertThat(config.files()).containsExactly("/config/redis.conf");
        assertThat(config.directories()).containsExactly("/data");
        assertThat(config.volumes()).containsExactly("redis-data:/data");
    }

    @Test
    @DisplayName("should deserialize from YAML with minimal fields")
    void shouldDeserializeFromYamlWithMinimalFields() throws Exception {
        String yaml = """
            service: postgres
            image: postgres:15
            """;

        AccessoryConfig config = mapper.readValue(yaml, AccessoryConfig.class);

        assertThat(config.service()).isEqualTo("postgres");
        assertThat(config.image()).isEqualTo("postgres:15");
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
    @DisplayName("should create config programmatically")
    void shouldCreateConfigProgrammatically() {
        AccessoryConfig config = new AccessoryConfig(
            "mysql",
            "mysql:8",
            "192.168.1.20",
            List.of("192.168.1.20"),
            List.of("db"),
            "mysqld",
            "3306",
            Map.of("tier", "database"),
            Map.of("restart", "unless-stopped"),
            new EnvironmentConfig(Map.of("MYSQL_ROOT_PASSWORD", "secret"), List.of(), null, null),
            List.of("/etc/mysql/my.cnf"),
            List.of("/var/lib/mysql"),
            List.of("mysql-data:/var/lib/mysql")
        );

        assertThat(config.service()).isEqualTo("mysql");
        assertThat(config.image()).isEqualTo("mysql:8");
        assertThat(config.host()).isEqualTo("192.168.1.20");
        assertThat(config.hosts()).containsExactly("192.168.1.20");
        assertThat(config.roles()).containsExactly("db");
        assertThat(config.cmd()).isEqualTo("mysqld");
        assertThat(config.port()).isEqualTo("3306");
        assertThat(config.labels()).containsEntry("tier", "database");
        assertThat(config.options()).containsEntry("restart", "unless-stopped");
        assertThat(config.env().clear()).containsEntry("MYSQL_ROOT_PASSWORD", "secret");
        assertThat(config.files()).containsExactly("/etc/mysql/my.cnf");
        assertThat(config.directories()).containsExactly("/var/lib/mysql");
        assertThat(config.volumes()).containsExactly("mysql-data:/var/lib/mysql");
    }

    @Test
    @DisplayName("should handle port as string")
    void shouldHandlePortAsString() throws Exception {
        String yaml = """
            service: nginx
            image: nginx:latest
            port: "8080:80"
            """;

        AccessoryConfig config = mapper.readValue(yaml, AccessoryConfig.class);

        assertThat(config.port()).isEqualTo("8080:80");
    }
}
