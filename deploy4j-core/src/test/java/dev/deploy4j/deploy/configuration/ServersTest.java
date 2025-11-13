package dev.deploy4j.deploy.configuration;

import dev.deploy4j.deploy.configuration.raw.DeployConfig;
import dev.deploy4j.deploy.configuration.raw.RoleConfig;
import dev.deploy4j.deploy.configuration.raw.ServersConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DisplayName("Servers")
class ServersTest {

    @Test
    @DisplayName("should provide access to configuration")
    void shouldProvideAccessToConfiguration() {
        // Arrange
        Configuration config = mock(Configuration.class);
        DeployConfig deployConfig = mock(DeployConfig.class);
        ServersConfig serversConfig = mock(ServersConfig.class);
        
        when(config.rawConfig()).thenReturn(deployConfig);
        when(deployConfig.servers()).thenReturn(serversConfig);
        when(serversConfig.isAList()).thenReturn(true);

        // Act
        Servers servers = new Servers(config);

        // Assert
        assertThat(servers.config()).isEqualTo(config);
        assertThat(servers.serversConfig()).isEqualTo(serversConfig);
    }
}
