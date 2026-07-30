package dev.deploy4j.init;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Objects;

public class Initializer {

  private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(Initializer.class);

  /**
   * Create config stub in config/deploy.yml and env stub in .deploy4j/secrets
   */
  public void init(boolean bundle) {
    Path workingDirectory = Path.of(System.getProperty("user.dir"));

    File deployFile = workingDirectory.resolve("config/deploy.yml").toFile();
    if (deployFile.exists()) {
      log.info("Config file already exists in config/deploy.yml (remove first to create a new one)");
    } else {
      deployFile.getParentFile().mkdirs();
      try (InputStream template = getTemplateStream("templates/deploy.yml")) {
        FileUtils.copyInputStreamToFile(template, deployFile);
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
      log.info("Created configuration file in config/deploy.yml");
    }

    File secretsFile = workingDirectory.resolve(".deploy4j/secrets").toFile();
    if(secretsFile.exists()) {
      log.info("Secrets file already exists in .deploy4j/secrets (remove first to create a new one)");
    } else {
      secretsFile.getParentFile().mkdirs();
      try (InputStream template = getTemplateStream("templates/secrets")) {
        FileUtils.copyInputStreamToFile(template, secretsFile);
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
      log.info("Created secrets file");
    }

    File hooksFolder = workingDirectory.resolve(".deploy4j/hooks").toFile();
    if(!hooksFolder.exists()) {
      hooksFolder.mkdirs();
      log.info("Created hooks folder");
    }

    // TODO: bundle add maven dependency?

  }

  private InputStream getTemplateStream(String templatePath) {
    ClassLoader classLoader = getClass().getClassLoader();
    InputStream stream = classLoader.getResourceAsStream(templatePath);
    if (stream == null) {
      stream = getClass().getResourceAsStream("/" + templatePath);
    }
    return Objects.requireNonNull(stream, "Missing template resource: " + templatePath);
  }

}
