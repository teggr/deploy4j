package dev.deploy4j.init;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

public class Initializer {

  private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(Initializer.class);

  /**
   * Create config stub in config/deploy.yml and env stub in .env
   */
  public void init(boolean bundle) {

    File deployFile = new File("config/deploy.yml");
    if (deployFile.exists()) {
      log.info("Config file already exists in config/deploy.yml (remove first to create a new one)");
    } else {
      deployFile.getParentFile().mkdirs();
      try {
        FileUtils.copyInputStreamToFile(
          getClass().getClassLoader().getResourceAsStream("templates/deploy.yml"),
          deployFile
        );
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
      log.info("Created configuration file in config/deploy.yml");
    }

    deployFile = new File(".env");
    if (!deployFile.exists()) {
      try {
        FileUtils.copyInputStreamToFile(
          getClass().getClassLoader().getResourceAsStream("templates/template.env"),
          deployFile
        );
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
      log.info("Created .env file");
    }

    // TODO: hooks

    // TODO: bundle add maven dependency?

  }

}
