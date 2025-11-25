package dev.deploy4j.deploy.env;

import org.apache.commons.lang.StringUtils;

import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

public class Dotenv {

  private static final ConcurrentHashMap<String, String> env = new ConcurrentHashMap<>(System.getenv());

  public static Map<String, String> parse(String file) {

    Map<String, String> resolvedMap = new HashMap<>();

    // just use java properties. similar format?
    Properties dotenv = new Properties();
    try (var inputStream = new FileInputStream(file)) {

      dotenv.load(inputStream);

      for (String name : dotenv.stringPropertyNames()) {
        String property = dotenv.getProperty(name);

        // if we have a value in the file, use it. otherwise fallback to existing env var or not set at all
        if(StringUtils.isNotBlank(property)) {
          resolvedMap.put(name, property);
        } else {
          if (env.containsKey(name)) {
            resolvedMap.put(name, env.get(name));
          }
        }
      }

    } catch (Exception e) {
      throw new RuntimeException("Failed to read " + file, e);
    }

    return resolvedMap;

  }

}
