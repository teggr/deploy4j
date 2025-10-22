package dev.deploy4j.deploy.utils;

import dev.deploy4j.deploy.configuration.Role;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class Utils {

  public static <T> List<T> filterSpecificItems(String[] filters, List<T> items) {
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

}
