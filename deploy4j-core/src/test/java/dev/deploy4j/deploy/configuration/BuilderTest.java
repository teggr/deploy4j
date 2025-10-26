package dev.deploy4j.deploy.configuration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

@DisplayName("Builder")
class BuilderTest {

    @Test
    @DisplayName("should create builder with configuration")
    void shouldCreateBuilderWithConfiguration() {
        // Arrange
        Configuration config = mock(Configuration.class);

        // Act
        Builder builder = new Builder(config);

        // Assert
        assertThat(builder).isNotNull();
    }

    @Test
    @DisplayName("should resolve to empty map")
    void shouldResolveToEmptyMap() {
        // Arrange
        Configuration config = mock(Configuration.class);
        Builder builder = new Builder(config);

        // Act
        Map<String, Object> resolved = builder.resolve();

        // Assert
        assertThat(resolved).isEmpty();
    }
}
