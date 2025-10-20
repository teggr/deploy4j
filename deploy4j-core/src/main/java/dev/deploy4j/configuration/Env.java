package dev.deploy4j.configuration;

import dev.deploy4j.EnvFile;
import dev.deploy4j.env.ENV;
import dev.deploy4j.raw.EnvironmentConfig;

import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

import static dev.deploy4j.Utils.argumentize;

public class Env {

  private List<String> secretsKeys;
  private Map<String, String> clear;
  private String secretsFile;
  private String context;

  private Map<String, String> secrets;

  public Env(EnvironmentConfig config) {
    this(config, null, "env");
  }

  public Env(EnvironmentConfig config, String secretsFile, String context) {
    this.clear = config.isAMap() ? config.map() : config.isClearAndSecrets() ? config.clear() : Map.of();
    this.secretsKeys = config.secrets() != null ? config.secrets() : List.of();
    this.secretsFile = secretsFile;
    this.context = context;
    // TODO: context to be used with validation/error messages
  }

  public List<String> args() {
    List<String> args = new ArrayList<>();
    args.add("--env-file");
    args.add(secretsFile());
    args.addAll(Stream.of(argumentize("--env", clear())).toList());
    return args;
  }

  public String secretsIO() {
    return new EnvFile(secrets()).encode();
  }

  private Map<String, String> secrets() {
    if (secrets == null) {
      secrets = secretsKeys.stream()
        .collect(HashMap::new, (map, key) -> {
          map.put(key, ENV.fetch(key));
        }, HashMap::putAll);
    }
    return secrets;
  }

  public String secretsDirectory() {
    return Paths.get(secretsFile()).getParent().toString();
  }

  public Env merge(Env other) {

    Map<String, String> mergedClear = new HashMap<>(clear());
    mergedClear.putAll(other.clear());

    Set<String> mergedSecrets = new HashSet<>(secretsKeys());
    mergedSecrets.addAll(other.secretsKeys());

    EnvironmentConfig config = new EnvironmentConfig(
      mergedClear,
      mergedSecrets.stream().toList(),
      null,
      null
    );

    return new Env(
      null, //config,
      secretsFile() != null ? this.secretsFile() : other.secretsFile(),
      "env"
    );

  }

  // attributes

  public List<String> secretsKeys() {
    return secretsKeys;
  }

  public Map<String, String> clear() {
    return clear;
  }

  public String secretsFile() {
    return secretsFile;
  }

  public String context() {
    return context;
  }
}
