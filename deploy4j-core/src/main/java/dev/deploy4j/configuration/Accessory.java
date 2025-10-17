package dev.deploy4j.configuration;

import dev.deploy4j.Cmd;
import dev.deploy4j.file.Deploy4jFile;
import dev.deploy4j.raw.AccessoryConfig;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static dev.deploy4j.Commands.*;

public class Accessory {
  private final String name;
  private final Configuration config;
  private final AccessoryConfig accessoryConfig;
  private final Env env;

  public Accessory(String name, Configuration config) {
    this.name = name;
    this.config = config;
    this.accessoryConfig = config.rawConfig().accessories().get(name);
    this.env = new Env(
      accessoryConfig.env(),
      Deploy4jFile.join( config.hostEnvDirectory(), "accessories", serviceName() + ".env" ),
      "accessories/%s/env".formatted(name)
    );
  }

  public String name() {
    return name;
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
    labels.putAll(accessoryConfig.labels() != null ? accessoryConfig.labels() : Map.of());
    return labels;
  }

  public String[] labelArgs() {
    return argumentize("--label", labels());
  }

  public List<String> envArgs() {
    return env.args();
  }

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

  public Map<String, String> directories() {
    List<String> directories = accessoryConfig.directories();
    if( directories == null ) {
      return new HashMap<>();
    } else {
      return directories.stream()
        .map( hostToContainerMapping -> {
          return hostToContainerMapping.split(":");
        } )
        .collect(
          HashMap::new,
          (map, arr) -> { map.put(
            expandHostPath(arr[0]),
            arr[1]
          ); },
          HashMap::putAll
        );
    }
  }

  private String expandHostPath(String hostPath) {
    if(absolutePath(hostPath)) {
      return hostPath;
    } else {
      File join = new File( serviceDataDirectory(), hostPath );
      return join.getAbsolutePath();
    }
  }

  public boolean absolutePath(String path) {
    return new File(path).isAbsolute();
  }

  private String serviceDataDirectory() {
    return "$PWD/%s".formatted(serviceName());
  }

  private String expandLocalFile(String localFile) {
    // TODO erb?
    return new File(localFile).getAbsolutePath();
  }

  private String expandRemoteFile(String remoteFile) {
    return serviceName() + remoteFile;
  }

  public Cmd makeDirectory(String path) {
    return Cmd.cmd("mkdir", "-p", path );
  }

  public Map<String, String> files() {
    List<String> files = accessoryConfig.files();
    if(files == null) {
      return new HashMap<>();
    } else {
      return files.stream()
        .map( localToRemoteMapping -> {
          return localToRemoteMapping.split(":");
        } )
        .collect(
          HashMap::new,
          (map, arr) -> { map.put(
            expandLocalFile(arr[0]),
            expandRemoteFile(arr[1])
          ); },
          HashMap::putAll
        );

    }
  }

  public void ensureLocalFilePresent(String localFile) {
    if( !new File(localFile).exists() ) {
      throw new RuntimeException("Missing file: " + localFile);
    }
  }

  public Cmd run() {
    return Cmd.cmd("docker", "run")
      .args("--name", serviceName())
      .args("--detach")
      .args("--restart", "unless-stopped")
      .args(config.loggingArgs())
      .args(publishArgs())
      .args(envArgs())
      .args(volumeArgs())
      .args(labelArgs())
      .args(optionArgs())
      .args(image())
      .args(cmd())
      .description("Run accessory");
  }

  private String[] volumeArgs() {
    return argumentize("--volume", volumes());
  }

  private List<String> volumes() {
    List<String> volumes = new ArrayList<>();
    volumes.addAll(specificVolumes());
    volumes.addAll(remoteFilesAsVolumes());
    volumes.addAll(remoteDirectoriesAsVolumes());
    return volumes;
  }

  private List<String> remoteDirectoriesAsVolumes() {
    List<String> directories = accessoryConfig.directories();
    if( directories == null ) {
      return new ArrayList<>();
    } else {
      return directories.stream().map(hostToContainerMapping -> {
        String[] arr = hostToContainerMapping.split(":");
        String hostPath = expandRemoteFile(arr[1]);
        String containerPath = expandRemoteFile(arr[1]);
        return "%s:%s".formatted(expandHostPath(hostPath), containerPath);
      }).toList();
    }
  }

  private List<String> remoteFilesAsVolumes() {
    List<String> files = accessoryConfig.files();
    if( files == null ) {
      return new ArrayList<>();
    } else {
      return files.stream().map(localToRemoteMapping -> {
        String[] arr = localToRemoteMapping.split(":");
        String remoteFile = expandRemoteFile(arr[1]);
        return "%s:%s".formatted(serviceDataDirectory() + remoteFile, remoteFile);
      }).toList();
    }
  }

  private List<String> specificVolumes() {
    return accessoryConfig.volumes() != null ? accessoryConfig.volumes() : new ArrayList<>();
  }


  public Cmd info() {
    return Cmd.cmd("docker", "ps")
      .args(serviceFilter());
  }

  private String[] serviceFilter() {
    return new String[]{
      "--filter",
      "label=service=" +  serviceName()
    };
  }

  public Cmd stop() {
    return Cmd.cmd( "docker", "container", "stop", serviceName() );
  }

  public Cmd removeContainer() {
    return Cmd.cmd( "docker", "container", "prune", "--force")
      .args( serviceName() );
  }

  public Cmd removeImage() {
    return Cmd.cmd( "docker", "image", "rm", "--force", image() );
  }

  public Cmd removeServiceDirectory() {
    return Cmd.cmd("rm", "-rf", serviceName() );
  }

  public Cmd start() {
    return Cmd.cmd("docker", "container", "start", serviceName() );
  }

  public Cmd executeInExistingContainer(String command) {
    return Cmd.cmd( "docker", "exec" )
      .args( serviceName() )
      .args( command );
  }

  public Cmd logs(String since, String lines, String grep, String grepOptions) {
    return pipe(
        Cmd.cmd("docker", "logs")
          .args(serviceName())
          .args(since != null ? List.of("--since", since) : List.of())
          .args(lines != null ? List.of("--tail", lines) : List.of())
          .args("--timestamps")
          .args("2>&1"),
        grep != null ? Cmd.cmd("grep", "'" + grep + "'")
          .args( grepOptions ) : null
    );
  }
}
