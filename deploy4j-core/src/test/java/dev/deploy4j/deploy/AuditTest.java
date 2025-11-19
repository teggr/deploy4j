package dev.deploy4j.deploy;

import dev.deploy4j.deploy.host.commands.AuditorHostCommands;
import dev.deploy4j.deploy.host.ssh.SshHosts;
import dev.deploy4j.deploy.local.LocalHost;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Audit")
class AuditTest {

    @Mock
    private SshHosts sshHosts;

    @Mock
    private AuditorHostCommands auditorHostCommands;

    @Mock
    private DeployContext deployContext;

    @Mock
    private Hooks hooks;

    @Mock
    private LocalHost localHost;

    private Audit audit;

    @BeforeEach
    void setUp() {
        audit = new Audit(sshHosts, hooks, localHost, auditorHostCommands);
    }

    @Test
    @DisplayName("should call sshHosts on method with correct hosts")
    void shouldCallSshHostsWithCorrectHosts() {
        // Arrange
        List<String> hosts = Arrays.asList("host1", "host2");
        when(deployContext.hosts()).thenReturn(hosts);

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<String>> hostsCaptor = ArgumentCaptor.forClass(List.class);

        // Act
        audit.audit(deployContext);

        // Assert
        verify(sshHosts).on(hostsCaptor.capture(), any());
        assertThat(hostsCaptor.getValue()).isEqualTo(hosts);
    }

    @Test
    @DisplayName("should retrieve hosts from context")
    void shouldRetrieveHostsFromContext() {
        // Arrange
        List<String> expectedHosts = Arrays.asList("server1.example.com");
        when(deployContext.hosts()).thenReturn(expectedHosts);

        // Act
        audit.audit(deployContext);

        // Assert
        verify(deployContext).hosts();
    }
}
