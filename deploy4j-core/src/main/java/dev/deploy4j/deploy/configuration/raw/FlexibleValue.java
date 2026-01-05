package dev.deploy4j.deploy.configuration.raw;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.ArrayList;
import java.util.List;

/**
 * A flexible value type that can hold either a single string or a list of strings.
 * This is useful for configuration options that can be specified multiple times.
 */
public class FlexibleValue {

  private final List<String> values;

  private FlexibleValue(List<String> values) {
    this.values = values;
  }

  @JsonCreator
  public static FlexibleValue from(Object value) {
    if (value == null) {
      return new FlexibleValue(List.of());
    }
    
    if (value instanceof String) {
      return new FlexibleValue(List.of((String) value));
    }
    
    if (value instanceof List<?>) {
      List<String> stringList = new ArrayList<>();
      for (Object item : (List<?>) value) {
        if (item != null) {
          stringList.add(item.toString());
        }
      }
      return new FlexibleValue(stringList);
    }
    
    // For any other type, convert to string
    return new FlexibleValue(List.of(value.toString()));
  }

  public List<String> asList() {
    return values;
  }

  public String asSingleValue() {
    return values.isEmpty() ? null : values.get(0);
  }

  public boolean isList() {
    return values.size() > 1;
  }

  public boolean isEmpty() {
    return values.isEmpty();
  }

  @JsonValue
  public Object toJson() {
    if (values.isEmpty()) {
      return null;
    }
    if (values.size() == 1) {
      return values.get(0);
    }
    return values;
  }

  @Override
  public String toString() {
    return values.toString();
  }
}
