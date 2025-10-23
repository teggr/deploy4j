package dev.deploy4j.deploy.configuration;

import dev.deploy4j.deploy.configuration.env.Tag;
import dev.deploy4j.deploy.configuration.raw.*;
import dev.deploy4j.deploy.utils.file.File;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static dev.rebelcraft.cmd.CmdUtils.argumentize;
import static dev.rebelcraft.cmd.CmdUtils.optionize;
import static java.util.Collections.emptyList;

public class Role {

  private static final String CORD_FILE = "cord";

  private final String name;
  private final Configuration config;
  private final Env specializedEnv;
  private final Logging specializedLogging;
  private final HealthCheck specializedHealthCheck;
  private Logging logging;
  private Map<String, Env> envs;
  private HealthCheck healthCheck;
  private Volume cordVolume;

  public Role(String name, Configuration config) {
    this.name = name;
    this.config = config;

    // TODO: validate

    this.specializedEnv = new Env(
      specializations().env() != null ? specializations().env() : new EnvironmentConfig(),
      File.join(config().hostEnvDirectory(), "roles", containerPrefix() + ".env"),
      "servers/%s/env".formatted(name())
    );

    this.specializedLogging = new Logging(
      specializations().logging() != null ? specializations().logging() : new LoggingConfig(),
      "servers/%s/logging".formatted(name)
    );

    this.specializedHealthCheck = new HealthCheck(
      specializations().healthcheck() != null ? specializations().healthcheck() : new HealthCheckConfig(),
      "servers/%s/healthcheck".formatted(name)
    );

  }

  public String primaryHost() {
    return hosts().get(0);
  }

  public List<String> hosts() {
    return taggedHosts().keySet().stream().toList();
  }

  public List<Tag> envTags(String host) {
    return taggedHosts().getOrDefault(host, emptyList())
      .stream()
      .flatMap(tag -> config().envTag(tag).stream())
      .toList();
  }

  public String cmd() {
    return specializations().cmd();
  }

  public List<String> optionArgs() {
    if (specializations().options() != null) {
      return optionize(specializations().options());
    } else {
      return List.of();
    }
  }

  public Map<String, String> labels() {
    Map<String, String> labels = new HashMap<>();
    labels.putAll(defaultLabels());
    labels.putAll(traefikLabels());
    labels.putAll(customLabels());
    return labels;
  }

  public String[] labelArgs() {
    return argumentize("--label", labels());
  }

  public String[] loggingArgs() {
    return logging().args();
  }

  private Logging logging() {
    if (logging == null) {
      logging = config().logging().merge(specializedLogging());
    }
    return logging;
  }

  public Env env(String host) {
    if (envs == null) {
      envs = new HashMap<>();
    }
    Env env = envs.get(host);
    if (env == null) {
      env = Stream.concat(
          Stream.of(config().env(), specializedEnv()),
          envTags(host).stream().map(Tag::env)
        ).reduce(Env::merge)
        .orElse(new Env(new EnvironmentConfig()));
      envs.put(host, env);
    }
    return env;
  }

  public List<String> envArgs(String host) {
    return env(host).args();
  }

  public String[] assetVolumeArgs() {
    return assetVolume(null) != null ? assetVolume(null).dockerArgs() : new String[]{};
  }

  public List<String> healthCheckArgs() {
    return healthCheckArgs(true);
  }

  private List<String> healthCheckArgs(boolean cord) {

    if( runningTraefik() || healthcheck().setPortOrPath() ) {
      if( cord && usesCord() ) {
        List<String> optionize = optionize(Map.of(
          "health-cmd", healthCheckCmdWithCord(),
          "health-interval", healthcheck().interval()
        ));
        return Stream.concat(
          optionize.stream(),
          Stream.of( cordVolume().dockerArgs() )
        ).toList();
      } else {
        return optionize(Map.of(
            "health-cmd", healthcheck().cmd(),
            "health-interval", healthcheck().interval()
          )
        );
      }
    } else {
      return emptyList();
    }

  }

  public HealthCheck healthcheck() {
    if(healthCheck == null) {
      if (runningTraefik()) {
        return config().healthcheck().merge(specializedHealthCheck()); // merge specialised
      } else {
        healthCheck = specializedHealthCheck();
      }
    }
    return healthCheck;
  }

  public String healthCheckCmdWithCord() {
    return "(%s) && (stat %s > /dev/null || exit 1)"
      .formatted( healthcheck().cmd(), cordContainerFile() );
  }

  public boolean runningTraefik() {
    if( specializations().traefik() == null ) {
      return primary();
    } else {
      return specializations().traefik();
    }
  }

  private boolean primary() {
    return this.equals(config.primaryRole());
  }

  public boolean usesCord() {
    return runningTraefik() && cordVolume() != null && healthcheck().cmd() != null;
  }

  public String cordHostDirectory() {
    return File.join( config().runDirectoryAsDockerVolume(), "cords", Stream.of( containerPrefix(), config().runId() ).collect(Collectors.joining("-"))  );
  }

  public Volume cordVolume() {
    String cord = healthcheck().cord();
    if(cord != null) {
      if(cordVolume == null) {
        cordVolume = new Volume(
          File.join( config().runDirectory(), "cords", Stream.of( containerPrefix(), config().runId() ).collect(Collectors.joining("-"))  ),
          cord
        );
      }
    }
    return cordVolume;
  }

  public String cordHostFile() {
    return File.join( cordVolume().hostPath(), CORD_FILE );
  }

  // TODO: unused?
//  public String cordContainerDirectory() {
//    return  healthcheck().options().get("cord");
//  }

  public String cordContainerFile() {
    return File.join( cordVolume().containerPath(), CORD_FILE );
  }

  public String containerName(String version) {
    return Stream.of(
        containerPrefix(),
        version != null ? version  : config().version()
      ).filter(Objects::nonNull)
      .collect(Collectors.joining("-"));
  }

  public String containerPrefix() {
    return Stream.of(
        config().service(),
        name(),
        config().destination()
      ).filter(Objects::nonNull)
      .collect(Collectors.joining("-"));
  }

  public String assetPath() {
    return specializations().assetPath() != null ?
      specializations().assetPath() :
      config().assetPath() != null ?
        config().assetPath() : null;
  }

  public boolean assets() {
    return assetPath() != null && runningTraefik();
  }

  public Volume assetVolume(String version) {
    if (assets()) {
      return new Volume(
        assetVolumePath(version),
        assetPath()
      );
    }
    return null;
  }

  public String assetExtractedPath(String version) {
    return File.join( config().runDirectory(), "assets", "extracted", containerName(version) );
  }

  public String assetVolumePath(String version) {
    return File.join( config().runDirectory(), "assets", "volumes", containerName(version) );
  }

  // private

  private Map<String, List<String>> taggedHosts() {
    return extractHostsFromConfig()
      .stream()
      .collect(Collectors.toMap(
        ServerConfig::host,
        hostConfig -> hostConfig.tags() != null ? hostConfig.tags() : emptyList()
      ));
  }

  private List<ServerConfig> extractHostsFromConfig() {
    if (config().rawConfig().servers().isAList()) {
      return config().rawConfig().servers().list();
    } else {
      RoleConfig servers = config().rawConfig().servers().roles().get(name());
      if(servers.isAList()) {
        return servers.list();
      } else {
        return servers.customRole().hosts();
      }
    }
  }

  private Map<String, String> defaultLabels() {
    Map<String, String> defaultLabels = new HashMap<>();
    defaultLabels.put("service", config().service());
    defaultLabels.put("role", name());
    defaultLabels.put("destination", config().destination());
    return defaultLabels;
  }

  private CustomRoleConfig specializations() {
    if (config().rawConfig().servers().isAList() ||
        (config().rawConfig().servers().roles().get(name()) != null
         && config().rawConfig().servers().roles().get(name()).isAList())) {
      return new CustomRoleConfig();
    } else {
      return config().rawConfig().servers().roles().get(name()).customRole();
    }
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

  private Map<String, String> customLabels() {
    Map<String, String> labels = new HashMap<>();
    labels.putAll(config().labels());
    if(specializations().labels() != null) labels.putAll( specializations().labels() );
    return labels;
  }

  // attributes

  public String name() {
    return name;
  }

  public Configuration config() {
    return config;
  }

  public Env specializedEnv() {
    return specializedEnv;
  }

  public Logging specializedLogging() {
    return specializedLogging;
  }

  public HealthCheck specializedHealthCheck() {
    return specializedHealthCheck;
  }
}
