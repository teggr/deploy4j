package dev.deploy4j.env;

import io.github.cdimascio.dotenv.Dotenv;

public class ENV {

  private final static Dotenv env = Dotenv
    .configure()
    .load();

  public static String fetch(String key) {
    return env.get(key);
  }

  public static String lookup(String key) {
    return env.get(key, key);
  }

}
