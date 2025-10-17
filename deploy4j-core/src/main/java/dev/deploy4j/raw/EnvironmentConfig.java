package dev.deploy4j.raw;

import com.fasterxml.jackson.annotation.JsonAnySetter;

import java.util.List;
import java.util.Map;

public record EnvironmentConfig(
  Map<String, String> clear,
  List<String> secrets,
  Map<String, EnvironmentConfig> tags,
  @JsonAnySetter Map<String, String> additionalVariables // root level treated like additional properties
) {
}
