package dev.deploy4j.deploy.host.ssh;

import dev.deploy4j.deploy.configuration.Ssh;
import dev.rebelcraft.cmd.Cmd;
import dev.rebelcraft.ssh.ExecResult;
import dev.rebelcraft.ssh.SSHTemplate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("SshHost")
class SshHostTest {

    @Mock
    private Ssh ssh;

    private SshHost sshHost;

    @BeforeEach
    void setUp() {
        when(ssh.user()).thenReturn("testuser");
        when(ssh.port()).thenReturn(22);
        when(ssh.keyPath()).thenReturn("/path/to/key");
        when(ssh.keyPassphrase()).thenReturn("passphrase");
        when(ssh.strictHostKeyChecking()).thenReturn(true);
    }

    @Test
    @DisplayName("should return host name")
    void shouldReturnHostName() {
        // Arrange
        try (MockedConstruction<SSHTemplate> mocked = mockConstruction(SSHTemplate.class)) {
            sshHost = new SshHost("test-host", ssh);

            // Act
            String hostName = sshHost.hostName();

            // Assert
            assertThat(hostName).isEqualTo("test-host");
        }
    }

    @Test
    @DisplayName("should execute command successfully")
    void shouldExecuteCommandSuccessfully() {
        // Arrange
        ExecResult successResult = new ExecResult(0, "output", "");

        try (MockedConstruction<SSHTemplate> mocked = mockConstruction(SSHTemplate.class,
                (mock, context) -> when(mock.exec(anyString())).thenReturn(successResult))) {
            sshHost = new SshHost("test-host", ssh);
            Cmd cmd = Cmd.cmd("echo", "hello");

            // Act
            boolean result = sshHost.execute(cmd);

            // Assert
            assertThat(result).isTrue();
        }
    }

    @Test
    @DisplayName("should throw exception on non-zero exit with raise flag")
    void shouldThrowExceptionOnNonZeroExitWithRaiseFlag() {
        // Arrange
        ExecResult failureResult = new ExecResult(1, "", "error message");

        try (MockedConstruction<SSHTemplate> mocked = mockConstruction(SSHTemplate.class,
                (mock, context) -> when(mock.exec(anyString())).thenReturn(failureResult))) {
            sshHost = new SshHost("test-host", ssh);
            Cmd cmd = Cmd.cmd("failing-command");

            // Act & Assert
            assertThatThrownBy(() -> sshHost.execute(cmd, true))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Command failed on host test-host");
        }
    }

    @Test
    @DisplayName("should not throw exception on non-zero exit without raise flag")
    void shouldNotThrowExceptionOnNonZeroExitWithoutRaiseFlag() {
        // Arrange
        ExecResult failureResult = new ExecResult(1, "", "error message");

        try (MockedConstruction<SSHTemplate> mocked = mockConstruction(SSHTemplate.class,
                (mock, context) -> when(mock.exec(anyString())).thenReturn(failureResult))) {
            sshHost = new SshHost("test-host", ssh);
            Cmd cmd = Cmd.cmd("failing-command");

            // Act
            boolean result = sshHost.execute(cmd, false);

            // Assert
            assertThat(result).isFalse();
        }
    }

    @Test
    @DisplayName("should capture command output successfully")
    void shouldCaptureCommandOutputSuccessfully() {
        // Arrange
        ExecResult result = new ExecResult(0, "captured output", "");

        try (MockedConstruction<SSHTemplate> mocked = mockConstruction(SSHTemplate.class,
                (mock, context) -> when(mock.exec(anyString())).thenReturn(result))) {
            sshHost = new SshHost("test-host", ssh);
            Cmd cmd = Cmd.cmd("ls");

            // Act
            String output = sshHost.capture(cmd);

            // Assert
            assertThat(output).isEqualTo("captured output");
        }
    }

    @Test
    @DisplayName("should capture string command output successfully")
    void shouldCaptureStringCommandOutputSuccessfully() {
        // Arrange
        ExecResult result = new ExecResult(0, "string output", "");

        try (MockedConstruction<SSHTemplate> mocked = mockConstruction(SSHTemplate.class,
                (mock, context) -> when(mock.exec(anyString())).thenReturn(result))) {
            sshHost = new SshHost("test-host", ssh);

            // Act
            String output = sshHost.capture("echo test");

            // Assert
            assertThat(output).isEqualTo("string output");
        }
    }

    @Test
    @DisplayName("should throw exception when capture fails with raise flag")
    void shouldThrowExceptionWhenCaptureFailsWithRaiseFlag() {
        // Arrange
        ExecResult failureResult = new ExecResult(1, "", "error");

        try (MockedConstruction<SSHTemplate> mocked = mockConstruction(SSHTemplate.class,
                (mock, context) -> when(mock.exec(anyString())).thenReturn(failureResult))) {
            sshHost = new SshHost("test-host", ssh);
            Cmd cmd = Cmd.cmd("failing-command");

            // Act & Assert
            assertThatThrownBy(() -> sshHost.capture(cmd, true))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Command failed on host test-host");
        }
    }

    @Test
    @DisplayName("should not throw exception when capture fails without raise flag")
    void shouldNotThrowExceptionWhenCaptureFailsWithoutRaiseFlag() {
        // Arrange
        ExecResult failureResult = new ExecResult(1, "", "error");

        try (MockedConstruction<SSHTemplate> mocked = mockConstruction(SSHTemplate.class,
                (mock, context) -> when(mock.exec(anyString())).thenReturn(failureResult))) {
            sshHost = new SshHost("test-host", ssh);
            Cmd cmd = Cmd.cmd("failing-command");

            // Act
            String output = sshHost.capture(cmd, false);

            // Assert
            assertThat(output).isEmpty();
        }
    }
}
