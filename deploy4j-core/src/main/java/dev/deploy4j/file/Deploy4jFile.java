package dev.deploy4j.file;

import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Deploy4jFile {

  public static String join(String... paths) {
    return Stream.of(paths)
      .filter(Objects::nonNull)
      .collect(Collectors.joining("/"));
  }

}
