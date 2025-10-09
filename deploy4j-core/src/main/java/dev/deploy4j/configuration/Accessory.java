package dev.deploy4j.configuration;

import dev.deploy4j.Cmd;
import dev.deploy4j.raw.AccessoryConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static dev.deploy4j.Commands.argumentize;
import static dev.deploy4j.Commands.optionize;

public class Accessory {
  private final String name;
  private final Configuration config;
  private final AccessoryConfig accessoryConfig;
  private final Env env;

  public Accessory(String name, Configuration config) {
    this.name = name;
    this.config = config;
    this.accessoryConfig = config.rawConfig().accessories().get(name);
    this.env = new Env(accessoryConfig.env());
  }

  public String serviceName() {
    return accessoryConfig.serviceName() != null ?
      accessoryConfig.serviceName() :
      "%s-%s".formatted( config.service(), name );
  }

  public String image() {
    return accessoryConfig.image();
  }

  public List<String> hosts() {
    List<String> hosts = hostsFromHost();
    if (hosts == null) {
      hosts = hostsFromHosts();
    }
    if( hosts == null ) {
      hosts = hostsFromRoles();
    }
    return hosts;
  }

  public String port() {
    String port = accessoryConfig.port();
    if(port.contains(":")) {
      return port;
    } else {
      return "%s:%s".formatted(port, port);
    }
  }

  public String[] publishArgs() {
    return argumentize("--publish", List.of(port()));
  }

  public Map<String, String> labels() {
    Map<String, String> labels = new HashMap<>();
    labels.putAll(defaultLabels());
    labels.putAll(accessoryConfig.labels());
    return labels;
  }

  public String[] labelArgs() {
    return argumentize("--label", labels());
  }

  public String[] envArgs() {
    return env.args();
  }

  // files
  // directories
  // volumns
  // volume args

  public List<String> optionArgs() {
    return optionize(accessoryConfig.options());
  }

  public String cmd() {
    return accessoryConfig.cmd();
  }

  private Map<String, String> defaultLabels() {
    return Map.of( "service", serviceName() );
  }

  private List<String> hostsFromRoles() {
    return accessoryConfig.roles().stream()
      .flatMap( role -> {
        return config.role(role).hosts().stream();
      })
      .toList();
  }

  private List<String> hostsFromHosts() {
    return accessoryConfig.hosts() != null ? accessoryConfig.hosts() : null;
  }

  private List<String> hostsFromHost() {
    return accessoryConfig.host() != null ? List.of(accessoryConfig.host()) : null;
  }

}
