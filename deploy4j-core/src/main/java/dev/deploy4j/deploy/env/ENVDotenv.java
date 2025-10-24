package dev.deploy4j.deploy.env;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.util.Properties;
import java.util.stream.Stream;

public class ENVDotenv {

  private static final Logger log = LoggerFactory.getLogger(ENVDotenv.class);

  public static void overload(String... envFiles) {
    Stream.of(envFiles).forEach(ENVDotenv::doOverload);
  }

  private static void doOverload(String envFile) {

    // just use java properties. similar format?
    Properties dotenv = new Properties();
    try {

      dotenv.load(new FileInputStream(envFile));

      ENV.update(dotenv.entrySet().stream()
        .collect(java.util.stream.Collectors.toMap(
          e -> e.getKey().toString(),
          e -> e.getValue().toString()
        )));

    } catch (Exception e) {
      throw new RuntimeException("Failed to read " + envFile, e);
    }

  }

}
