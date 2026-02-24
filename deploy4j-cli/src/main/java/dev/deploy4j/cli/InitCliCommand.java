package dev.deploy4j.cli;

import dev.deploy4j.init.InitConfig;
import dev.deploy4j.init.InitConfig.AgentType;
import dev.deploy4j.init.Initializer;
import picocli.CommandLine;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.Callable;

@CommandLine.Command(
  name = "init",
  description = "Interactively set up deploy4j configuration files for your project")
public class InitCliCommand implements Callable<Integer> {

  @CommandLine.Mixin
  private HelpOptions helpOptions = new HelpOptions();

  @CommandLine.Option(names = "--bundle", description = "Add Deploy4j to the maven file", defaultValue = "false")
  private boolean bundle;

  @Override
  public Integer call() throws Exception {

    Scanner scanner = new Scanner(System.in);

    System.out.println();
    System.out.println("👋 Welcome to deploy4j init!");
    System.out.println("   Let's get your project set up for deployment.");
    System.out.println();

    // Step 1: QuickStart details - hostname
    System.out.print("? Server IP address or hostname [localhost]: ");
    String hostname = scanner.nextLine().trim();
    if (hostname.isEmpty()) {
      hostname = InitConfig.DEFAULT_HOSTNAME;
    }

    // Step 2: QuickStart details - secret env var names
    System.out.println();
    System.out.print("? Secret environment variable names (comma-separated, press Enter to skip): ");
    String secretsInput = scanner.nextLine().trim();
    List<String> extraSecretNames = new ArrayList<>();
    if (!secretsInput.isEmpty()) {
      for (String name : secretsInput.split(",")) {
        String trimmed = name.trim();
        if (!trimmed.isEmpty()) {
          extraSecretNames.add(trimmed);
        }
      }
    }

    // Step 3: Detect AI agent and confirm
    AgentType agentType = detectAgentType();
    if (agentType != AgentType.NONE) {
      System.out.println();
      System.out.printf("? Detected %s — add deploy4j skills? [Y/n]: ", agentDisplayName(agentType));
      String answer = scanner.nextLine().trim().toLowerCase();
      if (answer.equals("n") || answer.equals("no")) {
        agentType = AgentType.NONE;
      }
    }

    System.out.println();
    System.out.println("📁 Setting up files...");
    System.out.println();

    InitConfig config = new InitConfig(bundle, hostname, extraSecretNames, agentType);
    new Initializer().init(config);

    System.out.println();
    System.out.println("✅ Done! Next steps:");
    System.out.println("   1. Review and edit config/deploy.yml");
    System.out.println("   2. Fill in your secrets in .deploy4j/secrets");
    System.out.println("   3. Run 'deploy4j setup <version>' for first-time deployment");
    System.out.println();

    return 0;

  }

  private AgentType detectAgentType() {
    if (new File("CLAUDE.md").exists()) {
      return AgentType.CLAUDE;
    }
    if (new File(".github/copilot-instructions.md").exists() || new File(".github").isDirectory()) {
      return AgentType.COPILOT;
    }
    return AgentType.NONE;
  }

  private String agentDisplayName(AgentType agentType) {
    return switch (agentType) {
      case COPILOT -> "GitHub Copilot";
      case CLAUDE -> "Claude";
      default -> "AI agent";
    };
  }

}

