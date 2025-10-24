package dev.deploy4j.deploy.utils.file;

import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.apache.commons.io.FilenameUtils.getFullPathNoEndSeparator;

public class File {

  public static String join(String... paths) {
    return Stream.of(paths)
      .filter(Objects::nonNull)
      .collect(Collectors.joining("/"));
  }

  public static String dirname(String file) {
    return getFullPathNoEndSeparator(file);
  }

}
