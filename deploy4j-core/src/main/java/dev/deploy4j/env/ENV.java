package dev.deploy4j.env;

import java.util.*;

/**
 * ENV provides access to environment variables. These are initially loaded from the system environment.
 * <p/>
 * You can also load environment variables
 */
public class ENV {

  private static final Map<String, String> env = new HashMap<>();

  static {

    // load system envs first time
    loadSystemEnv();

  }

  private static void loadSystemEnv() {
    env.putAll( System.getenv() );
  }

  public static Map<String, String> toHash() {
    return Map.copyOf( env );
  }

  public static void clear() {
    env.clear();
  }

  public static void update(Map<String, String> newEnv) {
    env.putAll( newEnv );
  }

  public static String fetch(String key) {
    return env.get(key);
  }

  public static void delete(String key) {
    env.remove(key);
  }

}
