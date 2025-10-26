package dev.deploy4j.deploy.utils.erb;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("ERB")
class ERBTest {

    @Test
    @DisplayName("should create ERB with file")
    void shouldCreateERBWithFile() {
        // Arrange
        File file = new File("test.erb");

        // Act
        ERB erb = new ERB(file);

        // Assert
        assertThat(erb).isNotNull();
    }

    @Test
    @DisplayName("should return null result")
    void shouldReturnNullResult() {
        // Arrange
        File file = new File("test.erb");
        ERB erb = new ERB(file);

        // Act
        String result = erb.result();

        // Assert
        // Currently returns null as it's not implemented
        assertThat(result).isNull();
    }
}
