package dev.deploy4j.raw;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.List;
import java.util.Map;

public class ServerConfig {

  private final String host;
  private final List<String> tags;

  @JsonIgnore
  public ServerConfig(String host) {
    this.host = host;
    this.tags = List.of();
  }

  @JsonCreator
  public ServerConfig(Map<String, ?> hostWithTags) {
    this.host = hostWithTags.keySet().stream().findFirst().orElse(null);
    Object o = hostWithTags.get(this.host);
    if( o instanceof String tag ) {
      this.tags = List.of(tag);
    } else if( o instanceof List<?> tagList ) {
      this.tags = tagList.stream().map( Object::toString ).toList();
    } else {
      this.tags = List.of();
    }
  }

  public String host() {
    return host;
  }

  public List<String> tags() {
    return tags;
  }

}
