package dev.deploy4j.raw;

import java.util.List;

public record ServerConfig(
  String host,
  List<String> tags
) {
}
