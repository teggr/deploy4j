package dev.deploy4j.deploy.host.commands;

import dev.deploy4j.deploy.configuration.Configuration;
import dev.rebelcraft.cmd.Cmd;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuditorHostCommands")
class AuditorHostCommandsTest {

    @Mock
    private Configuration config;

    private AuditorHostCommands auditorCommands;

    @BeforeEach
    void setUp() {
        lenient().when(config.runDirectory()).thenReturn("/home/deploy");
        lenient().when(config.service()).thenReturn("myapp");
        lenient().when(config.destination()).thenReturn("production");
        
        Map<String, String> details = Map.of(
                "deployer", "john.doe",
                "version", "1.2.3"
        );
        auditorCommands = new AuditorHostCommands(config, details);
    }

    @Test
    @DisplayName("should create record command with audit line")
    void shouldCreateRecordCommand() {
        // Act
        Cmd cmd = auditorCommands.record("Deployment started");

        // Assert
        String cmdStr = String.join(" ", cmd.build());
        assertThat(cmdStr)
                .contains("echo")
                .contains("Deployment started")
                .contains(">>")
                .contains("/home/deploy/myapp-production-audit.log");
    }

    @Test
    @DisplayName("should create record command with additional details")
    void shouldCreateRecordCommandWithDetails() {
        // Arrange
        Map<String, String> additionalDetails = Map.of("status", "success");

        // Act
        Cmd cmd = auditorCommands.record("Deployment completed", additionalDetails);

        // Assert
        String cmdStr = String.join(" ", cmd.build());
        assertThat(cmdStr)
                .contains("echo")
                .contains("Deployment completed")
                .contains("/home/deploy/myapp-production-audit.log");
    }

    @Test
    @DisplayName("should create reveal command to show audit log")
    void shouldCreateRevealCommand() {
        // Act
        Cmd cmd = auditorCommands.reveal();

        // Assert
        assertThat(cmd.build())
                .containsExactly("tail", "-n", "50", "/home/deploy/myapp-production-audit.log");
    }

    @Test
    @DisplayName("should include service and destination in audit log filename")
    void shouldIncludeServiceAndDestinationInFilename() {
        // Act
        Cmd cmd = auditorCommands.reveal();

        // Assert
        assertThat(cmd.build())
                .contains("/home/deploy/myapp-production-audit.log");
    }

    @Test
    @DisplayName("should handle null destination in audit log filename")
    void shouldHandleNullDestination() {
        // Arrange
        when(config.destination()).thenReturn(null);
        auditorCommands = new AuditorHostCommands(config, Map.of());

        // Act
        Cmd cmd = auditorCommands.reveal();

        // Assert
        String cmdStr = String.join(" ", cmd.build());
        assertThat(cmdStr)
                .contains("/home/deploy/")
                .contains("audit.log");
    }

    @Test
    @DisplayName("should return details map")
    void shouldReturnDetailsMap() {
        // Act
        Map<String, String> details = auditorCommands.details();

        // Assert
        assertThat(details)
                .containsEntry("deployer", "john.doe")
                .containsEntry("version", "1.2.3");
    }
}
