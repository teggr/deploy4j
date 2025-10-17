package dev.deploy4j.configuration;

import dev.deploy4j.file.Deploy4jFile;
import dev.deploy4j.raw.HostListConfig;
import dev.deploy4j.raw.ServerRoleConfig;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static dev.deploy4j.Commands.argumentize;
import static java.util.Collections.emptyList;

public class Role {
  private final String name;
  private final Configuration config;
  private final Env specializedEnv;

  public Role(String name, Configuration config) {
    this.name = name;
    this.config = config;

    this.specializedEnv = new Env(
      specializations() != null ? specializations().env() : null,
      Deploy4jFile.join(config.hostEnvDirectory(), "roles", containerPrefix() + ".env"),
      "servers/%s/env".formatted(name)
    );

    // TODO: validate roleName

    // TODO: specialized roles

  }

  private ServerRoleConfig specializations() {
    if( config.rawConfig().servers() != null && !config.rawConfig().servers().isEmpty() ) {
      return null;
    }
    return config.rawConfig().serverRoles().get( name );
  }

  public String name() {
    return name;
  }

  public List<String> hosts() {
    return taggedHosts().keySet().stream().toList();
  }

  public boolean runningTraefik() {
    // if !role.traefik then is primary?
    // else return traefik
    return isPrimary();
  }

  private boolean isPrimary() {
    return this.equals(config.primaryRole());
  }

  public List<Tag> envTags(String host) {
    return taggedHosts().getOrDefault(host, emptyList())
      .stream()
      .flatMap(tag -> config.envTag(tag).stream())
      .toList();
  }

  private Map<String, List<String>> taggedHosts() {
    Map<String, List<String>> taggedHosts = extractHostsFromConfig()
      .stream()
      .collect(Collectors.toMap(
        HostListConfig::host,
        hostConfig -> hostConfig.tags() != null ? hostConfig.tags() : emptyList()
      ));
    return taggedHosts;

  }

  private List<HostListConfig> extractHostsFromConfig() {
    if (!config.rawConfig().servers().isEmpty()) {
      return config.rawConfig().servers();
    } else {
      ServerRoleConfig servers = config.rawConfig().serverRoles().get(name);
      return List.of(servers.hostListConfig());
    }
  }

  public String containerPrefix() {
    return Stream.of(
        config.service(),
        name,
        config.destination()
      ).filter(Objects::nonNull)
      .collect(Collectors.joining("-"));
  }

  public String primaryHost() {
    return hosts().get(0);
  }


  public String[] envArgs(String host) {
    return env(host).args();
  }

  public Env env(String host) {
    return Stream.concat(
        Stream.of(config.env()),
        //sepecialized env (role based
        envTags(host).stream().map(Tag::env)
      )
      .reduce(Env::merge)
      .orElse(new Env(Map.of()));
  }

  public List<String> healthCheckArgs() {
    return healthCheckArgs(true);
  }

  private List<String> healthCheckArgs(boolean cord) {

//    if( runningTraefik() || ( healthcheck() != null && healthcheck().setPortOrPath() ) ) {
//      // cord / using cord
//      return optionize(Map.of(
//          "health-cmd", healthcheck().cmd(),
//          "health-interval", healthcheck().interval()
//        )
//      );
//    } else {
    return emptyList();
//    }

  }

  private HealthCheck healthcheck() {
    if (runningTraefik()) {
      return config.healthcheck(); // merge specialised
    }
    return null; // specidalized healthcheck
  }

  public String[] loggingArgs() {
    return logging().args();
  }

  private Logging logging() {
    return config.logging(); // merge specialised
  }

  public List<String> assetVolumeArgs() {
    // new Volume
    return List.of();
  }

  public String[] labelArgs() {
    return argumentize("--label", labels());
  }

  private Map<String, String> labels() {
    Map<String, String> labels = new HashMap<>();
    labels.putAll(defaultLabels());
    labels.putAll(traefikLabels());
    labels.putAll(customLabels());
    return labels;
  }

  private Map<String, String> customLabels() {
    Map<String, String> labels = new HashMap<>();
    labels.putAll(config.labels());
    // specialized labels
    return labels;
  }

  private Map<String, String> traefikLabels() {
    if (runningTraefik()) {
      String traefikService = traefikService();
      return Map.of(
        // Setting a service property ensures that the generated service name will be consistent between versions
        "traefik.http.services." + traefikService + ".loadbalancer.server.scheme", "http",

        "traefik.http.routers." + traefikService + ".rule", "PathPrefix(`/`)",
        "traefik.http.routers." + traefikService + ".priority", "2",
        "traefik.http.middlewares." + traefikService + "-retry.retry.attempts", "5",
        "traefik.http.middlewares." + traefikService + "-retry.retry.initialinterval", "500ms",
        "traefik.http.routers." + traefikService + ".middlewares", "" + traefikService + "-retry@docker"
      );
    }
    return Map.of();
  }

  private String traefikService() {
    return containerPrefix();
  }

  private Map<String, String> defaultLabels() {
    Map<String, String> defaultLabels = new HashMap<>();
    defaultLabels.put("service", config.service());
    defaultLabels.put("role", name);
    defaultLabels.put("destination", config.destination());
    return defaultLabels;
  }

  public List<String> optionArgs() {
    // specialized options
    return List.of();
  }

  public List<String> cmd() {
    return null;
  }
}
