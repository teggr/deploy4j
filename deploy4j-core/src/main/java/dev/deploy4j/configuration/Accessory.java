package dev.deploy4j.configuration;

import dev.deploy4j.file.Deploy4jFile;
import dev.deploy4j.raw.AccessoryConfig;

import java.io.File;
import java.nio.file.Paths;
import java.util.*;

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

    // TODO: validate

    this.env = new Env(
      accessoryConfig.env() != null ? accessoryConfig.env() : new dev.deploy4j.raw.EnvironmentConfig(),
      Deploy4jFile.join(config.hostEnvDirectory(), "accessories", serviceName() + ".env"),
      "accessories/%s/env".formatted(name)
    );
  }

  public String serviceName() {
    return Optional.ofNullable(accessoryConfig().service())
      .orElseGet(() -> "%s-%s".formatted(config().service(), name()));
  }

  public String image() {
    return accessoryConfig().image();
  }

  public List<String> hosts() {
    List<String> hosts = hostsFromHost();
    if (hosts == null) {
      hosts = hostsFromHosts();
    }
    if (hosts == null) {
      hosts = hostsFromRoles();
    }
    return hosts;
  }

  public String port() {
    String port = accessoryConfig().port() != null ? accessoryConfig().port() : null;
    if( port != null ) {
      if (port.contains(":")) {
        return port;
      } else {
        return "%s:%s".formatted(port, port);
      }
    }
    return null;
  }

  public String[] publishArgs() {
    if(port() != null) {
      return argumentize("--publish", port());
    } else {
      return new String[]{};
    }
  }

  public Map<String, String> labels() {
    Map<String, String> labels = new HashMap<>();
    labels.putAll(defaultLabels());
    labels.putAll(accessoryConfig().labels() != null ? accessoryConfig().labels() : Map.of());
    return labels;
  }

  public String[] labelArgs() {
    return argumentize("--label", labels());
  }

  public List<String> envArgs() {
    return env().args();
  }

  public Map<String, String> files() {
    List<String> files = accessoryConfig().files();
    if (files == null) {
      return new HashMap<>();
    } else {
      return files.stream()
        .map(localToRemoteMapping -> {
          return localToRemoteMapping.split(":");
        })
        .collect(
          HashMap::new,
          (map, arr) -> {
            map.put(
              expandLocalFile(arr[0]),
              expandRemoteFile(arr[1])
            );
          },
          HashMap::putAll
        );

    }
  }

  public Map<String, String> directories() {
    List<String> directories = accessoryConfig().directories();
    if (directories == null) {
      return new HashMap<>();
    } else {
      return directories.stream()
        .map(hostToContainerMapping -> {
          return hostToContainerMapping.split(":");
        })
        .collect(
          HashMap::new,
          (map, arr) -> {
            map.put(
              expandHostPath(arr[0]),
              arr[1]
            );
          },
          HashMap::putAll
        );
    }
  }

  public List<String> volumes() {
    List<String> volumes = new ArrayList<>();
    volumes.addAll(specificVolumes());
    volumes.addAll(remoteFilesAsVolumes());
    volumes.addAll(remoteDirectoriesAsVolumes());
    return volumes;
  }

  private String[] volumeArgs() {
    return argumentize("--volume", volumes());
  }

  public List<String> optionArgs() {
    Map<String, String> options = accessoryConfig().options();
    if (options != null) {
      return optionize(options);
    } else {
      return List.of();
    }
  }

  public String cmd() {
    return accessoryConfig().cmd();
  }

  // private

  private Map<String, String> defaultLabels() {
    return Map.of("service", serviceName());
  }

  private String expandLocalFile(String localFile) {
    // TODO erb?
    return new File(localFile).getAbsolutePath();
  }

  // TODO: with clear env loaded
  // TODO: read dynamic file

  private String expandRemoteFile(String remoteFile) {
    return serviceName() + remoteFile;
  }

  private List<String> specificVolumes() {
    return accessoryConfig().volumes() != null ? accessoryConfig().volumes() : new ArrayList<>();
  }

  private List<String> remoteFilesAsVolumes() {
    List<String> files = accessoryConfig().files();
    if (files == null) {
      return new ArrayList<>();
    } else {
      return files.stream().map(localToRemoteMapping -> {
        String[] arr = localToRemoteMapping.split(":");
        String remoteFile = expandRemoteFile(arr[1]);
        return "%s:%s".formatted(serviceDataDirectory() + remoteFile, remoteFile);
      }).toList();
    }
  }

  private List<String> remoteDirectoriesAsVolumes() {
    List<String> directories = accessoryConfig().directories();
    if (directories == null) {
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

  private String expandHostPath(String hostPath) {
    if (absolutePath(hostPath)) {
      return hostPath;
    } else {
      return Deploy4jFile.join(serviceDataDirectory(), hostPath);
    }
  }

  public boolean absolutePath(String path) {
    return Paths.get(path).isAbsolute();
  }

  private String serviceDataDirectory() {
    return "$PWD/%s".formatted(serviceName());
  }

  private List<String> hostsFromHost() {
    return accessoryConfig().host() != null ? List.of(accessoryConfig().host()) : null;
  }

  private List<String> hostsFromHosts() {
    return accessoryConfig().hosts() != null ? accessoryConfig().hosts() : null;
  }

  private List<String> hostsFromRoles() {
    return accessoryConfig().roles().stream()
      .flatMap(role -> {
        return config().role(role).hosts().stream();
      })
      .toList();
  }

  // attributes

  public Configuration config() {
    return config;
  }

  public String name() {
    return name;
  }

  public AccessoryConfig accessoryConfig() {
    return accessoryConfig;
  }

  public Env env() {
    return env;
  }

}
