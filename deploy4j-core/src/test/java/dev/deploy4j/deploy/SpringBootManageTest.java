package dev.deploy4j.deploy;

import dev.deploy4j.deploy.host.commands.SpringBootHostCommands;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("SpringBootManage")
class SpringBootManageTest {

    @Mock
    private SshHosts sshHosts;

    @Mock
    private SpringBootHostCommands springBootHostCommands;

    @Mock
    private DeployContext deployContext;

    @Mock
    private Hooks hooks;

    @Mock
    private LocalHost localHost;

    @Mock
    private dev.deploy4j.deploy.configuration.Configuration configuration;

    @Mock
    private dev.deploy4j.deploy.configuration.SpringBoot springBootConfig;

    private SpringBootManage springBootManage;

    @BeforeEach
    void setUp() {
        springBootManage = new SpringBootManage(sshHosts, hooks, localHost, springBootHostCommands);
    }

    @Test
    @DisplayName("should call sshHosts on method with configured hosts for health")
    void shouldCallSshHostsWithConfiguredHostsForHealth() {
        // Arrange
        List<String> hosts = Arrays.asList("host1", "host2");
        when(deployContext.specificHosts()).thenReturn(null);
        when(deployContext.config()).thenReturn(configuration);
        when(configuration.springBoot()).thenReturn(springBootConfig);
        when(springBootConfig.effectiveHosts()).thenReturn(hosts);

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<String>> hostsCaptor = ArgumentCaptor.forClass(List.class);

        // Act
        springBootManage.health(deployContext);

        // Assert
        verify(sshHosts).on(hostsCaptor.capture(), any());
        assertThat(hostsCaptor.getValue()).isEqualTo(hosts);
    }

    @Test
    @DisplayName("should use specific hosts from deploy context when provided for info")
    void shouldUseSpecificHostsFromDeployContextForInfo() {
        // Arrange
        List<String> specificHosts = Arrays.asList("specificHost1");
        when(deployContext.specificHosts()).thenReturn(specificHosts);

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<String>> hostsCaptor = ArgumentCaptor.forClass(List.class);

        // Act
        springBootManage.info(deployContext);

        // Assert
        verify(sshHosts).on(hostsCaptor.capture(), any());
        assertThat(hostsCaptor.getValue()).isEqualTo(specificHosts);
    }

    @Test
    @DisplayName("should call sshHosts for env endpoint")
    void shouldCallSshHostsForEnvEndpoint() {
        // Arrange
        List<String> hosts = Arrays.asList("host1");
        when(deployContext.specificHosts()).thenReturn(hosts);

        // Act
        springBootManage.env(deployContext);

        // Assert
        verify(sshHosts).on(eq(hosts), any());
    }

    @Test
    @DisplayName("should call sshHosts for loggers endpoint")
    void shouldCallSshHostsForLoggersEndpoint() {
        // Arrange
        List<String> hosts = Arrays.asList("host1");
        when(deployContext.specificHosts()).thenReturn(hosts);

        // Act
        springBootManage.loggers(deployContext);

        // Assert
        verify(sshHosts).on(eq(hosts), any());
    }

    @Test
    @DisplayName("should call sshHosts for metrics endpoint")
    void shouldCallSshHostsForMetricsEndpoint() {
        // Arrange
        List<String> hosts = Arrays.asList("host1");
        when(deployContext.specificHosts()).thenReturn(hosts);

        // Act
        springBootManage.metrics(deployContext);

        // Assert
        verify(sshHosts).on(eq(hosts), any());
    }

    @Test
    @DisplayName("should call sshHosts for metrics endpoint with specific metric name")
    void shouldCallSshHostsForMetricsEndpointWithName() {
        // Arrange
        List<String> hosts = Arrays.asList("host1");
        when(deployContext.specificHosts()).thenReturn(hosts);

        // Act
        springBootManage.metrics(deployContext, "jvm.memory.used");

        // Assert
        verify(sshHosts).on(eq(hosts), any());
    }

    @Test
    @DisplayName("should call sshHosts for threaddump endpoint")
    void shouldCallSshHostsForThreaddumpEndpoint() {
        // Arrange
        List<String> hosts = Arrays.asList("host1");
        when(deployContext.specificHosts()).thenReturn(hosts);

        // Act
        springBootManage.threaddump(deployContext);

        // Assert
        verify(sshHosts).on(eq(hosts), any());
    }

    @Test
    @DisplayName("should call sshHosts for heapdump endpoint")
    void shouldCallSshHostsForHeapdumpEndpoint() {
        // Arrange
        List<String> hosts = Arrays.asList("host1");
        when(deployContext.specificHosts()).thenReturn(hosts);

        // Act
        springBootManage.heapdump(deployContext);

        // Assert
        verify(sshHosts).on(eq(hosts), any());
    }

    @Test
    @DisplayName("should call sshHosts for beans endpoint")
    void shouldCallSshHostsForBeansEndpoint() {
        // Arrange
        List<String> hosts = Arrays.asList("host1");
        when(deployContext.specificHosts()).thenReturn(hosts);

        // Act
        springBootManage.beans(deployContext);

        // Assert
        verify(sshHosts).on(eq(hosts), any());
    }

    @Test
    @DisplayName("should call sshHosts for shutdown endpoint")
    void shouldCallSshHostsForShutdownEndpoint() {
        // Arrange
        List<String> hosts = Arrays.asList("host1");
        when(deployContext.specificHosts()).thenReturn(hosts);

        // Act
        springBootManage.shutdown(deployContext);

        // Assert
        verify(sshHosts).on(eq(hosts), any());
    }

    @Test
    @DisplayName("should call sshHosts for generic endpoint")
    void shouldCallSshHostsForGenericEndpoint() {
        // Arrange
        List<String> hosts = Arrays.asList("host1");
        when(deployContext.specificHosts()).thenReturn(hosts);

        // Act
        springBootManage.endpoint(deployContext, "custom/endpoint");

        // Assert
        verify(sshHosts).on(eq(hosts), any());
    }

    @Test
    @DisplayName("should not call sshHosts when no hosts are configured")
    void shouldNotCallSshHostsWhenNoHostsConfigured() {
        // Arrange
        when(deployContext.specificHosts()).thenReturn(null);
        when(deployContext.config()).thenReturn(configuration);
        when(configuration.springBoot()).thenReturn(springBootConfig);
        when(springBootConfig.effectiveHosts()).thenReturn(List.of());

        // Act
        springBootManage.health(deployContext);

        // Assert
        verify(sshHosts, never()).on(any(), any());
    }

    @Test
    @DisplayName("should call sshHosts for conditions endpoint")
    void shouldCallSshHostsForConditionsEndpoint() {
        // Arrange
        List<String> hosts = Arrays.asList("host1");
        when(deployContext.specificHosts()).thenReturn(hosts);

        // Act
        springBootManage.conditions(deployContext);

        // Assert
        verify(sshHosts).on(eq(hosts), any());
    }

    @Test
    @DisplayName("should call sshHosts for configprops endpoint")
    void shouldCallSshHostsForConfigpropsEndpoint() {
        // Arrange
        List<String> hosts = Arrays.asList("host1");
        when(deployContext.specificHosts()).thenReturn(hosts);

        // Act
        springBootManage.configprops(deployContext);

        // Assert
        verify(sshHosts).on(eq(hosts), any());
    }

    @Test
    @DisplayName("should call sshHosts for mappings endpoint")
    void shouldCallSshHostsForMappingsEndpoint() {
        // Arrange
        List<String> hosts = Arrays.asList("host1");
        when(deployContext.specificHosts()).thenReturn(hosts);

        // Act
        springBootManage.mappings(deployContext);

        // Assert
        verify(sshHosts).on(eq(hosts), any());
    }

    @Test
    @DisplayName("should call sshHosts for caches endpoint")
    void shouldCallSshHostsForCachesEndpoint() {
        // Arrange
        List<String> hosts = Arrays.asList("host1");
        when(deployContext.specificHosts()).thenReturn(hosts);

        // Act
        springBootManage.caches(deployContext);

        // Assert
        verify(sshHosts).on(eq(hosts), any());
    }

    @Test
    @DisplayName("should call sshHosts for scheduledtasks endpoint")
    void shouldCallSshHostsForScheduledtasksEndpoint() {
        // Arrange
        List<String> hosts = Arrays.asList("host1");
        when(deployContext.specificHosts()).thenReturn(hosts);

        // Act
        springBootManage.scheduledtasks(deployContext);

        // Assert
        verify(sshHosts).on(eq(hosts), any());
    }

    @Test
    @DisplayName("should call sshHosts for httptrace endpoint")
    void shouldCallSshHostsForHttptraceEndpoint() {
        // Arrange
        List<String> hosts = Arrays.asList("host1");
        when(deployContext.specificHosts()).thenReturn(hosts);

        // Act
        springBootManage.httptrace(deployContext);

        // Assert
        verify(sshHosts).on(eq(hosts), any());
    }

    @Test
    @DisplayName("should call sshHosts for httpexchanges endpoint")
    void shouldCallSshHostsForHttpexchangesEndpoint() {
        // Arrange
        List<String> hosts = Arrays.asList("host1");
        when(deployContext.specificHosts()).thenReturn(hosts);

        // Act
        springBootManage.httpexchanges(deployContext);

        // Assert
        verify(sshHosts).on(eq(hosts), any());
    }

    @Test
    @DisplayName("should call sshHosts for flyway endpoint")
    void shouldCallSshHostsForFlywayEndpoint() {
        // Arrange
        List<String> hosts = Arrays.asList("host1");
        when(deployContext.specificHosts()).thenReturn(hosts);

        // Act
        springBootManage.flyway(deployContext);

        // Assert
        verify(sshHosts).on(eq(hosts), any());
    }

    @Test
    @DisplayName("should call sshHosts for liquibase endpoint")
    void shouldCallSshHostsForLiquibaseEndpoint() {
        // Arrange
        List<String> hosts = Arrays.asList("host1");
        when(deployContext.specificHosts()).thenReturn(hosts);

        // Act
        springBootManage.liquibase(deployContext);

        // Assert
        verify(sshHosts).on(eq(hosts), any());
    }

    @Test
    @DisplayName("should call sshHosts for sessions endpoint")
    void shouldCallSshHostsForSessionsEndpoint() {
        // Arrange
        List<String> hosts = Arrays.asList("host1");
        when(deployContext.specificHosts()).thenReturn(hosts);

        // Act
        springBootManage.sessions(deployContext);

        // Assert
        verify(sshHosts).on(eq(hosts), any());
    }

    @Test
    @DisplayName("should call sshHosts for startup endpoint")
    void shouldCallSshHostsForStartupEndpoint() {
        // Arrange
        List<String> hosts = Arrays.asList("host1");
        when(deployContext.specificHosts()).thenReturn(hosts);

        // Act
        springBootManage.startup(deployContext);

        // Assert
        verify(sshHosts).on(eq(hosts), any());
    }
}
