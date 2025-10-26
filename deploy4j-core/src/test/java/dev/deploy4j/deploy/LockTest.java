package dev.deploy4j.deploy;

import dev.deploy4j.deploy.host.commands.LockHostCommands;
import dev.deploy4j.deploy.host.commands.ServerHostCommands;
import dev.deploy4j.deploy.host.ssh.SshHosts;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Lock")
class LockTest {

    @Mock
    private SshHosts sshHosts;

    @Mock
    private LockManager lockManager;

    @Mock
    private ServerHostCommands serverHostCommands;

    @Mock
    private LockHostCommands lockHostCommands;

    @Mock
    private DeployContext deployContext;

    private Lock lock;

    @BeforeEach
    void setUp() {
        lock = new Lock(sshHosts, lockManager, serverHostCommands, lockHostCommands);
    }

    @Test
    @DisplayName("should retrieve primary host from context for status")
    void shouldRetrievePrimaryHostForStatus() {
        // Arrange
        String primaryHost = "primary.example.com";
        when(deployContext.primaryHost()).thenReturn(primaryHost);

        // Act
        lock.status(deployContext);

        // Assert
        verify(deployContext).primaryHost();
    }

    @Test
    @DisplayName("should call lockManager raiseIfLocked for acquire")
    void shouldCallLockManagerForAcquire() {
        // Arrange
        String message = "Deploying version 1.0";

        // Act
        lock.acquire(deployContext, message);

        // Assert
        verify(lockManager).raiseIfLocked(any(), any());
    }

    @Test
    @DisplayName("should retrieve primary host from context for release")
    void shouldRetrievePrimaryHostForRelease() {
        // Arrange
        String primaryHost = "primary.example.com";
        when(deployContext.primaryHost()).thenReturn(primaryHost);

        // Act
        lock.release(deployContext);

        // Assert
        verify(deployContext).primaryHost();
    }

    @Test
    @DisplayName("should be instantiated with required dependencies")
    void shouldBeInstantiatedWithDependencies() {
        assertThat(lock).isNotNull();
    }
}
