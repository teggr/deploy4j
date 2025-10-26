package dev.deploy4j.deploy;

import dev.deploy4j.deploy.configuration.Configuration;
import dev.deploy4j.deploy.host.commands.AuditorHostCommands;
import dev.deploy4j.deploy.host.commands.PruneHostCommands;
import dev.deploy4j.deploy.host.ssh.SshHosts;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Prune")
class PruneTest {

    @Mock
    private SshHosts sshHosts;

    @Mock
    private LockManager lockManager;

    @Mock
    private PruneHostCommands pruneHostCommands;

    @Mock
    private AuditorHostCommands auditorHostCommands;

    @Mock
    private DeployContext deployContext;

    @Mock
    private Configuration configuration;

    private Prune prune;

    @BeforeEach
    void setUp() {
        prune = new Prune(sshHosts, lockManager, pruneHostCommands, auditorHostCommands);
    }

    @Test
    @DisplayName("should call lockManager for all operations")
    void shouldCallLockManagerForAllOperations() {
        // Act
        prune.all(deployContext);

        // Assert
        verify(lockManager).withLock(any(), any());
    }

    @Test
    @DisplayName("should call lockManager for images operations")
    void shouldCallLockManagerForImagesOperations() {
        // Act
        prune.images(deployContext);

        // Assert
        verify(lockManager).withLock(any(), any());
    }

    @Test
    @DisplayName("should call lockManager for containers operations")
    void shouldCallLockManagerForContainersOperations() {
        // Arrange
        when(deployContext.config()).thenReturn(configuration);
        when(configuration.retainContainer()).thenReturn(5);

        // Act
        prune.containers(deployContext);

        // Assert
        verify(lockManager).withLock(any(), any());
    }

    @Test
    @DisplayName("should throw exception when retain is less than 1")
    void shouldThrowExceptionWhenRetainLessThan1() {
        // Act & Assert
        assertThatThrownBy(() -> prune.containers(deployContext, 0))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("retain must be at least 1");
    }

    @Test
    @DisplayName("should accept valid retain value")
    void shouldAcceptValidRetainValue() {
        // Act - Should not throw
        prune.containers(deployContext, 10);

        // Assert
        verify(lockManager).withLock(any(), any());
    }

    @Test
    @DisplayName("should be instantiated with required dependencies")
    void shouldBeInstantiatedWithDependencies() {
        assertThat(prune).isNotNull();
    }
}
