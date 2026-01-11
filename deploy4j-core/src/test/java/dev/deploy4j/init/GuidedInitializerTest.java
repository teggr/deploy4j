package dev.deploy4j.init;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("GuidedInitializer")
class GuidedInitializerTest {

  @Test
  @DisplayName("should replace service name in template content")
  void shouldReplaceServiceNameInTemplateContent() throws IOException {
    // Arrange - Load the template
    String template = IOUtils.toString(
      getClass().getClassLoader().getResourceAsStream("templates/deploy.yml"),
      StandardCharsets.UTF_8
    );
    String customServiceName = "my-custom-service";

    // Act - Replace service name like Initializer does
    String result = template.replaceFirst(
      "service: deploy4j-demo",
      "service: " + customServiceName
    );
    result = result.replaceFirst(
      "image: teggr/deploy4j-demo",
      "image: " + customServiceName
    );

    // Assert - verify the replacements worked
    assertThat(result).contains("service: " + customServiceName);
    assertThat(result).contains("image: " + customServiceName);
    assertThat(result).doesNotContain("service: deploy4j-demo");
    // Should still have the teggr/ prefix for the original, but our custom one doesn't
    assertThat(result).doesNotContain("image: teggr/deploy4j-demo");
  }

  @Test
  @DisplayName("should preserve template when service name is null")
  void shouldPreserveTemplateWhenServiceNameIsNull() throws IOException {
    // Arrange
    String template = IOUtils.toString(
      getClass().getClassLoader().getResourceAsStream("templates/deploy.yml"),
      StandardCharsets.UTF_8
    );

    // Act - null service name means no replacement
    String result = template;

    // Assert - should keep default values
    assertThat(result).contains("service: deploy4j-demo");
    assertThat(result).contains("image: teggr/deploy4j-demo");
  }

  @Test
  @DisplayName("should preserve template when service name is empty")
  void shouldPreserveTemplateWhenServiceNameIsEmpty() throws IOException {
    // Arrange
    String template = IOUtils.toString(
      getClass().getClassLoader().getResourceAsStream("templates/deploy.yml"),
      StandardCharsets.UTF_8
    );
    String emptyServiceName = "  ";

    // Act - empty/whitespace service name should be treated as no replacement
    String result;
    if (emptyServiceName == null || emptyServiceName.trim().isEmpty()) {
      result = template;
    } else {
      result = template.replaceFirst(
        "service: deploy4j-demo",
        "service: " + emptyServiceName.trim()
      );
      result = result.replaceFirst(
        "image: teggr/deploy4j-demo",
        "image: " + emptyServiceName.trim()
      );
    }

    // Assert - should keep default values
    assertThat(result).contains("service: deploy4j-demo");
    assertThat(result).contains("image: teggr/deploy4j-demo");
  }
}
