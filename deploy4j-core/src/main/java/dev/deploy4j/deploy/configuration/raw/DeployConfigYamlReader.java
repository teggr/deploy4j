package dev.deploy4j.deploy.configuration.raw;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLAnchorReplayingFactory;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.FileTemplateResolver;

import java.io.IOException;
import java.util.List;

public class DeployConfigYamlReader {

  private final static ObjectMapper mapper = new ObjectMapper(new YAMLAnchorReplayingFactory());

  private final static TemplateEngine templateEngine = getTemplateEngine();

  private static TemplateEngine getTemplateEngine() {
    TemplateEngine engine = new TemplateEngine();
    FileTemplateResolver templateResolver = new FileTemplateResolver();
    templateResolver.setTemplateMode(TemplateMode.TEXT);
    engine.setTemplateResolver(templateResolver);
    return engine;
  }

  public static class EnvHelper {
    public String get(String key) {
      return System.getenv(key);
    }

    public String get(String key, String defaultValue) {
      String value = System.getenv(key);
      return value != null ? value : defaultValue;
    }
  }

  public static class PropsHelper {
    public String get(String key) {
      return System.getProperty(key);
    }

    public String get(String key, String defaultValue) {
      return System.getProperty(key, defaultValue);
    }
  }

  public static DeployConfig loadConfigFiles(List<String> files) {
    try {
      com.fasterxml.jackson.databind.JsonNode merged = null;
      for (String file : files) {

        Context context = new Context();
        context.setVariable("env", new EnvHelper());
        context.setVariable("props", new PropsHelper());
        String output = templateEngine.process(file, context);

        com.fasterxml.jackson.databind.JsonNode node = mapper.readTree(output);
        if (merged == null) {
          merged = node;
        } else {
          merged = mergeNodes(merged, node);
        }
      }
      return mapper.treeToValue(merged, DeployConfig.class);
    } catch (Exception e) {
      throw new RuntimeException("Failed to load or merge YAML config files", e);
    }
  }

  private static JsonNode mergeNodes(JsonNode mainNode, JsonNode updateNode) {
    if (mainNode == null || updateNode == null) {
      return mainNode == null ? updateNode : mainNode;
    }
    if (mainNode.isObject() && updateNode.isObject()) {
      updateNode.fieldNames().forEachRemaining(fieldName -> {
        JsonNode valueToUpdate = updateNode.get(fieldName);
        JsonNode valueMain = mainNode.get(fieldName);
        if (valueMain != null && valueMain.isObject() && valueToUpdate.isObject()) {
          ((com.fasterxml.jackson.databind.node.ObjectNode) mainNode).set(
            fieldName, mergeNodes(valueMain, valueToUpdate));
        } else {
          ((com.fasterxml.jackson.databind.node.ObjectNode) mainNode).set(fieldName, valueToUpdate);
        }
      });
      return mainNode;
    }
    return updateNode;
  }

  /**
   * Reads a YAML file and produces a Deploy4jConfig object using Jackson.
   *
   * @param yamlFilePath path to the YAML file
   * @return Deploy4jConfig object
   * @throws IOException if file cannot be read
   */
  public static DeployConfig readYaml(String yamlFilePath) throws IOException {
    return mapper.readValue(new java.io.File(yamlFilePath), DeployConfig.class);
  }

  public static DeployConfig readYamlFromString(String yaml) throws IOException {
    return mapper.readValue(yaml, DeployConfig.class);
  }

}
