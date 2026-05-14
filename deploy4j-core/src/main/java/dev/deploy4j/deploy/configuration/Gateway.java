package dev.deploy4j.deploy.configuration;

import dev.deploy4j.deploy.configuration.raw.EnvironmentConfig;
import dev.deploy4j.deploy.configuration.raw.FlexibleValue;
import dev.deploy4j.deploy.configuration.raw.GatewayConfig;

import java.util.HashMap;
import java.util.Map;

public class Gateway {

  private static final String DEFAULT_IMAGE = "springcloud/spring-cloud-gateway:latest";
  private static final Integer DEFAULT_HOST_PORT = 80;
  private static final Integer CONTAINER_PORT = 8080;
  private static final Map<String, String> DEFAULT_ARGS = Map.of(
    "management.endpoint.gateway.enabled", "true",
    "management.endpoints.web.exposure.include", "gateway,health,info"
  );

  private final Configuration config;
  private final GatewayConfig gatewayConfig;

  public Gateway(Configuration config) {
    this.config = config;
    this.gatewayConfig = config.rawConfig().gateway() != null ?
      config.rawConfig().gateway() :
      new GatewayConfig();
  }

  public boolean publish() {
    return gatewayConfig().publish() == null || gatewayConfig().publish() != false;
  }

  public Map<String, String> labels() {
    return gatewayConfig().labels() != null ? gatewayConfig().labels() : Map.of();
  }

  public Env env() {
    return new Env(
      gatewayConfig().env() != null ? gatewayConfig().env() : new EnvironmentConfig(),
      config().secrets(),
      "gateway/env"
    );
  }

  public Integer hostPort() {
    return gatewayConfig().hostPort() != null ?
      gatewayConfig().hostPort() :
      DEFAULT_HOST_PORT;
  }

  public Map<String, Object> options() {
    if (gatewayConfig().options() == null) {
      return Map.of();
    }
    
    Map<String, Object> result = new HashMap<>();
    for (Map.Entry<String, FlexibleValue> entry : gatewayConfig().options().entrySet()) {
      FlexibleValue flexValue = entry.getValue();
      if (flexValue != null && !flexValue.isEmpty()) {
        if (flexValue.isList()) {
          result.put(entry.getKey(), flexValue.asList());
        } else {
          result.put(entry.getKey(), flexValue.asSingleValue());
        }
      }
    }
    return result;
  }

  public String port() {
    return "%s:%s".formatted(hostPort(), CONTAINER_PORT);
  }

  public Map<String, String> args() {
    Map<String, String> args = new HashMap<>();
    args.putAll(DEFAULT_ARGS);
    args.putAll(gatewayConfig().args() != null ? gatewayConfig().args() : Map.of());
    return args;
  }

  public String image() {
    return gatewayConfig().image() != null ?
      gatewayConfig().image() :
      DEFAULT_IMAGE;
  }

  // attributes

  public Configuration config() {
    return config;
  }

  public GatewayConfig gatewayConfig() {
    return gatewayConfig;
  }

}
