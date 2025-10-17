package dev.deploy4j.raw;

import java.util.List;

public record HostListConfig(
  String host,
  List<String> tags
) {
}
