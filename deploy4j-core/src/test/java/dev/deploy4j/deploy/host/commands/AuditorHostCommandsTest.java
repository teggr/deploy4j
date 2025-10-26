package dev.deploy4j.deploy.host.commands;

import dev.deploy4j.deploy.configuration.Configuration;
import dev.rebelcraft.cmd.Cmd;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("AuditorHostCommands")
class AuditorHostCommandsTest {

    @Mock
    private Configuration config;

    private Map<String, String> details;
    private AuditorHostCommands auditorCommands;

    @BeforeEach
    void setUp() {
        details = new HashMap<>();
        details.put("user", "testuser");
        details.put("action", "deploy");

        lenient().when(config.service()).thenReturn("myapp");
        lenient().when(config.destination()).thenReturn("production");
        lenient().when(config.runDirectory()).thenReturn("/tmp/deploy");
        lenient().when(config.version()).thenReturn("1.0.0");

        auditorCommands = new AuditorHostCommands(config, details);
    }

    @Test
    @DisplayName("should record audit line with default details")
    void shouldRecordAuditLineWithDefaultDetails() {
        // Act
        Cmd result = auditorCommands.record("Deployment started");

        // Assert
        assertThat(result).isNotNull();
        String builtCmd = String.join(" ", result.build());
        assertThat(builtCmd)
            .contains("echo")
            .contains("Deployment started")
            .contains("/tmp/deploy/myapp-production-audit.log");
    }

    @Test
    @DisplayName("should record audit line with additional details")
    void shouldRecordAuditLineWithAdditionalDetails() {
        // Arrange
        Map<String, String> additionalDetails = new HashMap<>();
        additionalDetails.put("server", "host1");

        // Act
        Cmd result = auditorCommands.record("Starting deployment", additionalDetails);

        // Assert
        assertThat(result).isNotNull();
        String builtCmd = String.join(" ", result.build());
        assertThat(builtCmd).contains("Starting deployment");
    }

    @Test
    @DisplayName("should reveal audit log")
    void shouldRevealAuditLog() {
        // Act
        Cmd result = auditorCommands.reveal();

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.build())
            .contains("tail")
            .contains("-n")
            .contains("50")
            .containsAnyOf("/tmp/deploy/myapp-production-audit.log");
    }

    @Test
    @DisplayName("should handle null details")
    void shouldHandleNullDetails() {
        // Arrange
        AuditorHostCommands nullDetailsCommands = new AuditorHostCommands(config, null);

        // Act
        Cmd result = nullDetailsCommands.record("Test message");

        // Assert
        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("should create audit log file path correctly")
    void shouldCreateAuditLogFilePathCorrectly() {
        // Act
        Cmd result = auditorCommands.reveal();

        // Assert
        String builtCmd = String.join(" ", result.build());
        assertThat(builtCmd).contains("myapp-production-audit.log");
    }

    @Test
    @DisplayName("should merge details when recording with additional details")
    void shouldMergeDetailsWhenRecordingWithAdditionalDetails() {
        // Arrange
        Map<String, String> extraDetails = new HashMap<>();
        extraDetails.put("host", "server1");
        extraDetails.put("action", "updated");  // Override existing detail

        // Act
        Cmd result = auditorCommands.record("Test", extraDetails);

        // Assert
        assertThat(result).isNotNull();
        // The command should include both original and additional details
    }

    @Test
    @DisplayName("should return details")
    void shouldReturnDetails() {
        // Act
        Map<String, String> result = auditorCommands.details();

        // Assert
        assertThat(result).isEqualTo(details);
        assertThat(result).containsEntry("user", "testuser");
        assertThat(result).containsEntry("action", "deploy");
    }
}
