package dev.deploy4j.init;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class Initializer {

  private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(Initializer.class);

  /**
   * Create config stub in config/deploy.yml and env stub in .deploy4j/secrets
   */
  public void init(boolean bundle) {
    init(InitConfig.defaults(bundle));
  }

  /**
   * Create config stub in config/deploy.yml, env stub in .deploy4j/secrets,
   * and optionally an AI agent skills file, using the provided {@link InitConfig}.
   */
  public void init(InitConfig config) {

    File deployFile = new File("config/deploy.yml");
    if (deployFile.exists()) {
      log.info("Config file already exists in config/deploy.yml (remove first to create a new one)");
    } else {
      deployFile.getParentFile().mkdirs();
      try {
        String template = readTemplate("templates/deploy.yml");
        String content = template.replace("- localhost", "- " + config.getHostname());
        FileUtils.writeStringToFile(deployFile, content, StandardCharsets.UTF_8);
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
      log.info("Created configuration file in config/deploy.yml");
    }

    File secretsFile = new File(".deploy4j/secrets");
    if (secretsFile.exists()) {
      log.info("Secrets file already exists in .deploy4j/secrets (remove first to create a new one)");
    } else {
      secretsFile.getParentFile().mkdirs();
      try {
        String template = readTemplate("templates/secrets");
        StringBuilder secrets = new StringBuilder(template);
        for (String name : config.getExtraSecretNames()) {
          if (!name.isBlank()) {
            secrets.append(name.trim()).append("=\n");
          }
        }
        FileUtils.writeStringToFile(secretsFile, secrets.toString(), StandardCharsets.UTF_8);
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
      log.info("Created secrets file in .deploy4j/secrets");
    }

    File hooksFolder = new File(".deploy4j/hooks");
    if (!hooksFolder.exists()) {
      hooksFolder.mkdirs();
      log.info("Created hooks folder in .deploy4j/hooks");
    }

    initAgentSkills(config.getAgentType());

    // TODO: bundle add maven dependency?

  }

  private void initAgentSkills(InitConfig.AgentType agentType) {
    if (agentType == InitConfig.AgentType.COPILOT) {
      File copilotFile = new File(".github/copilot-instructions.md");
      copilotFile.getParentFile().mkdirs();
      try {
        String skills = readTemplate("templates/copilot-skills.md");
        if (copilotFile.exists()) {
          String existing = FileUtils.readFileToString(copilotFile, StandardCharsets.UTF_8);
          if (!existing.contains("deploy4j Deployment")) {
            FileUtils.writeStringToFile(copilotFile, existing + "\n" + skills, StandardCharsets.UTF_8);
            log.info("Added deploy4j skills to .github/copilot-instructions.md");
          } else {
            log.info("deploy4j skills already present in .github/copilot-instructions.md");
          }
        } else {
          FileUtils.writeStringToFile(copilotFile, skills, StandardCharsets.UTF_8);
          log.info("Created .github/copilot-instructions.md with deploy4j skills");
        }
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    } else if (agentType == InitConfig.AgentType.CLAUDE) {
      File claudeFile = new File("CLAUDE.md");
      try {
        String skills = readTemplate("templates/claude-skills.md");
        if (claudeFile.exists()) {
          String existing = FileUtils.readFileToString(claudeFile, StandardCharsets.UTF_8);
          if (!existing.contains("deploy4j Deployment")) {
            FileUtils.writeStringToFile(claudeFile, existing + "\n" + skills, StandardCharsets.UTF_8);
            log.info("Added deploy4j skills to CLAUDE.md");
          } else {
            log.info("deploy4j skills already present in CLAUDE.md");
          }
        } else {
          FileUtils.writeStringToFile(claudeFile, skills, StandardCharsets.UTF_8);
          log.info("Created CLAUDE.md with deploy4j skills");
        }
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
  }

  private String readTemplate(String resourcePath) throws IOException {
    try (InputStream is = getClass().getClassLoader().getResourceAsStream(resourcePath)) {
      if (is == null) {
        throw new IOException("Template not found: " + resourcePath);
      }
      return new String(is.readAllBytes(), StandardCharsets.UTF_8);
    }
  }

}
