package dev.deploy4j.raw;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.io.IOException;

public class Deploy4jConfigReader {
    /**
     * Reads a YAML file and produces a Deploy4jConfig object using Jackson.
     *
     * @param yamlFilePath path to the YAML file
     * @return Deploy4jConfig object
     * @throws IOException if file cannot be read
     */
    public static Deploy4jConfig readYaml(String yamlFilePath) throws IOException {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        return mapper.readValue(new java.io.File(yamlFilePath), Deploy4jConfig.class);
    }
}
