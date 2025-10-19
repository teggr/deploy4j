package dev.deploy4j.raw;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.io.IOException;

public class Deploy4jYamlConfigReader {

  private final static ObjectMapper mapper = new ObjectMapper(new YAMLFactory());

  /**
   * Reads a YAML file and produces a Deploy4jConfig object using Jackson.
   *
   * @param yamlFilePath path to the YAML file
   * @return Deploy4jConfig object
   * @throws IOException if file cannot be read
   */
  public static Deploy4jConfig readYaml(String yamlFilePath) throws IOException {
    return mapper.readValue(new java.io.File(yamlFilePath), Deploy4jConfig.class);
  }

  public static Deploy4jConfig readYamlFromString(String yaml) throws IOException {
    return mapper.readValue(yaml, Deploy4jConfig.class);
  }

}
