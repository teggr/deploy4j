package dev.deploy4j.env;

import io.github.cdimascio.dotenv.Dotenv;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public class ENV {

  private final static List<Dotenv> envs = new ArrayList<>();

  static {
    Dotenv dotenv = Dotenv
      .configure()
      .ignoreIfMissing()
      .load();
    envs.add(dotenv);
  }

  public static void overload(String... envFiles) {
    envs.clear();
    Stream.of(envFiles)
      .forEach(envFile -> {
        Dotenv dotenv = Dotenv.configure()
          .filename(envFile)
          .ignoreIfMissing()
          .load();
        envs.add(dotenv);
      });
  }

  public static String fetch(String key) {
    return envs.stream()
      .map( env -> env.get(key) )
      .filter(Objects::nonNull) // value will be null if not found
      .reduce((first, second) -> second)
      .orElse(null);
  }

  public static String lookup(String key) {
    return envs.stream()
      .map( env -> env.get(key) )
      .filter(Objects::nonNull) // value will be null if not found
      .reduce((first, second) -> second)
      .orElse(key);
  }

}
