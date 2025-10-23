package dev.deploy4j.deploy.configuration.raw;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.io.IOException;

public class DeployConfigYamlReader {

  private final static ObjectMapper mapper = new ObjectMapper(new YAMLFactory());

  public static DeployConfig loadConfigFiles(String... files) {
    try {
      com.fasterxml.jackson.databind.JsonNode merged = null;
      for (String file : files) {
        com.fasterxml.jackson.databind.JsonNode node = mapper.readTree(new java.io.File(file));
        if (merged == null) {
          merged = node;
        } else {
          merged = mergeNodes(merged, node);
        }
      }
      return mapper.treeToValue(merged, DeployConfig.class);
    } catch (IOException e) {
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
