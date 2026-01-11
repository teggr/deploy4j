package dev.deploy4j.init;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("GuidedInitializer")
class GuidedInitializerTest {

  @Test
  @DisplayName("should process template with custom service name")
  void shouldProcessTemplateWithCustomServiceName() {
    // Arrange
    TemplateProcessor templateProcessor = new TemplateProcessor();
    String customServiceName = "my-custom-service";
    InitializationModel model = new InitializationModel(customServiceName);

    // Act
    String result = templateProcessor.processTemplate("deploy.yml", model);

    // Assert - verify the template was processed correctly
    assertThat(result).contains("service: " + customServiceName);
    assertThat(result).contains("image: " + customServiceName);
    assertThat(result).doesNotContain("[(${model.serviceName()})]");
    assertThat(result).doesNotContain("[(${model.imageName()})]");
  }

  @Test
  @DisplayName("should use default service name when null is provided")
  void shouldUseDefaultServiceNameWhenNull() {
    // Arrange
    TemplateProcessor templateProcessor = new TemplateProcessor();
    InitializationModel model = new InitializationModel(null);

    // Act
    String result = templateProcessor.processTemplate("deploy.yml", model);

    // Assert - should use default values
    assertThat(result).contains("service: deploy4j-demo");
    assertThat(result).contains("image: deploy4j-demo");
  }

  @Test
  @DisplayName("should use default service name when empty string is provided")
  void shouldUseDefaultServiceNameWhenEmpty() {
    // Arrange
    TemplateProcessor templateProcessor = new TemplateProcessor();
    InitializationModel model = new InitializationModel("  ");

    // Act
    String result = templateProcessor.processTemplate("deploy.yml", model);

    // Assert - should use default values when empty/whitespace
    assertThat(result).contains("service: deploy4j-demo");
    assertThat(result).contains("image: deploy4j-demo");
  }
  
  @Test
  @DisplayName("InitializationModel should provide serviceName and imageName")
  void initializationModelShouldProvideAccessors() {
    // Arrange & Act
    InitializationModel model1 = new InitializationModel("test-service");
    InitializationModel model2 = new InitializationModel(null);
    InitializationModel model3 = new InitializationModel("  ");

    // Assert
    assertThat(model1.serviceName()).isEqualTo("test-service");
    assertThat(model1.imageName()).isEqualTo("test-service");
    
    assertThat(model2.serviceName()).isEqualTo("deploy4j-demo");
    assertThat(model2.imageName()).isEqualTo("deploy4j-demo");
    
    assertThat(model3.serviceName()).isEqualTo("deploy4j-demo");
    assertThat(model3.imageName()).isEqualTo("deploy4j-demo");
  }
}
