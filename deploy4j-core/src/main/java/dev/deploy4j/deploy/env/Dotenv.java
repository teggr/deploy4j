package dev.deploy4j.deploy.env;

import org.apache.commons.lang3.StringUtils;

import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class Dotenv {

  public static Map<String, String> parse(String file) {

    Map<String, String> resolvedMap = new HashMap<>();

    // just use java properties. similar format?
    Properties dotenv = new Properties();
    try (var inputStream = new FileInputStream(file)) {

      dotenv.load(inputStream);

      for (String name : dotenv.stringPropertyNames()) {
        String property = dotenv.getProperty(name);

        // do we have a $REFERENCE to a env var?
        if (StringUtils.isNotBlank(property) && property.startsWith("$") ) {
          property = System.getenv(property.substring(1));
        }
        resolvedMap.put(name, property);

      }

    } catch (Exception e) {
      throw new RuntimeException("Failed to read " + file, e);
    }

    return resolvedMap;

  }

}
