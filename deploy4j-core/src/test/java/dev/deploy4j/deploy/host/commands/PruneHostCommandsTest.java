package dev.deploy4j.deploy.host.commands;

import dev.deploy4j.deploy.configuration.Configuration;
import dev.rebelcraft.cmd.Cmd;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("PruneHostCommands")
class PruneHostCommandsTest {

    @Mock
    private Configuration config;

    private PruneHostCommands pruneCommands;

    @BeforeEach
    void setUp() {
        pruneCommands = new PruneHostCommands(config);
        lenient().when(config.service()).thenReturn("myapp");
        lenient().when(config.healthcheckService()).thenReturn("myapp-healthcheck");
        lenient().when(config.repository()).thenReturn("myregistry/myapp");
        lenient().when(config.latestImage()).thenReturn("myregistry/myapp:latest");
    }

    @Test
    @DisplayName("should create command to prune dangling images")
    void shouldPruneDanglingImages() {
        // Act
        Cmd cmd = pruneCommands.danglingImages();

        // Assert
        assertThat(cmd.build())
                .contains("docker", "image", "prune")
                .contains("--force")
                .contains("--filter", "label=service=myapp");
        assertThat(cmd.description()).isEqualTo("dangling images");
    }

    @Test
    @DisplayName("should create command to prune tagged images")
    void shouldPruneTaggedImages() {
        // Act
        Cmd cmd = pruneCommands.taggedImages();

        // Assert
        String cmdStr = String.join(" ", cmd.build());
        assertThat(cmdStr)
                .contains("docker", "image", "ls")
                .contains("--filter", "label=service=myapp")
                .contains("--format")
                .contains("grep", "-v", "-w")
                .contains("while read image tag; do docker rmi $tag; done");
        // Note: description is empty for piped commands
    }

    @Test
    @DisplayName("should create command to prune app containers")
    void shouldPruneAppContainers() {
        // Act
        Cmd cmd = pruneCommands.appContainers(5);

        // Assert
        String cmdStr = String.join(" ", cmd.build());
        assertThat(cmdStr)
                .contains("docker", "ps", "-q", "-a")
                .contains("--filter", "label=service=myapp")
                .contains("--filter", "status=created")
                .contains("--filter", "status=exited")
                .contains("--filter", "status=dead")
                .contains("tail", "-n", "+6") // retain + 1
                .contains("while read container_id; do docker rm $container_id; done");
        assertThat(cmd.description()).isEqualTo("app containers");
    }

    @Test
    @DisplayName("should create command to prune healthcheck containers")
    void shouldPruneHealthcheckContainers() {
        // Act
        Cmd cmd = pruneCommands.healthcheckContainers();

        // Assert
        assertThat(cmd.build())
                .contains("docker", "container", "prune")
                .contains("--force")
                .contains("--filter", "label=service=myapp-healthcheck");
        assertThat(cmd.description()).isEqualTo("healthcheck containers");
    }

    @Test
    @DisplayName("should handle different retain count")
    void shouldHandleDifferentRetainCount() {
        // Act
        Cmd cmd = pruneCommands.appContainers(10);

        // Assert
        String cmdStr = String.join(" ", cmd.build());
        assertThat(cmdStr).contains("tail", "-n", "+11"); // 10 + 1
    }

    @Test
    @DisplayName("should use service name in filter")
    void shouldUseServiceNameInFilter() {
        // Arrange
        when(config.service()).thenReturn("another-app");

        // Act
        Cmd cmd = pruneCommands.danglingImages();

        // Assert
        assertThat(cmd.build())
                .contains("--filter", "label=service=another-app");
    }
}
