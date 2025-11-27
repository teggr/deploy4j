package dev.deploy4j.deploy.configuration.raw;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BootConfigTest {

    @Test
    void shouldCreateEmptyBootConfig() {
        BootConfig config = new BootConfig();
        
        assertThat(config.limit()).isNull();
        assertThat(config.waitTime()).isNull();
    }

    @Test
    void shouldCreateBootConfigWithLimitAndWait() {
        BootConfig config = new BootConfig("10", "5");
        
        assertThat(config.limit()).isEqualTo("10");
        assertThat(config.waitTime()).isEqualTo("5");
    }

    @Test
    void shouldHandleNullValues() {
        BootConfig config = new BootConfig(null, null);
        
        assertThat(config.limit()).isNull();
        assertThat(config.waitTime()).isNull();
    }

    @Test
    void shouldHandleLimitOnly() {
        BootConfig config = new BootConfig("5", null);
        
        assertThat(config.limit()).isEqualTo("5");
        assertThat(config.waitTime()).isNull();
    }

    @Test
    void shouldHandleWaitOnly() {
        BootConfig config = new BootConfig(null, "10");
        
        assertThat(config.limit()).isNull();
        assertThat(config.waitTime()).isEqualTo("10");
    }

    @Test
    void shouldHandleNumericStringValues() {
        BootConfig config = new BootConfig("100", "30");
        
        assertThat(config.limit()).isEqualTo("100");
        assertThat(config.waitTime()).isEqualTo("30");
    }
}
