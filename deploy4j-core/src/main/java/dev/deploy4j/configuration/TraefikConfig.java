package dev.deploy4j.configuration;

import java.util.Map;

public class TraefikConfig {

  private static final int CONTAINER_PORT = 80;
  private static final String DEFAULT_IMAGE = "traefik:v2.11";
  private static final Map<String, String> DEFAULT_LABELS = Map.of(
    // These ensure we serve a 502 rather than a 404 if no containers are available
    "traefik.http.routers.catchall.entryPoints" , "http",
"traefik.http.routers.catchall.rule" , "PathPrefix(`/`)",
"traefik.http.routers.catchall.service" , "unavailable",
"traefik.http.routers.catchall.priority" , "1",
"traefik.http.services.unavailable.loadbalancer.server.port" , "0"
  );
  private static final Map<String, String> DEFAULT_ARGS = Map.of(
    "log.level", "DEBUG"
  );

  private final Map<String, String> options;
  private final Map<String, String> args;

  public TraefikConfig(Map<String, String> options, Map<String, String> args) {
    this.options = options;
    this.args = args;
  }

  public boolean publish() {
    return true;
  }

  public String port() {
    return "%s:%s".formatted(hostPort(), CONTAINER_PORT);
  }

  private String hostPort() {
    // get or default
    return "" + CONTAINER_PORT;
  }

  public String image() {
    // get or default
    return DEFAULT_IMAGE;
  }

  public String[] envArgs() {
    // TODO: --env-file secrets argumentize all others
    return new String[]{};
  }

  public Map<String, String> labels() {
    // TODO merge with config
    return DEFAULT_LABELS;
  }

  public Map<String, String> options() {
    return options;
  }

  public Map<String, String> args() {
    return new java.util.HashMap<>() {{
      putAll(DEFAULT_ARGS);
      putAll(args);
    }};
  }

}
