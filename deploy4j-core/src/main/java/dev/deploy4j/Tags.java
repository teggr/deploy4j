package dev.deploy4j;

import dev.deploy4j.configuration.Configuration;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Tags {

  public static Tags fromConfig(Configuration config, Map<String, String> extra) {
    Map<String, String> merged = new HashMap<>(defaultTags(config));
    merged.putAll(extra);
    return new Tags(merged);
  }

  private static Map<String, String> defaultTags(Configuration config) {
    Map<String, String> tags = new HashMap<String, String>();
    tags.put("recorded_at", OffsetDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
    // TODO who am i
    tags.put("destination", config.destination());
    tags.put("version", config.version());
    tags.put("service_version", serviceVersion(config));
    tags.put("service", config.service());
    return tags;
  }

  private static String serviceVersion(Configuration config) {
    return Stream.of(config.service(), config.abbreviatedVersion())
      .filter(Objects::nonNull).collect(Collectors.joining("@"));
  }

  private final Map<String, String> tags;

  public Tags(Map<String, String> tags) {
    this.tags = tags;
  }

  public Tags env() {
    Map<String, String> transformedTags = tags().entrySet().stream()
      .collect(Collectors.toMap(
        e -> "DEPLOY4J_" + e.getKey().toUpperCase(),
        e -> e.getValue()));
    return new Tags(transformedTags);
  }

  public String toTagString() {
    return tags().values().stream()
      .map(value -> "[" + value + "]")
      .collect(Collectors.joining(" "));
  }

  public Tags except(String... tags) {
    Map<String, String> exceptTags = Map.copyOf(tags());
    Stream.of(tags).forEach(exceptTags::remove);
    return new Tags(exceptTags);
  }

  // attributes

  public Map<String, String> tags() {
    return tags;
  }

}
