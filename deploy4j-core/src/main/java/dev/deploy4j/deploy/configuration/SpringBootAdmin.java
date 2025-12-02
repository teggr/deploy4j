package dev.deploy4j.deploy.configuration;

import dev.deploy4j.deploy.configuration.raw.EnvironmentConfig;
import dev.deploy4j.deploy.configuration.raw.SpringBootAdminConfig;

import java.util.HashMap;
import java.util.Map;

public class SpringBootAdmin {

  private static final String DEFAULT_IMAGE = "teggr/deploy4j-spring-boot-admin:latest";
  private static final Integer CONTAINER_PORT = 8080;
  private static final Map<String, String> DEFAULT_ARGS = Map.of();
  private static final Map<String, String> DEFAULT_LABELS = Map.of();

  private final Configuration config;
  private final SpringBootAdminConfig springBootAdminConfig;

  public SpringBootAdmin(Configuration config) {
    this.config = config;
    this.springBootAdminConfig = config.rawConfig().springBootAdmin() != null ?
      config.rawConfig().springBootAdmin() :
      new SpringBootAdminConfig();
  }

  public boolean publish() {
    return springBootAdminConfig().publish() == null || springBootAdminConfig().publish() != false;
  }

  public Map<String, String> labels() {
    Map<String, String> labels = new HashMap<>();
    labels.putAll(DEFAULT_LABELS);
    labels.putAll(springBootAdminConfig().labels() != null ? springBootAdminConfig().labels() : Map.of());
    return labels;
  }

  public Env env() {
    return new Env(
      springBootAdminConfig().env() != null ? springBootAdminConfig().env() : new EnvironmentConfig(),
      config().secrets(),
      "spring_boot_admin/env"
    );
  }

  public Integer hostPort() {
    return springBootAdminConfig().hostPort() != null ?
      springBootAdminConfig().hostPort() :
      CONTAINER_PORT;
  }

  public Map<String, String> options() {
    return springBootAdminConfig().options() != null ?
      springBootAdminConfig().options() :
      Map.of();
  }

  public String port() {
    return "%s:%s".formatted(hostPort(), CONTAINER_PORT);
  }

  public Map<String, String> args() {
    Map<String, String> args = new HashMap<>();
    args.putAll(DEFAULT_ARGS);
    args.putAll(springBootAdminConfig().args() != null ? springBootAdminConfig().args() : Map.of());
    return args;
  }

  public String image() {
    return springBootAdminConfig().image() != null ?
      springBootAdminConfig().image() :
      DEFAULT_IMAGE;
  }

  // attributes

  public Configuration config() {
    return config;
  }

  public SpringBootAdminConfig springBootAdminConfig() {
    return springBootAdminConfig;
  }

}
