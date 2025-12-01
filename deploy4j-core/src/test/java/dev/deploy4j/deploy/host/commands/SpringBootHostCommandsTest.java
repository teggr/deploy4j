package dev.deploy4j.deploy.host.commands;

import dev.deploy4j.deploy.configuration.Configuration;
import dev.deploy4j.deploy.configuration.DeployConfigBuilder;
import dev.deploy4j.deploy.configuration.raw.DeployConfig;
import dev.deploy4j.deploy.configuration.raw.SpringBootConfig;
import dev.rebelcraft.cmd.Cmd;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("SpringBootHostCommands")
class SpringBootHostCommandsTest {

    private static final String CONTAINER_NAME = "test-service-web-1.0.0";
    private SpringBootHostCommands springBootCommands;

    @BeforeEach
    void setUp() {
        // Create minimal configuration with spring boot settings
        SpringBootConfig springBootConfig = new SpringBootConfig(
            8081,
            "/actuator"
        );
        DeployConfig deployConfig = DeployConfigBuilder.minimal()
            .springBoot(springBootConfig)
            .build();
        Configuration configuration = new Configuration(deployConfig, null, null);
        springBootCommands = new SpringBootHostCommands(configuration, CONTAINER_NAME);
    }

    @Test
    @DisplayName("should create pull curl image command")
    void shouldCreatePullCurlImageCommand() {
        // Act
        Cmd cmd = springBootCommands.pullCurlImage();

        // Assert
        assertThat(cmd.build())
            .contains("docker", "pull", "curlimages/curl");
        assertThat(cmd.description()).isEqualTo("pull curl image");
    }

    @Test
    @DisplayName("should create health command using docker run with curl container")
    void shouldCreateHealthCommand() {
        // Act
        Cmd cmd = springBootCommands.health();

        // Assert
        assertThat(cmd.build())
            .contains("docker", "run", "--rm", "--network", "deploy4j", "curlimages/curl", "-s", 
                "http://" + CONTAINER_NAME + ":8081/actuator/health");
        assertThat(cmd.description()).isEqualTo("actuator health");
    }

    @Test
    @DisplayName("should create info command")
    void shouldCreateInfoCommand() {
        // Act
        Cmd cmd = springBootCommands.info();

        // Assert
        assertThat(cmd.build())
            .contains("docker", "run", "--rm", "--network", "deploy4j", "curlimages/curl", "-s",
                "http://" + CONTAINER_NAME + ":8081/actuator/info");
        assertThat(cmd.description()).isEqualTo("actuator info");
    }

    @Test
    @DisplayName("should create env command")
    void shouldCreateEnvCommand() {
        // Act
        Cmd cmd = springBootCommands.env();

        // Assert
        assertThat(cmd.build())
            .contains("http://" + CONTAINER_NAME + ":8081/actuator/env");
        assertThat(cmd.description()).isEqualTo("actuator env");
    }

    @Test
    @DisplayName("should create loggers command")
    void shouldCreateLoggersCommand() {
        // Act
        Cmd cmd = springBootCommands.loggers();

        // Assert
        assertThat(cmd.build())
            .contains("http://" + CONTAINER_NAME + ":8081/actuator/loggers");
        assertThat(cmd.description()).isEqualTo("actuator loggers");
    }

    @Test
    @DisplayName("should create metrics command")
    void shouldCreateMetricsCommand() {
        // Act
        Cmd cmd = springBootCommands.metrics();

        // Assert
        assertThat(cmd.build())
            .contains("http://" + CONTAINER_NAME + ":8081/actuator/metrics");
        assertThat(cmd.description()).isEqualTo("actuator metrics");
    }

    @Test
    @DisplayName("should create metrics command with specific metric name")
    void shouldCreateMetricsCommandWithName() {
        // Act
        Cmd cmd = springBootCommands.metrics("jvm.memory.used");

        // Assert
        assertThat(cmd.build())
            .contains("http://" + CONTAINER_NAME + ":8081/actuator/metrics/jvm.memory.used");
        assertThat(cmd.description()).isEqualTo("actuator metrics jvm.memory.used");
    }

    @Test
    @DisplayName("should create threaddump command")
    void shouldCreateThreaddumpCommand() {
        // Act
        Cmd cmd = springBootCommands.threaddump();

        // Assert
        assertThat(cmd.build())
            .contains("http://" + CONTAINER_NAME + ":8081/actuator/threaddump");
        assertThat(cmd.description()).isEqualTo("actuator threaddump");
    }

    @Test
    @DisplayName("should create heapdump command")
    void shouldCreateHeapdumpCommand() {
        // Act
        Cmd cmd = springBootCommands.heapdump();

        // Assert
        assertThat(cmd.build())
            .contains("http://" + CONTAINER_NAME + ":8081/actuator/heapdump");
        assertThat(cmd.description()).isEqualTo("actuator heapdump");
    }

    @Test
    @DisplayName("should create scheduledtasks command")
    void shouldCreateScheduledtasksCommand() {
        // Act
        Cmd cmd = springBootCommands.scheduledtasks();

        // Assert
        assertThat(cmd.build())
            .contains("http://" + CONTAINER_NAME + ":8081/actuator/scheduledtasks");
        assertThat(cmd.description()).isEqualTo("actuator scheduledtasks");
    }

    @Test
    @DisplayName("should create httpexchanges command")
    void shouldCreateHttpexchangesCommand() {
        // Act
        Cmd cmd = springBootCommands.httpexchanges();

        // Assert
        assertThat(cmd.build())
            .contains("http://" + CONTAINER_NAME + ":8081/actuator/httpexchanges");
        assertThat(cmd.description()).isEqualTo("actuator httpexchanges");
    }

    @Test
    @DisplayName("should create beans command")
    void shouldCreateBeansCommand() {
        // Act
        Cmd cmd = springBootCommands.beans();

        // Assert
        assertThat(cmd.build())
            .contains("http://" + CONTAINER_NAME + ":8081/actuator/beans");
        assertThat(cmd.description()).isEqualTo("actuator beans");
    }

    @Test
    @DisplayName("should create conditions command")
    void shouldCreateConditionsCommand() {
        // Act
        Cmd cmd = springBootCommands.conditions();

        // Assert
        assertThat(cmd.build())
            .contains("http://" + CONTAINER_NAME + ":8081/actuator/conditions");
        assertThat(cmd.description()).isEqualTo("actuator conditions");
    }

    @Test
    @DisplayName("should create configprops command")
    void shouldCreateConfigpropsCommand() {
        // Act
        Cmd cmd = springBootCommands.configprops();

        // Assert
        assertThat(cmd.build())
            .contains("http://" + CONTAINER_NAME + ":8081/actuator/configprops");
        assertThat(cmd.description()).isEqualTo("actuator configprops");
    }

    @Test
    @DisplayName("should create mappings command")
    void shouldCreateMappingsCommand() {
        // Act
        Cmd cmd = springBootCommands.mappings();

        // Assert
        assertThat(cmd.build())
            .contains("http://" + CONTAINER_NAME + ":8081/actuator/mappings");
        assertThat(cmd.description()).isEqualTo("actuator mappings");
    }

    @Test
    @DisplayName("should create shutdown command with POST method")
    void shouldCreateShutdownCommand() {
        // Act
        Cmd cmd = springBootCommands.shutdown();

        // Assert
        assertThat(cmd.build())
            .contains("docker", "run", "--rm", "--network", "deploy4j", "curlimages/curl", 
                "-s", "-X", "POST", "http://" + CONTAINER_NAME + ":8081/actuator/shutdown");
        assertThat(cmd.description()).isEqualTo("actuator shutdown");
    }

    @Test
    @DisplayName("should create caches command")
    void shouldCreateCachesCommand() {
        // Act
        Cmd cmd = springBootCommands.caches();

        // Assert
        assertThat(cmd.build())
            .contains("http://" + CONTAINER_NAME + ":8081/actuator/caches");
        assertThat(cmd.description()).isEqualTo("actuator caches");
    }

    @Test
    @DisplayName("should create flyway command")
    void shouldCreateFlywayCommand() {
        // Act
        Cmd cmd = springBootCommands.flyway();

        // Assert
        assertThat(cmd.build())
            .contains("http://" + CONTAINER_NAME + ":8081/actuator/flyway");
        assertThat(cmd.description()).isEqualTo("actuator flyway");
    }

    @Test
    @DisplayName("should create liquibase command")
    void shouldCreateLiquibaseCommand() {
        // Act
        Cmd cmd = springBootCommands.liquibase();

        // Assert
        assertThat(cmd.build())
            .contains("http://" + CONTAINER_NAME + ":8081/actuator/liquibase");
        assertThat(cmd.description()).isEqualTo("actuator liquibase");
    }

    @Test
    @DisplayName("should create sessions command")
    void shouldCreateSessionsCommand() {
        // Act
        Cmd cmd = springBootCommands.sessions();

        // Assert
        assertThat(cmd.build())
            .contains("http://" + CONTAINER_NAME + ":8081/actuator/sessions");
        assertThat(cmd.description()).isEqualTo("actuator sessions");
    }

    @Test
    @DisplayName("should create startup command")
    void shouldCreateStartupCommand() {
        // Act
        Cmd cmd = springBootCommands.startup();

        // Assert
        assertThat(cmd.build())
            .contains("http://" + CONTAINER_NAME + ":8081/actuator/startup");
        assertThat(cmd.description()).isEqualTo("actuator startup");
    }

    @Test
    @DisplayName("should create generic endpoint command")
    void shouldCreateGenericEndpointCommand() {
        // Act
        Cmd cmd = springBootCommands.endpoint("custom/endpoint");

        // Assert
        assertThat(cmd.build())
            .contains("http://" + CONTAINER_NAME + ":8081/actuator/custom/endpoint");
        assertThat(cmd.description()).isEqualTo("actuator custom/endpoint");
    }

    @Test
    @DisplayName("should use default port when not configured")
    void shouldUseDefaultPortWhenNotConfigured() {
        // Create config without custom port
        DeployConfig deployConfig = DeployConfigBuilder.minimal()
            .springBoot(null)
            .build();
        Configuration configuration = new Configuration(deployConfig, null, null);
        SpringBootHostCommands commands = new SpringBootHostCommands(configuration, CONTAINER_NAME);

        // Act
        Cmd cmd = commands.health();

        // Assert
        assertThat(cmd.build())
            .contains("http://" + CONTAINER_NAME + ":8080/actuator/health");
    }

    @Test
    @DisplayName("should use default base path when not configured")
    void shouldUseDefaultBasePathWhenNotConfigured() {
        // Create config without custom base path
        SpringBootConfig springBootConfig = new SpringBootConfig(
            9090,
            null
        );
        DeployConfig deployConfig = DeployConfigBuilder.minimal()
            .springBoot(springBootConfig)
            .build();
        Configuration configuration = new Configuration(deployConfig, null, null);
        SpringBootHostCommands commands = new SpringBootHostCommands(configuration, CONTAINER_NAME);

        // Act
        Cmd cmd = commands.health();

        // Assert
        assertThat(cmd.build())
            .contains("http://" + CONTAINER_NAME + ":9090/actuator/health");
    }

    @Test
    @DisplayName("should handle custom base path without leading slash")
    void shouldHandleCustomBasePathWithoutLeadingSlash() {
        SpringBootConfig springBootConfig = new SpringBootConfig(
            8080,
            "management"
        );
        DeployConfig deployConfig = DeployConfigBuilder.minimal()
            .springBoot(springBootConfig)
            .build();
        Configuration configuration = new Configuration(deployConfig, null, null);
        SpringBootHostCommands commands = new SpringBootHostCommands(configuration, CONTAINER_NAME);

        // Act
        Cmd cmd = commands.health();

        // Assert
        assertThat(cmd.build())
            .contains("http://" + CONTAINER_NAME + ":8080/management/health");
    }
}
