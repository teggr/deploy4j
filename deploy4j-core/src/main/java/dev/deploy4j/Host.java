package dev.deploy4j;

import dev.deploy4j.configuration.Deploy4jConfig;
import dev.deploy4j.configuration.SshConfig;
import dev.deploy4j.ssh.SSHTemplate;

import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Host {

  private final String host;
  private final String role;
  private final Deploy4jConfig config;
  private final SSHTemplate sshTemplate;

  public Host(String host, String role, Deploy4jConfig config ) {
    this.host = host;
    this.role = role;
    this.config = config;
    this.sshTemplate = new SSHTemplate(host, config.ssh());
  }

  public String role() {
    return role;
  }

  public String name() {
    return host;
  }

  public void close() {
    sshTemplate.close();
  }

  public boolean execute(Cmd cmd) {
    return sshTemplate.exec(cmd).exitStatus() == 0;
  }

  public String capture(Cmd cmd) {
    return sshTemplate.exec(cmd).execOutput();
  }

  public boolean runningTraefik() {
    // if !role.traefik then is primary?
    // else return traefik
    return isPrimary();
  }

  private boolean isPrimary() {
    return role.equals(config.primaryRole());
  }

  //  public String primaryHost() {
//    return hosts().stream().findFirst().orElse(null);
//  }
//
//  private Collection<String> hosts() {
//    return taggedHosts().keySet();
//  }
//
//  public List<EnvTag> envTags(String host) {
//    return taggedHosts().getOrDefault(host, emptyList())
//              .stream()
//              .flatMap( tag -> config.envTag(tag).stream() )
//                    .toList();
//  }
//
//  private Map<String, List<String>> taggedHosts() {
//    return extractHostsFromConfig().stream()
//      .collect(Collectors.toMap(
//        ServerConfig::host,
//        sc -> sc.tags() != null ? sc.tags() : emptyList()
//      ));
//
//  }
//
//  private List<ServerConfig> extractHostsFromConfig() {
//    if (!config.servers().isEmpty()) {
//      return config.servers();
//    }
//    if (!config.roles().isEmpty()) {
//      return config.roles().stream()
//        .filter(rc -> rc.name().equalsIgnoreCase(this.name()))
//        .findFirst()
//        .map(RoleConfig::servers)
//        .orElseGet(Collections::emptyList);
//    }
//    return emptyList();
//  }

  public String containerPrefix() {
    return Stream.of(
        config.service(),
        role,
        config.destination()
      ).filter(Objects::nonNull)
      .collect(Collectors.joining("-"));
  }


//  public String[] envArgs(String host) {
//    return env(host).args();
//  }
//
//  private EnvTag env(String host) {
//    EnvTag root = new EnvTag("", config.env());
//    // envs specialized_env
//    envTags(host).forEach( root::merge );
//    return root;
//  }
//
//  public List<String> healthCheckArgs() {
//    if( runningTraefik() || healthcheck().setPortOrPath() ) {
//      return optionize(Map.of(
//          "health-cmd", healthcheck().cmd(),
//          "health-interval", healthcheck().interval()
//        )
//      );
//    } else {
//      return emptyList();
//    }
//  }
//
//  private Cmd healthcheck() {
//    return config.healthCheck();
//  }
//
//  private boolean runningTraefik() {
//    return false;
//  }

}
