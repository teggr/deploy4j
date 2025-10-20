package dev.deploy4j.configuration;

import dev.deploy4j.file.Deploy4jFile;
import dev.deploy4j.raw.EnvironmentConfig;
import dev.deploy4j.raw.TraefikConfig;

import java.util.HashMap;
import java.util.Map;

public class Traefik {

  private static final String DEFAULT_IMAGE = "traefik:v2.11";
  private static final Integer CONTAINER_PORT = 80;
  private static final Map<String, String> DEFAULT_ARGS = Map.of(
    "log.level", "DEBUG"
  );
  private static final Map<String, String> DEFAULT_LABELS = Map.of(
    // These ensure we serve a 502 rather than a 404 if no containers are available
    "traefik.http.routers.catchall.entryPoints", "http",
    "traefik.http.routers.catchall.rule", "PathPrefix(`/`)",
    "traefik.http.routers.catchall.service", "unavailable",
    "traefik.http.routers.catchall.priority", "1",
    "traefik.http.services.unavailable.loadbalancer.server.port", "0"
  );

  private final Configuration config;
  private final TraefikConfig traefikConfig;

  public Traefik(Configuration config) {
    this.config = config;
    this.traefikConfig = config.rawConfig().traefik() != null ?
      config.rawConfig().traefik() :
      new TraefikConfig();
  }

  public boolean publish() {
    return traefikConfig().publish() == null || traefikConfig().publish() != false;
  }

  public Map<String, String> labels() {
    Map<String, String> labels = new HashMap<>();
    labels.putAll(DEFAULT_LABELS);
    labels.putAll(traefikConfig().labels() != null ? traefikConfig().labels() : Map.of());
    return labels;
  }

  public Env env() {
    return new Env(
      traefikConfig().env() != null ? traefikConfig().env() : new EnvironmentConfig(),
      Deploy4jFile.join(config().hostEnvDirectory(), "traefik", "traefik.env"),
      "traefik/env"
    );
  }

  public Integer hostPort() {
    return traefikConfig().hostPort() != null ?
      traefikConfig().hostPort() :
      CONTAINER_PORT;
  }

  public Map<String, String> options() {
    return traefikConfig().options() != null ?
      traefikConfig().options() :
      Map.of();
  }

  public String port() {
    return "%s:%s".formatted(hostPort(), CONTAINER_PORT);
  }

  public Map<String, String> args() {
    Map<String, String> args = new HashMap<>();
    args.putAll(DEFAULT_ARGS);
    args.putAll(traefikConfig().args() != null ? traefikConfig().args() : Map.of());
    return args;
  }

  public String image() {
    return traefikConfig().image() != null ?
      traefikConfig().image() :
      DEFAULT_IMAGE;
  }

  // attributes

  public Configuration config() {
    return config;
  }

  public TraefikConfig traefikConfig() {
    return traefikConfig;
  }

}
