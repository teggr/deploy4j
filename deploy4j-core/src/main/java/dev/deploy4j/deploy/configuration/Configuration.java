package dev.deploy4j.deploy.configuration;

import dev.deploy4j.deploy.configuration.env.Tag;
import dev.deploy4j.deploy.configuration.raw.DeployConfig;
import dev.deploy4j.deploy.configuration.raw.DeployConfigYamlReader;
import dev.deploy4j.deploy.configuration.raw.EnvironmentConfig;
import dev.deploy4j.deploy.env.ENV;
import dev.deploy4j.deploy.utils.RandomHex;
import dev.deploy4j.deploy.utils.file.File;
import org.apache.commons.lang.StringUtils;

import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static dev.rebelcraft.cmd.CmdUtils.argumentize;

public class Configuration {

  public static Configuration createFrom(ConfigureArgs configureArgs) {
    DeployConfig rawConfig = DeployConfigYamlReader.loadConfigFiles(
      Stream.of(
          configureArgs.configFile(),
          destinationConfigFile(configureArgs.configFile(), configureArgs.destination()))
        .filter(Objects::nonNull).toList()
    );
    return new Configuration(rawConfig, configureArgs.destination(), configureArgs.version());
  }

  private static String destinationConfigFile(String baseConfigFile, String destination) {
    if (destination != null) {
      String baseName = baseConfigFile.substring(0, baseConfigFile.lastIndexOf('.'));
      return baseName + "." + destination + ".yml";
    }
    return null;
  }

  public static Configuration createFrom(String configFile, String destination, String version) {
    return createFrom(new ConfigureArgs(configFile, destination, version));
  }

  private final String destination;
  private final DeployConfig rawConfig;

  private final List<Accessory> accessories;
  private final Boot boot;
  private final Builder builder;
  private final Env env;
  private final HealthCheck healthcheck;
  private final Logging logging;
  private final Traefik traefik;
  private final Servers servers;
  private final Ssh ssh;
  private final Registry registry;

  private String declaredVersion;
  private String runId;
  private List<Tag> envTags;


  public Configuration(
    DeployConfig rawConfig,
    String destination,
    String version
  ) {
    this.rawConfig = rawConfig;
    this.destination = destination;
    this.declaredVersion = version;

    // TODO: validate config

    this.servers = new Servers(this);
    this.registry = new Registry(this);

    this.accessories = Optional
      .ofNullable(rawConfig.accessories())
      .orElse(Map.of())
      .keySet().stream().map(name -> new Accessory(name, this)).toList();
    this.boot = new Boot(this);
    this.builder = new Builder(this);
    this.env = new Env(rawConfig.env());

    this.healthcheck = new HealthCheck(rawConfig.healthCheck(), null);
    this.logging = new Logging(rawConfig.logging(), null);
    this.traefik = new Traefik(this);
    this.ssh = new Ssh(this);

    // TODO: more validations
//    ensure_destination_if_required
//      ensure_required_keys_present
//    ensure_valid_kamal_version
//      ensure_retain_containers_valid
//    ensure_valid_service_name

  }

  public void version(String version) {
    declaredVersion = version;
  }

  public String version() {
    // declared version || ENV version || git commit hash || "latest"
    if (StringUtils.isNotBlank(declaredVersion)) {
      return declaredVersion;
    }
    String env = ENV.fetch("VERSION");
    if (env != null) {
      return env;
    }
    return null; // git version
  }

  public String abbreviatedVersion() {
    if (version() != null) {
      if (version().contains("_")) {
        return version();
      } else {
        return version().substring(0, 7);
      }
    }
    return null;
  }

  public String minumimVersion() {
    return rawConfig().minimumVersion();
  }

  public List<Role> roles() {
    return servers.roles();
  }

  public Role role(String name) {
    return roles().stream()
      .filter(r -> r.name().equals(name))
      .findFirst()
      .orElse(null);
  }

  public Accessory accessory(String name) {
    return accessories.stream()
      .filter(a -> a.name().equals(name))
      .findFirst()
      .orElse(null);
  }

  public List<String> allHosts() {
    List<String> hosts = new ArrayList<>();
    roles().stream().flatMap(role -> role.hosts().stream())
      .forEach(hosts::add);
    accessories().stream().flatMap(a -> a.hosts().stream()).forEach(hosts::add);
    return hosts.stream().distinct().collect(Collectors.toList());
  }

  public String primaryHost() {
    if (primaryRole() != null) {
      return primaryRole().primaryHost();
    }
    return null;
  }

  private String primaryRoleName() {
    return rawConfig.primaryRole() != null ? rawConfig.primaryRole() : "web";
  }

  public Role primaryRole() {
    return role(primaryRoleName());
  }

  public Boolean allowEmptyRoles() {
    return rawConfig().allowEmptyRoles();
  }

  private List<Role> traefikRoles() {
    return roles().stream()
      .filter(Role::runningTraefik)
      .toList();
  }

  private List<String> traefikRoleNames() {
    return traefikRoles().stream()
      .map(Role::name)
      .toList();
  }

  public List<String> traefikHosts() {
    return traefikRoles().stream()
      .flatMap(r -> r.hosts().stream())
      .distinct()
      .toList();
  }

  public String repository() {
    return Stream.of(registry().server(), image())
      .filter(StringUtils::isNotBlank)
      .collect(Collectors.joining("/"));
  }

  public String absoluteImage() {
    return "%s:%s".formatted(repository(), version());
  }

  public String latestImage() {
    return "%s:%s".formatted(repository(), latestTag());
  }

  public String latestTag() {
    return Stream.of("latest", destination())
      .filter(Objects::nonNull)
      .collect(Collectors.joining("-"));
  }

  private String serviceWithVersion() {
    return "%s-%s".formatted(service(), version());
  }

  public Boolean requireDestination() {
    return rawConfig().requireDestination();
  }

  public int retainContainer() {
    return rawConfig().retainContainers() != null ?
      rawConfig().retainContainers() : 5;
  }

  public String[] volumeArgs() {
    if (rawConfig().volumes() != null) {
      return argumentize("--volume", rawConfig().volumes());
    }
    return new String[]{};
  }

  public String[] loggingArgs() {
    return logging().args();
  }

  public String healthcheckService() {
    return Stream.of("healthcheck", service(), destination())
      .filter(StringUtils::isNotBlank)
      .collect(Collectors.joining("-"));
  }

  public Integer readinessDelay() {
    return rawConfig().readinessDelay();
  }

  public String runId() {
    if (runId == null) {
      runId = RandomHex.randomHex(16);
    }
    return runId;
  }

  public String runDirectory() {
    return rawConfig().runDirectory() != null ?
      rawConfig().runDirectory() : ".deploy4j";
  }

  public String runDirectoryAsDockerVolume() {
    if (Paths.get(runDirectory()).isAbsolute()) {
      return runDirectory();
    } else {
      return File.join("$(pwd)", runDirectory());
    }
  }

  public String hooksPath() {
    return rawConfig().hooksPath() != null ?
      rawConfig().hooksPath() : ".deploy4j/hooks";
  }

  public String assetPath() {
    return rawConfig().assetPath();
  }

  public String hostEnvDirectory() {
    return File.join(runDirectory(), "env");
  }

  private List<Tag> envTags() {
    if (envTags == null) {
      Map<String, EnvironmentConfig> tags = rawConfig().env().tags();
      if (tags != null) {
        tags.entrySet().stream().forEach(entry -> {
          envTags.add(new Tag(entry.getKey(), entry.getValue()));
        });
      } else {
        envTags = List.of();
      }
    }
    return envTags;
  }

  public List<Tag> envTag(String name) {
    return envTags().stream()
      .filter(t -> t.name().equals(name))
      .toList();
  }

  public Map<String, Object> resolve() {
    Map<String, Object> map = new HashMap<>();
    map.put("roles", roleNames());
    map.put("hosts", allHosts());
    map.put("primaryHost", primaryHost());
    map.put("version", version());
    map.put("repository", repository());
    map.put("absoluteImage", absoluteImage());
    map.put("serviceWithVersion", serviceWithVersion());
    map.put("volumeArgs", volumeArgs());
    map.put("ssh", ssh().resolve());
    map.put("builder", builder().resolve());
    map.put("accessories", rawConfig().accessories());
    map.put("logging", loggingArgs());
    map.put("healthcheck", healthcheck().resolve());
    return map;
  }

  // private

  // TODO: ensure_destination_if_required
  // TODO: ensure_required_keys_present
  // TODO: ensure_valid_service_name
  // TODO: ensure_valid_kamal_version
  // TODO: ensure_retain_containers_valid

  public List<String> roleNames() {
    if (rawConfig().servers().isAList()) {
      return List.of("web");
    } else {
      return rawConfig().servers().roles().keySet()
        .stream().sorted().toList();
    }

  }

  // TODO: git_version

  // attribute readers

  public String destination() {
    return destination;
  }

  public DeployConfig rawConfig() {
    return rawConfig;
  }

  public List<Accessory> accessories() {
    return accessories;
  }

  public Boot boot() {
    return boot;
  }

  public Builder builder() {
    return builder;
  }

  public Env env() {
    return env;
  }

  public HealthCheck healthcheck() {
    return healthcheck;
  }

  public Logging logging() {
    return logging;
  }

  public Traefik traefik() {
    return traefik;
  }

  public Servers servers() {
    return servers;
  }

  public Ssh ssh() {
    return ssh;
  }

  public Registry registry() {
    return registry;
  }

  // delegates

  public String service() {
    return rawConfig().service();
  }

  public String image() {
    return rawConfig().image();
  }

  public Map<String, String> labels() {
    return rawConfig().labels();
  }

  public Integer stopWaitTime() {
    return rawConfig().stopWaitTime();
  }

}
