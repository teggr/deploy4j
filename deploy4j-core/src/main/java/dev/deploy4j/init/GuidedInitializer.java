package dev.deploy4j.init;

import de.codeshelf.consoleui.prompt.ConsolePrompt;
import de.codeshelf.consoleui.prompt.InputResult;
import de.codeshelf.consoleui.prompt.builder.PromptBuilder;
import org.fusesource.jansi.AnsiConsole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Guided initialization that prompts user for configuration values
 */
public class GuidedInitializer {

  private static final Logger log = LoggerFactory.getLogger(GuidedInitializer.class);

  /**
   * Prompt the user for service name
   * @return the service name entered by the user
   */
  public String promptForServiceName() {
    try {
      AnsiConsole.systemInstall();
      
      ConsolePrompt prompt = new ConsolePrompt();
      PromptBuilder promptBuilder = prompt.getPromptBuilder();
      
      promptBuilder.createInputPrompt()
        .name("serviceName")
        .message("Enter service name")
        .defaultValue("deploy4j-demo")
        .addPrompt();
      
      HashMap<String, ? extends de.codeshelf.consoleui.prompt.PromtResultItemIF> result = prompt.prompt(promptBuilder.build());
      
      de.codeshelf.consoleui.prompt.PromtResultItemIF serviceNameResult = result.get("serviceName");
      if (serviceNameResult != null && serviceNameResult instanceof InputResult) {
        return ((InputResult) serviceNameResult).getInput();
      }
      
      return "deploy4j-demo";
    } catch (IOException e) {
      log.error("Error during guided initialization", e);
      return "deploy4j-demo";
    } finally {
      try {
        AnsiConsole.systemUninstall();
      } catch (Exception e) {
        log.warn("Error uninstalling AnsiConsole", e);
      }
    }
  }

  /**
   * Run guided initialization
   * @param bundle whether to bundle deploy4j
   * @return the Initializer with prompted configuration
   */
  public void runGuidedInit(boolean bundle) {
    String serviceName = promptForServiceName();
    log.info("Using service name: {}", serviceName);
    
    Initializer initializer = new Initializer();
    initializer.init(bundle, serviceName);
  }
}
