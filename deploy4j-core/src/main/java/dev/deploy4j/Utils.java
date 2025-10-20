package dev.deploy4j;

import dev.deploy4j.configuration.Role;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class Utils {
  static <T> List<T> filterSpecificItems(String[] filters, List<T> items) {
    List<T> matches = new ArrayList<>();
    Stream.of(filters)
      .forEach(filter -> {
        items.stream()
          .filter(item -> {

              if (item instanceof String) {
                return ((String) item).equalsIgnoreCase(filter);
              } else if (item instanceof Role) {
                return ((Role) item).name().equalsIgnoreCase(filter);
              } else {
                return false;
              }

            }
          )
          .forEach(matches::add);
      });
    return matches.stream().distinct().toList();
  }

  public static List<String> optionize(Map<String, String> args) {
    return optionize(args, null);
  }

  /**
   * Builds a list of shell options like Ruby's optionize.
   *
   * Example:
   * optionize(Map.of("publish","8080", "detach","true"), "=")
   * => ["--publish=8080", "--detach=true"]
   *
   * optionize(Map.of("publish","8080", "name","myapp"), null)
   * => ["--publish", "8080", "--name", "myapp"]
   */
  public static List<String> optionize(Map<String, String> args, String with) {
    if (args == null || args.isEmpty()) {
      return List.of();
    }

    List<String> options = new ArrayList<>();

    for (Map.Entry<String, String> entry : args.entrySet()) {
      String key = entry.getKey();
      String value = entry.getValue();

      if ("true".equalsIgnoreCase(value)) {
        // treat as flag
        options.add("--" + key + with + value);
      } else
        if (value != null) {
        if (with != null) {
          // single token: --key=value
          options.add("--" + key + with + escapeShellValue(value));
        } else {
          // two tokens: --key value
          options.add("--" + key);
          options.add(escapeShellValue(value));
        }
      }
      // null values are skipped (like Ruby's compact)
    }

    return options;
  }

  /**
   * Builds a list of shell arguments like Ruby's argumentize.
   *
   * Example:
   * argumentize("-e", List.of("USER", "DEBUG"))
   * => ["-e", "USER", "-e", "DEBUG"]
   */
  public static String[] argumentize(String argument, String... attributes) {
    return argumentize(argument, Stream.of(attributes).toList());
  }

  /**
   * Builds a list of shell arguments like Ruby's argumentize.
   *
   * Example:
   * argumentize("-e", List.of("USER", "DEBUG"))
   * => ["-e", "USER", "-e", "DEBUG"]
   */
  public static String[] argumentize(String argument, List<String> attributes) {
    List<String> list = attributes.stream()
      //.map( Commands::escapeShellValue )
      .flatMap(attr -> Stream.of(argument, attr))
      .toList();
    return list.toArray(new String[0]);
  }

  /**
   * Builds a list of shell arguments like Ruby's argumentize.
   *
   * Example:
   * argumentize("-e", Map.of("USER", "root", "DEBUG", null))
   * => ["-e", "USER=root", "-e", "DEBUG"]
   */
  public static String[] argumentize(String argument, Map<String, String> attributes) {
    List<String> args = new ArrayList<>();
    if (attributes == null || attributes.isEmpty()) {
      return new String[]{};
    }

    for (Map.Entry<String, String> entry : attributes.entrySet()) {
      String key = entry.getKey();
      String value = entry.getValue();

      if (value != null && !value.isBlank()) {
        String attr = key + "=" + escapeShellValue(value);
        args.add(argument);
        args.add(attr);
      } else {
        args.add(argument);
        args.add(key);
      }
    }

    return args.toArray(new String[0]);
  }

  public static String escapeShellValue(Object value) {
    if (value == null) return "\"\"";

    String s = value.toString();

    // Step 1: Rough equivalent of Ruby's dump (escape special chars, wrap in quotes)
    StringBuilder sb = new StringBuilder();
    sb.append("\"");
    for (char c : s.toCharArray()) {
      switch (c) {
        case '\\': sb.append("\\\\"); break;
        case '"':  sb.append("\\\""); break;
        case '\n': sb.append("\\n"); break;
        case '\r': sb.append("\\r"); break;
        case '\t': sb.append("\\t"); break;
        default:
          if (c < 0x20 || c == 0x7F) {
            // control chars -> \\uXXXX
            sb.append(String.format("\\u%04x", (int) c));
          } else {
            sb.append(c);
          }
      }
    }
    sb.append("\"");

    String escaped = sb.toString();

    // Step 2: Escape backticks
    escaped = escaped.replace("`", "\\`");

    // Step 3: Escape $ unless it is ${...}
    Matcher m = DOLLAR_SIGN_WITHOUT_SHELL_EXPANSION.matcher(escaped);
    escaped = m.replaceAll("\\\\\\$");

    return escaped;
  }

  // TODO: return a sensitive value wrapper that masks the value in logs
  public static String sensitive(String s) {
    return s;
  }

  static final Pattern DOLLAR_SIGN_WITHOUT_SHELL_EXPANSION =
    Pattern.compile("\\$(?!\\{[^}]*\\})");
}
