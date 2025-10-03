package dev.deploy4j.config;

import java.util.Map;

public class Traefik {
  public int port = 80;
  public boolean publish = true;
  public Map<String, String> labels = Map.of(
    // These ensure we serve a 502 rather than a 404 if no containers are available
    "traefik.http.routers.catchall.entryPoints", "http",
    "traefik.http.routers.catchall.rule", "PathPrefix(`/`)",
    "traefik.http.routers.catchall.service", "unavailable",
    "traefik.http.routers.catchall.priority", "1",
    "traefik.http.services.unavailable.loadbalancer.server.port", "0",
    "traefik.http.routers.dashboard.rule", "Host(`traefik.localhost`) && (PathPrefix(`/api`) || PathPrefix(`/dashboard`))",
    "traefik.http.routers.dashboard.service", "api@internal"
  );
  public String env;
  public String image = "traefik:v3.5";
  public Map<String, String> options = Map.of(
    "publish", "8080"
  );
  public Map<String, String> args = Map.of(
    "log.level", "DEBUG",
    "api.insecure", "true",
    "api.dashboard", "true"
  );
}
