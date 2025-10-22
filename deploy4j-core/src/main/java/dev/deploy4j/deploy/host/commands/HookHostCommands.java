package dev.deploy4j.deploy.host.commands;

import dev.deploy4j.deploy.configuration.Configuration;
import dev.deploy4j.deploy.utils.file.File;

import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

public class HookHostCommands extends BaseHostCommands {

  public HookHostCommands(Configuration config) {
    super(config);
  }

  public List<?> run(String hook, Map<String, String> details) {
    return List.of(hookFile(hook), tags(details).env());
  }

  public boolean hookExists(String hook) {
    return Paths.get(hookFile(hook)).toFile().exists();
  }

  // private

  public String hookFile(String hook) {
    return File.join(config().hooksPath(), hook);
  }

}
