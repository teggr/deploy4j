package dev.deploy4j.raw;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import dev.deploy4j.configuration.Env;

import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CustomRoleConfig {

  private final List<ServerConfig> hosts;
  private final boolean traefik;
  private final String cmd;

  @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
  public CustomRoleConfig(
    @JsonProperty("hosts") List<?> hosts,
    @JsonProperty("traefik") boolean traefik,
    @JsonProperty("cmd") String cmd ) {
    this.hosts = hosts.stream().map( o -> {;
      if( o instanceof String s ) {
        return new ServerConfig(s);
      } else if( o instanceof Map<?,?> m ) {
        return new ServerConfig( (Map<String, List<String>>)m );
      } else {
        throw new IllegalArgumentException("Invalid server entry: " + o);
      }
    }).toList();
    this.traefik = traefik;
    this.cmd = cmd;
  }

  public List<ServerConfig> hosts() {
    return hosts;
  }

  public boolean traefik() {
    return traefik;
  }

  public String cmd() {
    return cmd;
  }

}
