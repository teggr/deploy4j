package dev.deploy4j.deploy.host.ssh;

import dev.rebelcraft.cmd.Cmd;
import dev.rebelcraft.ssh.ExecResult;
import dev.rebelcraft.ssh.SSHTemplate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("SshHost")
class SshHostTest {

    @Mock
    private SSHTemplate sshTemplate;

    private SshHost sshHost;

    @BeforeEach
    void setUp() {
        sshHost = new SshHost("test-host", sshTemplate);
    }

    @Test
    @DisplayName("should return host name")
    void shouldReturnHostName() {
        // Act
        String hostName = sshHost.hostName();

        // Assert
        assertThat(hostName).isEqualTo("test-host");
    }

    @Test
    @DisplayName("should execute command successfully")
    void shouldExecuteCommandSuccessfully() {
        // Arrange
        ExecResult successResult = new ExecResult(0, "output", "");
        when(sshTemplate.exec(anyString())).thenReturn(successResult);
        Cmd cmd = Cmd.cmd("echo", "hello");

        // Act
        boolean result = sshHost.execute(cmd);

        // Assert
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("should throw exception on non-zero exit with raise flag")
    void shouldThrowExceptionOnNonZeroExitWithRaiseFlag() {
        // Arrange
        ExecResult failureResult = new ExecResult(1, "", "error message");
        when(sshTemplate.exec(anyString())).thenReturn(failureResult);
        Cmd cmd = Cmd.cmd("failing-command");

        // Act & Assert
        assertThatThrownBy(() -> sshHost.execute(cmd, true))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("Command failed on host test-host");
    }

    @Test
    @DisplayName("should not throw exception on non-zero exit without raise flag")
    void shouldNotThrowExceptionOnNonZeroExitWithoutRaiseFlag() {
        // Arrange
        ExecResult failureResult = new ExecResult(1, "", "error message");
        when(sshTemplate.exec(anyString())).thenReturn(failureResult);
        Cmd cmd = Cmd.cmd("failing-command");

        // Act
        boolean result = sshHost.execute(cmd, false);

        // Assert
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("should capture command output successfully")
    void shouldCaptureCommandOutputSuccessfully() {
        // Arrange
        ExecResult result = new ExecResult(0, "captured output", "");
        when(sshTemplate.exec(anyString())).thenReturn(result);
        Cmd cmd = Cmd.cmd("ls");

        // Act
        String output = sshHost.capture(cmd);

        // Assert
        assertThat(output).isEqualTo("captured output");
    }

    @Test
    @DisplayName("should capture string command output successfully")
    void shouldCaptureStringCommandOutputSuccessfully() {
        // Arrange
        ExecResult result = new ExecResult(0, "string output", "");
        when(sshTemplate.exec(anyString())).thenReturn(result);

        // Act
        String output = sshHost.capture("echo test");

        // Assert
        assertThat(output).isEqualTo("string output");
    }

    @Test
    @DisplayName("should throw exception when capture fails with raise flag")
    void shouldThrowExceptionWhenCaptureFailsWithRaiseFlag() {
        // Arrange
        ExecResult failureResult = new ExecResult(1, "", "error");
        when(sshTemplate.exec(anyString())).thenReturn(failureResult);
        Cmd cmd = Cmd.cmd("failing-command");

        // Act & Assert
        assertThatThrownBy(() -> sshHost.capture(cmd, true))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("Command failed on host test-host");
    }

    @Test
    @DisplayName("should not throw exception when capture fails without raise flag")
    void shouldNotThrowExceptionWhenCaptureFailsWithoutRaiseFlag() {
        // Arrange
        ExecResult failureResult = new ExecResult(1, "", "error");
        when(sshTemplate.exec(anyString())).thenReturn(failureResult);
        Cmd cmd = Cmd.cmd("failing-command");

        // Act
        String output = sshHost.capture(cmd, false);

        // Assert
        assertThat(output).isEmpty();
    }
}
