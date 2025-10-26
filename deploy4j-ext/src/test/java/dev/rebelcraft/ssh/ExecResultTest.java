package dev.rebelcraft.ssh;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("ExecResult")
class ExecResultTest {

    @Test
    @DisplayName("should create record with all fields")
    void shouldCreateRecordWithAllFields() {
        // Act
        ExecResult result = new ExecResult(0, "output text", "error text");

        // Assert
        assertThat(result.exitStatus()).isEqualTo(0);
        assertThat(result.execOutput()).isEqualTo("output text");
        assertThat(result.execErrorOutput()).isEqualTo("error text");
    }

    @Test
    @DisplayName("should handle success exit status")
    void shouldHandleSuccessExitStatus() {
        // Act
        ExecResult result = new ExecResult(0, "success", "");

        // Assert
        assertThat(result.exitStatus()).isZero();
    }

    @Test
    @DisplayName("should handle failure exit status")
    void shouldHandleFailureExitStatus() {
        // Act
        ExecResult result = new ExecResult(1, "", "error message");

        // Assert
        assertThat(result.exitStatus()).isNotZero();
        assertThat(result.execErrorOutput()).isEqualTo("error message");
    }

    @Test
    @DisplayName("should handle empty output and error strings")
    void shouldHandleEmptyOutputs() {
        // Act
        ExecResult result = new ExecResult(0, "", "");

        // Assert
        assertThat(result.execOutput()).isEmpty();
        assertThat(result.execErrorOutput()).isEmpty();
    }

    @Test
    @DisplayName("should support record equality")
    void shouldSupportRecordEquality() {
        // Arrange
        ExecResult result1 = new ExecResult(0, "output", "error");
        ExecResult result2 = new ExecResult(0, "output", "error");
        ExecResult result3 = new ExecResult(1, "output", "error");

        // Assert
        assertThat(result1).isEqualTo(result2);
        assertThat(result1).isNotEqualTo(result3);
    }

    @Test
    @DisplayName("should support toString")
    void shouldSupportToString() {
        // Arrange
        ExecResult result = new ExecResult(0, "output", "error");

        // Act
        String str = result.toString();

        // Assert
        assertThat(str)
                .contains("0")
                .contains("output")
                .contains("error");
    }
}
