package dev.deploy4j.init;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class Initializer {

  private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(Initializer.class);
  private final TemplateProcessor templateProcessor;

  public Initializer() {
    this.templateProcessor = new TemplateProcessor();
  }

  /**
   * Create config stub in config/deploy.yml and env stub in .deploy4j/secrets
   */
  public void init(boolean bundle) {
    init(bundle, null);
  }

  /**
   * Create config stub in config/deploy.yml and env stub in .deploy4j/secrets
   * @param bundle whether to add deploy4j to the maven file
   * @param serviceName custom service name to use in deploy.yml, or null for default
   */
  public void init(boolean bundle, String serviceName) {

    File deployFile = new File("config/deploy.yml");
    if (deployFile.exists()) {
      log.info("Config file already exists in config/deploy.yml (remove first to create a new one)");
    } else {
      deployFile.getParentFile().mkdirs();
      try {
        // Create model for template processing
        InitializationModel model = new InitializationModel(serviceName);
        
        // Process template using Thymeleaf
        String deployContent = templateProcessor.processTemplate("deploy.yml", model);
        
        FileUtils.writeStringToFile(deployFile, deployContent, StandardCharsets.UTF_8);
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
      log.info("Created configuration file in config/deploy.yml");
    }

    File secretsFile = new File(".deploy4j/secrets");
    if(secretsFile.exists()) {
      log.info("Secrets file already exists in .deploy4j/secrets (remove first to create a new one)");
    } else {
      secretsFile.getParentFile().mkdirs();
      try {
        FileUtils.copyInputStreamToFile(
          getClass().getClassLoader().getResourceAsStream("templates/secrets"),
          secretsFile
        );
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
      log.info("Created secrets file");
    }

    File hooksFolder = new File(".deploy4j/hooks");
    if(!hooksFolder.exists()) {
      hooksFolder.mkdirs();
      log.info("Created hooks folder");
    }

    // TODO: bundle add maven dependency?

  }

}
