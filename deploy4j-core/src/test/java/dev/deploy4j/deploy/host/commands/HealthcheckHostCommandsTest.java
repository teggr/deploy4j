package dev.deploy4j.deploy.host.commands;

import dev.deploy4j.deploy.configuration.Configuration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class HealthcheckHostCommandsTest {

    private Configuration mockConfig;
    private HealthcheckHostCommands healthcheckCommands;

    @BeforeEach
    void setUp() {
        mockConfig = mock(Configuration.class);
        healthcheckCommands = new HealthcheckHostCommands(mockConfig);
    }

    @Test
    void shouldInstantiateHealthcheckHostCommands() {
        assertThat(healthcheckCommands).isNotNull();
    }

    @Test
    void shouldExtendBaseHostCommands() {
        assertThat(healthcheckCommands).isInstanceOf(BaseHostCommands.class);
    }

    @Test
    void shouldAcceptConfiguration() {
        Configuration config = mock(Configuration.class);
        HealthcheckHostCommands commands = new HealthcheckHostCommands(config);
        
        assertThat(commands).isNotNull();
    }
}
