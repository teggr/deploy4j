package dev.deploy4j.raw;

import java.util.List;
import java.util.Map;

public record AccessoryConfig (
  String serviceName,
  String image,
  String host,
  List<String> hosts,
  List<String> roles,
  String cmd,
  String port,
  Map<String, String> labels,
  Map<String, String> options,
  EnvironmentConfig env,
  List<String> files,
  List<String> directories,
  List<String> volumes
) {
}
