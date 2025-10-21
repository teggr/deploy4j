package dev.deploy4j;

import dev.deploy4j.configuration.Role;
import dev.rebelcraft.cmd.CmdUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
    return CmdUtils.optionize(args);
  }

  public static List<String> optionize(Map<String, String> args, String with) {
    return CmdUtils.optionize(args, with);
  }

  public static String[] argumentize(String argument, String... attributes) {
    return CmdUtils.argumentize(argument, attributes);
  }

  public static String[] argumentize(String argument, List<String> attributes) {
    return CmdUtils.argumentize(argument, attributes);
  }

  public static String[] argumentize(String argument, Map<String, String> attributes) {
    return CmdUtils.argumentize(argument, attributes);
  }

  public static String escapeShellValue(Object value) {
    return CmdUtils.escapeShellValue(value);
  }

  // TODO: return a sensitive value wrapper that masks the value in logs
  public static String sensitive(String s) {
    return s;
  }

}
