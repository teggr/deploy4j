package dev.deploy4j.deploy;

import dev.deploy4j.deploy.env.ENV;
import dev.deploy4j.deploy.env.ENVDotenv;

import java.util.Map;
import java.util.function.Supplier;

public class Environment {

  private final String destination;
  private final Map<String, String> originalEnv;

  public Environment(String destination) {
    this.destination = destination;
    originalEnv = ENV.toHash();
    loadEnv();
  }

  public void reloadEnv() {
    resetEnv();
    loadEnv();
  }

  public void loadEnv() {
    if (destination != null) {
      ENVDotenv.overload(".env", ".env." + destination);
    } else {
      ENVDotenv.overload(".env");
    }
  }

  public void resetEnv() {
    replaceEnv(originalEnv);
  }

  public void replaceEnv(Map<String, String> env) {
    ENV.clear();
    ENV.update(env);
  }

  public <T> T withOriginalEnv(Supplier<T> runnable) {
    return keepingCurrentEnv(() -> {
      resetEnv();
      return runnable.get();
    });
  }

  public <T> T keepingCurrentEnv(Supplier<T> runnable) {
    Map<String, String> currentEnv = ENV.toHash();
    try {
      return runnable.get();
    } finally {
      replaceEnv(currentEnv);
    }
  }

}
