package dev.deploy4j.commands;

import dev.deploy4j.configuration.Configuration;
import dev.deploy4j.file.Deploy4jFile;

import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

public class Hook extends Base {
  public Hook(Configuration config) {
    super(config);
  }

  public List<?> run(String hook, Map<String, String> details) {
    return List.of( hookFile(hook), tags(details).env() );
  }

  public boolean hookExists(String hook) {
    return Paths.get( hookFile(hook) ).toFile().exists();
  }

  // private

  public String hookFile(String hook) {
    return Deploy4jFile.join(config().hooksPath(), hook);
  }

}
