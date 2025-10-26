package dev.deploy4j.deploy.configuration.raw;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class LoggingConfigTest {

    @Test
    void shouldCreateConfigWithDriverAndOptions() {
        Map<String, String> options = Map.of("max-size", "10m", "max-file", "3");
        
        LoggingConfig config = new LoggingConfig("json-file", options);
        
        assertThat(config.driver()).isEqualTo("json-file");
        assertThat(config.options()).isEqualTo(options);
    }

    @Test
    void shouldCreateEmptyConfig() {
        LoggingConfig config = new LoggingConfig();
        
        assertThat(config.driver()).isNull();
        assertThat(config.options()).isNull();
    }

    @Test
    void shouldDeepMergeWithOtherConfig() {
        Map<String, String> options1 = Map.of("max-size", "10m");
        Map<String, String> options2 = Map.of("max-size", "20m", "max-file", "5");
        
        LoggingConfig config1 = new LoggingConfig("json-file", options1);
        LoggingConfig config2 = new LoggingConfig("syslog", options2);
        
        LoggingConfig merged = config1.deepMerge(config2);
        
        assertThat(merged.driver()).isEqualTo("syslog");
        assertThat(merged.options()).isEqualTo(options2);
    }

    @Test
    void shouldKeepOriginalValuesWhenMergingWithNulls() {
        Map<String, String> options = Map.of("max-size", "10m");
        LoggingConfig config1 = new LoggingConfig("json-file", options);
        LoggingConfig config2 = new LoggingConfig(null, null);
        
        LoggingConfig merged = config1.deepMerge(config2);
        
        assertThat(merged.driver()).isEqualTo("json-file");
        assertThat(merged.options()).isEqualTo(options);
    }

    @Test
    void shouldHandleNullDriver() {
        Map<String, String> options = Map.of("max-size", "10m");
        LoggingConfig config = new LoggingConfig(null, options);
        
        assertThat(config.driver()).isNull();
        assertThat(config.options()).isEqualTo(options);
    }

    @Test
    void shouldHandleNullOptions() {
        LoggingConfig config = new LoggingConfig("json-file", null);
        
        assertThat(config.driver()).isEqualTo("json-file");
        assertThat(config.options()).isNull();
    }
}
