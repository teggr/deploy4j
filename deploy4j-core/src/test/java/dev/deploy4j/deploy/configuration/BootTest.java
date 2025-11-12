package dev.deploy4j.deploy.configuration;

import dev.deploy4j.deploy.configuration.raw.BootConfig;
import dev.deploy4j.deploy.configuration.raw.DeployConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DisplayName("Boot")
class BootTest {

    @Test
    @DisplayName("should use default boot config when null")
    void shouldUseDefaultBootConfigWhenNull() {
        // Arrange
        DeployConfig deployConfig = DeployConfigBuilder.minimal().boot(null).build();
        Configuration config = mock(Configuration.class);
        when(config.rawConfig()).thenReturn(deployConfig);
        when(config.allHosts()).thenReturn(List.of("host1", "host2", "host3"));

        // Act
        Boot boot = new Boot(config);

        // Assert
        assertThat(boot.bootConfig()).isNotNull();
        assertThat(boot.hostCount()).isEqualTo(3);
    }

    @Test
    @DisplayName("should use provided boot config")
    void shouldUseProvidedBootConfig() {
        // Arrange
        BootConfig bootConfig = new BootConfig("50%", "10");
        DeployConfig deployConfig = DeployConfigBuilder.minimal().boot(bootConfig).build();
        Configuration config = mock(Configuration.class);
        when(config.rawConfig()).thenReturn(deployConfig);
        when(config.allHosts()).thenReturn(List.of("host1", "host2", "host3", "host4"));

        // Act
        Boot boot = new Boot(config);

        // Assert
        assertThat(boot.bootConfig()).isEqualTo(bootConfig);
        assertThat(boot.hostCount()).isEqualTo(4);
    }

    @Test
    @DisplayName("should calculate limit as percentage of hosts")
    void shouldCalculateLimitAsPercentageOfHosts() {
        // Arrange
        BootConfig bootConfig = new BootConfig("50%", null);
        DeployConfig deployConfig = DeployConfigBuilder.minimal().boot(bootConfig).build();
        Configuration config = mock(Configuration.class);
        when(config.rawConfig()).thenReturn(deployConfig);
        when(config.allHosts()).thenReturn(List.of("host1", "host2", "host3", "host4"));

        // Act
        Boot boot = new Boot(config);

        // Assert
        assertThat(boot.limit()).isEqualTo(2); // 50% of 4 = 2
    }

    @Test
    @DisplayName("should calculate limit as absolute number")
    void shouldCalculateLimitAsAbsoluteNumber() {
        // Arrange
        BootConfig bootConfig = new BootConfig("3", null);
        DeployConfig deployConfig = DeployConfigBuilder.minimal().boot(bootConfig).build();
        Configuration config = mock(Configuration.class);
        when(config.rawConfig()).thenReturn(deployConfig);
        when(config.allHosts()).thenReturn(List.of("host1", "host2", "host3", "host4", "host5"));

        // Act
        Boot boot = new Boot(config);

        // Assert
        assertThat(boot.limit()).isEqualTo(3);
    }

    @Test
    @DisplayName("should ensure minimum limit of 1 for percentage calculations")
    void shouldEnsureMinimumLimitOf1ForPercentageCalculations() {
        // Arrange
        BootConfig bootConfig = new BootConfig("10%", null);
        DeployConfig deployConfig = DeployConfigBuilder.minimal().boot(bootConfig).build();
        Configuration config = mock(Configuration.class);
        when(config.rawConfig()).thenReturn(deployConfig);
        when(config.allHosts()).thenReturn(List.of("host1", "host2")); // 10% of 2 = 0.2

        // Act
        Boot boot = new Boot(config);

        // Assert
        assertThat(boot.limit()).isEqualTo(1); // Should be at least 1
    }

    @Test
    @DisplayName("should return waiter value from config")
    void shouldReturnWaiterValueFromConfig() {
        // Arrange
        BootConfig bootConfig = new BootConfig("2", "30");
        DeployConfig deployConfig = DeployConfigBuilder.minimal().boot(bootConfig).build();
        Configuration config = mock(Configuration.class);
        when(config.rawConfig()).thenReturn(deployConfig);
        when(config.allHosts()).thenReturn(List.of("host1"));

        // Act
        Boot boot = new Boot(config);

        // Assert
        assertThat(boot.waiter()).isEqualTo("30");
    }

    @Test
    @DisplayName("should handle 100 percent limit")
    void shouldHandle100PercentLimit() {
        // Arrange
        BootConfig bootConfig = new BootConfig("100%", null);
        DeployConfig deployConfig = DeployConfigBuilder.minimal().boot(bootConfig).build();
        Configuration config = mock(Configuration.class);
        when(config.rawConfig()).thenReturn(deployConfig);
        when(config.allHosts()).thenReturn(List.of("host1", "host2", "host3"));

        // Act
        Boot boot = new Boot(config);

        // Assert
        assertThat(boot.limit()).isEqualTo(3); // 100% of 3 = 3
    }

    @Test
    @DisplayName("should handle 25 percent limit")
    void shouldHandle25PercentLimit() {
        // Arrange
        BootConfig bootConfig = new BootConfig("25%", null);
        DeployConfig deployConfig = DeployConfigBuilder.minimal().boot(bootConfig).build();
        Configuration config = mock(Configuration.class);
        when(config.rawConfig()).thenReturn(deployConfig);
        when(config.allHosts()).thenReturn(List.of("host1", "host2", "host3", "host4", "host5", "host6", "host7", "host8"));

        // Act
        Boot boot = new Boot(config);

        // Assert
        assertThat(boot.limit()).isEqualTo(2); // 25% of 8 = 2
    }
}
