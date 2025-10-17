package dev.deploy4j.env;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class ENVTest {

  @Test
  void testFetchOfSystemEnvironmentVariables() {

    // given
    Map.Entry<String, String> aSystemEnvironmentVariables = pickRandomSystemEnvironmentVariable();

    // when
    String fetchedValue = ENV.fetch(aSystemEnvironmentVariables.getKey());

    // then
    assertEquals(aSystemEnvironmentVariables.getValue(), fetchedValue);

  }

  @Test
  void testAllVariablesCleared() {

    // given
    Map.Entry<String, String> aSystemEnvironmentVariables = pickRandomSystemEnvironmentVariable();

    // when
    ENV.clear();
    String fetchedValue = ENV.fetch(aSystemEnvironmentVariables.getKey());

    // then
    assertNull(fetchedValue);

  }

  @Test
  void testFetchOfUpdatedSystemEnvironmentVariables() {

    // given
    Map.Entry<String, String> aSystemEnvironmentVariables = pickRandomSystemEnvironmentVariable();
    String overridingValue = "OVERRIDING_VALUE";

    // when
    ENV.update( Map.of( aSystemEnvironmentVariables.getKey(), overridingValue) );
    String fetchedValue = ENV.fetch(aSystemEnvironmentVariables.getKey());

    // then
    assertEquals(overridingValue, fetchedValue);

  }

  @Test
  void testVariablesDeleted() {

    // given
    Map.Entry<String, String> aSystemEnvironmentVariables = pickRandomSystemEnvironmentVariable();

    // when
    ENV.delete(aSystemEnvironmentVariables.getKey());
    String fetchedValue = ENV.fetch(aSystemEnvironmentVariables.getKey());

    // then
    assertNull(fetchedValue);

  }

  private Map.Entry<String, String> pickRandomSystemEnvironmentVariable() {
    return System.getenv().entrySet().stream().findAny().orElseThrow();
  }

}