package dev.deploy4j.deploy;

import dev.deploy4j.deploy.env.Dotenv;
import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Secrets {

  private final String destination;

  private Map<String, String> secrets;

  public Secrets(String destination) {
    this.destination = destination;
  }

  public String get(String key) {
    if (secrets().containsKey(key)) {
      return secrets().get(key);
    } else {
      throw new RuntimeException("Secret '" + key + "' not found in " + String.join(", ", secretsFiles()));
    }
  }

  private List<String> secretsFiles() {
    return Arrays.stream(secretsFilenames())
      .filter(f -> new File(f).exists()
      ).toList();
  }

  // private

  private Map<String, String> secrets() {
    if (secrets == null) {
      secrets = new HashMap<>();
      secretsFiles().forEach(filename -> {
        secrets.putAll(Dotenv.parse(filename));
      });
    }
    return secrets;
  }

  private String[] secretsFilenames() {
    return new String[]{
      ".deploy4j/secrets-common",
      ".deploy4j/secrets" + (StringUtils.isNotBlank(destination) ? "." + destination : "")
    };
  }

}
