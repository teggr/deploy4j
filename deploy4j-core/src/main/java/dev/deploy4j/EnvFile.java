package dev.deploy4j;

import org.apache.commons.lang.StringEscapeUtils;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * Encode an env hash as a string where secret values have been
 * looked up and all values escaped for Docker.
 */
public class EnvFile {

  private final Map<String, String> env;

  public EnvFile(Map<String, String> env ) {
    this.env = env;
  }

  public String encode() {

    String envFile = env.entrySet().stream()
      .sorted(Map.Entry.comparingByKey())
      .map(e -> dockerEnvFileLine(e.getKey(), e.getValue()))
      .collect(Collectors.joining());
    if(envFile.isEmpty()) {
      envFile += "\n";
    }
    return envFile;

  }

  private String dockerEnvFileLine(String key, String value) {
    return "%s=%s\n".formatted( key, escapeDockerEnvFileValue(value) );
  }

  /**
   * Escape a value to make it safe to dump in a docker file.
   */
  private String escapeDockerEnvFileValue(Object value) {
    String str = value == null ? "" : value.toString();
    StringBuilder result = new StringBuilder();
    int i = 0;
    while (i < str.length()) {
      int codePoint = str.codePointAt(i);
      if (codePoint <= 0x7F) {
        String asciiPart = new String(Character.toChars(codePoint));
        result.append(escapeDockerEnvFileAsciiValue(asciiPart));
      } else {
        result.append(new String(Character.toChars(codePoint)));
      }
      i += Character.charCount(codePoint);
    }
    return result.toString();
  }

  private String escapeDockerEnvFileAsciiValue(String value) {
    // Remove leading and trailing double quotes, unescape any others
    String dumped = StringEscapeUtils.escapeJava(value);
    if (dumped.startsWith("\"") && dumped.endsWith("\"") && dumped.length() > 1) {
      dumped = dumped.substring(1, dumped.length() - 1);
    }
    return dumped.replace("\\\"", "\"");
  }

}
