package dev.deploy4j.configuration;

import dev.deploy4j.env.ENV;
import dev.deploy4j.raw.Deploy4jConfig;
import org.apache.commons.lang.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Configuration {

  public static final String DEFAULT_RUN_DIRECTORY = ".deploy4j";

  private final Deploy4jConfig rawConfig;
  private final String destination;
  private final String declaredVersion;

  private final Servers servers;
  private final Registry registry;
  private final List<Accessory> accessories;
  private final Boot boot;
  private final Builder builder;
  private final Env env;
  private final HealthCheck healthcheck;
  private final Logging logging;
  private final Traefik traefik;
  private final Ssh ssh;

  public Configuration(
    Deploy4jConfig rawConfig,
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
    this.env = new Env(rawConfig.env() != null ? rawConfig.env() : new HashMap<String, String>());

    this.healthcheck = new HealthCheck(rawConfig.healthCheck());
    this.logging = new Logging(rawConfig.logging());
    this.traefik = new Traefik(this);
    this.ssh = new Ssh(this);

    // TODO: more validations
//    ensure_destination_if_required
//      ensure_required_keys_present
//    ensure_valid_kamal_version
//      ensure_retain_containers_valid
//    ensure_valid_service_name

  }

  public String service() {
    return rawConfig().service();
  }

  public String image() {
    return rawConfig().image();
  }

  public Map<String, String> labels() {
    return rawConfig().labels();
  }

  // stop wait tmie
  // hooks path

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

  public String destination() {
    return destination;
  }

  Deploy4jConfig rawConfig() {
    return rawConfig;
  }

  public Servers servers() {
    return servers;
  }

  public Registry registry() {
    return registry;
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

  public Ssh ssh() {
    return ssh;
  }

  public String runDirectory() {
    return ".deploy4j";
  }

  public String absoluteImage() {
    return "%s:%s".formatted(repository(), version());
  }


  private String repository() {
    return Stream.of(registry().server(), image())
      .filter(StringUtils::isNotBlank)
      .collect(Collectors.joining("/"));
  }

  public Role primaryRole() {
    return role(primaryRoleName());
  }

  public Role role(String name) {
    return roles().stream()
      .filter(r -> r.name().equals(name))
      .findFirst()
      .orElse(null);
  }

  private String primaryRoleName() {
    return rawConfig.primaryRole() != null ? rawConfig.primaryRole() : "web";
  }

  public String latestImage() {
    return "%s:%s".formatted(repository(), latestTag());
  }

  public String latestTag() {
    return Stream.of("latest", destination())
      .collect(Collectors.joining("-"));
  }

  public String stopWaitTime() {
    return null;  // TODO: support stop wait time
  }

  public List<Role> roles() {
    return servers.roles();
  }

  public List<String> allHosts() {
    // TODO acessories + unique
    return roles().stream().flatMap(role -> role.hosts().stream())
      .distinct().toList();
  }

  public String primaryHost() {
    if (primaryRole() != null) {
      return primaryRole().primaryHost();
    }
    return null;
  }

  public List<String> traefikHosts() {
    return traefikRoles().stream()
      .flatMap(r -> r.hosts().stream())
      .distinct()
      .toList();
  }

  private List<Role> traefikRoles() {
    return roles().stream()
      .filter(Role::runningTraefik)
      .toList();
  }

  public List<Tag> envTag(String name) {
    return envTags().stream()
      .filter(t -> t.name().equals(name))
      .toList();
  }

  private List<Tag> envTags() {
    // TODO: Env::Tag clear/secret/tags in configuration
    return List.of();
  }

  public String[] loggingArgs() {
    return logging().args();
  }

  public List<String> volumeArgs() {
    // volumes present
    return List.of();
  }

  public int retainContainer() {
    // add to raw config
    return 5;
  }

  public String healthcheckService() {
    return Stream.of("healthcheck", service(), destination())
      .filter(StringUtils::isNotBlank)
      .collect(Collectors.joining("-"));
  }

  public Accessory accessory(String name) {
    return accessories.stream()
      .filter( a -> a.name().equals(name) )
      .findFirst()
      .orElse(null);
  }

}
