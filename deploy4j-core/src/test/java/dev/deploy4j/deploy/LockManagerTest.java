package dev.deploy4j.deploy;

import dev.deploy4j.deploy.host.commands.LockHostCommands;
import dev.deploy4j.deploy.host.commands.ServerHostCommands;
import dev.deploy4j.deploy.host.ssh.SshHost;
import dev.deploy4j.deploy.host.ssh.SshHosts;
import dev.rebelcraft.cmd.Cmd;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("LockManager")
class LockManagerTest {

    @Mock
    private SshHosts sshHosts;

    @Mock
    private LockHostCommands lockCommands;

    @Mock
    private ServerHostCommands serverCommands;

    @Mock
    private LockContext lockContext;

    @Mock
    private SshHost sshHost;

    private LockManager lockManager;

    @BeforeEach
    void setUp() {
        lockManager = new LockManager(sshHosts, lockCommands, serverCommands, "1.0.0");
        
        lenient().when(lockContext.primaryHost()).thenReturn("host1");
        lenient().when(lockContext.hosts()).thenReturn(List.of("host1", "host2"));
    }

    @Test
    @DisplayName("should run without acquiring lock when already holding lock")
    void shouldRunWithoutAcquiringWhenAlreadyHoldingLock() {
        // Arrange
        when(lockContext.holdingLock()).thenReturn(true);
        Runnable task = mock(Runnable.class);

        // Act
        lockManager.withLock(lockContext, task);

        // Assert
        verify(task).run();
        verify(sshHosts, never()).on(any(), any());
        verify(lockContext, never()).holdingLock(true);
    }

    @Test
    @DisplayName("should acquire and release lock when not holding lock")
    void shouldAcquireAndReleaseLock() {
        // Arrange
        when(lockContext.holdingLock()).thenReturn(false);
        Runnable task = mock(Runnable.class);
        
        Cmd acquireCmd = mock(Cmd.class);
        Cmd releaseCmd = mock(Cmd.class);
        Cmd ensureRunDirCmd = mock(Cmd.class);
        Cmd ensureLocksDirCmd = mock(Cmd.class);
        
        when(lockCommands.acquire(anyString(), anyString())).thenReturn(acquireCmd);
        when(lockCommands.release()).thenReturn(releaseCmd);
        when(lockCommands.ensureLocksDirectory()).thenReturn(ensureLocksDirCmd);
        when(serverCommands.ensureRunDirectory()).thenReturn(ensureRunDirCmd);

        doAnswer(invocation -> {
            Consumer<SshHost> consumer = invocation.getArgument(1);
            consumer.accept(sshHost);
            return null;
        }).when(sshHosts).on(any(List.class), any());

        // Act
        lockManager.withLock(lockContext, task);

        // Assert
        verify(task).run();
        verify(lockContext).holdingLock(true);
        verify(lockContext).holdingLock(false);
        verify(sshHost, atLeastOnce()).execute(any());
    }

    @Test
    @DisplayName("should release lock even when task throws exception")
    void shouldReleaseLockOnException() {
        // Arrange
        when(lockContext.holdingLock()).thenReturn(false);
        Runnable task = mock(Runnable.class);
        doThrow(new RuntimeException("Task failed")).when(task).run();
        
        Cmd acquireCmd = mock(Cmd.class);
        Cmd releaseCmd = mock(Cmd.class);
        Cmd ensureRunDirCmd = mock(Cmd.class);
        Cmd ensureLocksDirCmd = mock(Cmd.class);
        
        when(lockCommands.acquire(anyString(), anyString())).thenReturn(acquireCmd);
        when(lockCommands.release()).thenReturn(releaseCmd);
        when(lockCommands.ensureLocksDirectory()).thenReturn(ensureLocksDirCmd);
        when(serverCommands.ensureRunDirectory()).thenReturn(ensureRunDirCmd);

        doAnswer(invocation -> {
            Consumer<SshHost> consumer = invocation.getArgument(1);
            consumer.accept(sshHost);
            return null;
        }).when(sshHosts).on(any(List.class), any());

        // Act & Assert
        assertThatThrownBy(() -> lockManager.withLock(lockContext, task))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Task failed");

        // Verify lock was released
        verify(lockContext).holdingLock(true);
        verify(lockContext).holdingLock(false);
    }

    @Test
    @DisplayName("should ensure directories are created before acquiring lock")
    void shouldEnsureDirectoriesCreated() {
        // Arrange
        when(lockContext.holdingLock()).thenReturn(false);
        Runnable task = mock(Runnable.class);
        
        Cmd acquireCmd = mock(Cmd.class);
        Cmd releaseCmd = mock(Cmd.class);
        Cmd ensureRunDirCmd = mock(Cmd.class);
        Cmd ensureLocksDirCmd = mock(Cmd.class);
        
        when(lockCommands.acquire(anyString(), anyString())).thenReturn(acquireCmd);
        when(lockCommands.release()).thenReturn(releaseCmd);
        when(lockCommands.ensureLocksDirectory()).thenReturn(ensureLocksDirCmd);
        when(serverCommands.ensureRunDirectory()).thenReturn(ensureRunDirCmd);

        doAnswer(invocation -> {
            Consumer<SshHost> consumer = invocation.getArgument(1);
            consumer.accept(sshHost);
            return null;
        }).when(sshHosts).on(any(List.class), any());

        // Act
        lockManager.withLock(lockContext, task);

        // Assert
        verify(serverCommands).ensureRunDirectory();
        verify(lockCommands).ensureLocksDirectory();
    }

    @Test
    @DisplayName("should handle locked state and show status")
    void shouldHandleLockedState() {
        // Arrange
        Runnable task = mock(Runnable.class);
        RuntimeException lockedException = new RuntimeException("cannot create directory");
        doThrow(lockedException).when(task).run();
        
        Cmd statusCmd = mock(Cmd.class);
        when(lockCommands.status()).thenReturn(statusCmd);
        when(sshHost.capture(any(Cmd.class))).thenReturn("Lock status output");

        doAnswer(invocation -> {
            Consumer<SshHost> consumer = invocation.getArgument(1);
            consumer.accept(sshHost);
            return null;
        }).when(sshHosts).on(eq(List.of("host1")), any());

        // Act & Assert
        assertThatThrownBy(() -> lockManager.raiseIfLocked(lockContext, task))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Deploy lock found");

        verify(lockCommands).status();
    }

    @Test
    @DisplayName("should rethrow other exceptions without lock handling")
    void shouldRethrowOtherExceptions() {
        // Arrange
        Runnable task = mock(Runnable.class);
        RuntimeException otherException = new RuntimeException("Some other error");
        doThrow(otherException).when(task).run();

        // Act & Assert
        assertThatThrownBy(() -> lockManager.raiseIfLocked(lockContext, task))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Some other error");

        verify(lockCommands, never()).status();
    }
}
