package dev.deploy4j.configuration;

public record ServerConfig(String host) {
  public String role() {
    return "web";
  }
}
