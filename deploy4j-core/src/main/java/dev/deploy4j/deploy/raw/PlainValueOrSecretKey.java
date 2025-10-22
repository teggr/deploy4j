package dev.deploy4j.deploy.raw;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.List;

public class PlainValueOrSecretKey {

  private final String key;
  private final String value;

  @JsonCreator
  public PlainValueOrSecretKey(Object password) {

    if (password instanceof String) {
      this.key = null;
      this.value = (String) password;
    } else if (password instanceof List<?>) {
      this.key = String.join("", (List<String>) password);
      this.value = null;
    } else {
      this.key = null;
      this.value = null;
    }

  }

  public boolean isKey() {
    return key != null;
  }

  public String key() {
    return key;
  }

  public String value() {
    return value;
  }
}
