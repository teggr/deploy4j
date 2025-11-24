package dev.deploy4j.deploy.configuration;

import dev.deploy4j.deploy.Secrets;
import dev.deploy4j.deploy.configuration.raw.EnvironmentConfig;
import dev.deploy4j.deploy.env.EnvFile;

import java.util.*;
import java.util.stream.Stream;

import static dev.rebelcraft.cmd.CmdUtils.argumentize;

public class Env {

  private final List<String> secretsKeys;
  private final Map<String, String> clear;
  private final Secrets secrets;
  private final String context;

  public Env(EnvironmentConfig config, Secrets secrets) {
    this(config, secrets, "env");
  }

  public Env(EnvironmentConfig config, Secrets secrets, String context) {
    if (config == null) {
      this.clear = Map.of();
      this.secretsKeys = List.of();
      this.secrets = secrets;
      this.context = context;
    } else {
      this.clear = config.isAMap() ? config.map() : config.isClearAndSecrets() ? config.clear() : Map.of();
      this.secretsKeys = config.secrets() != null ? config.secrets() : List.of();
      this.secrets = secrets;
      this.context = context;
    }
  }

  public List<String> clearArgs() {
    List<String> args = new ArrayList<>();
    args.addAll(Stream.of(argumentize("--env", clear())).toList());
    return args;
  }

  public String secretsIO() {
    return new EnvFile(aliasedSecrets()).encode();
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
      config,
      secrets,
      "env"
    );

  }

  // private

  private Map<String, String> aliasedSecrets() {
    return secretsKeys().stream()
      .map(this::extractAlias)
      .collect(
        HashMap::new,
        (map, pair) -> map.put(pair[0], secrets.get(pair[1])),
        HashMap::putAll
      );
  }

  private String[] extractAlias(String key) {
    String[] split = key.split(":");
    if (split.length == 2) {
      return split;
    }
    return new String[]{key, key};
  }

  // attributes

  public List<String> secretsKeys() {
    return secretsKeys;
  }

  public Map<String, String> clear() {
    return clear;
  }

  public String context() {
    return context;
  }
}
