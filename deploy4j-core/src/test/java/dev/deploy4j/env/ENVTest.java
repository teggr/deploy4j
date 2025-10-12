package dev.deploy4j.env;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class ENVTest {

  static String systemEnvironmentVariableName;
  static String systemEnvironmentVariableValue;

  @BeforeAll
  static void setup() {

    System.getenv()
      .entrySet().stream()
      .forEach(System.out::println);

    // Pick a random system environment variable to use in tests
    java.util.Map.Entry<String, String> randomEnv = System.getenv()
      .entrySet().stream()
      .findAny().orElse(null);
    if (randomEnv != null) {
      systemEnvironmentVariableName = randomEnv.getKey();
      systemEnvironmentVariableValue = randomEnv.getValue();
    } else {
      systemEnvironmentVariableName = "DUMMY_ENV_KEY";
      systemEnvironmentVariableValue = "DUMMY_ENV_VALUE";
    }
  }

  @Nested
  class DefaultEnv {

    @Test
    void shouldFetchTheMatchingSystemEnvValue() {
      String value = ENV.fetch(systemEnvironmentVariableName);
      assertEquals(systemEnvironmentVariableValue, value);
    }

    @Test
    void shouldFetchNoValueIfSystemEnvDoesNotExist() {
      String value = ENV.fetch("UNKNOWN");
      assertEquals(null, value);
    }

    @Test
    void shouldLookupTheMatchingSystemEnvValue() {
      String value = ENV.lookup(systemEnvironmentVariableName);
      assertEquals(systemEnvironmentVariableValue, value);
    }

    @Test
    void shouldLookupKeyIfSystemEnvDoesNotExist() {
      String value = ENV.lookup("UNKNOWN");
      assertEquals("UNKNOWN", value);
    }

  }

  @Nested
  class SingleOverridingEnv {

    @BeforeEach
    void setup() {
      ENV.overload(new String[]{ "src/test/resources/envs/test.env" });
    }

    @Test
    void shouldFetchTheMatchingSystemEnvValue() {
      String value = ENV.fetch(systemEnvironmentVariableName);
      assertEquals(systemEnvironmentVariableValue, value);
    }

    @Test
    void shouldFetchTheOverridingSystemValue() {
      String value = ENV.fetch("Path");
      assertNotEquals("OVERRIDING_VALUE", value);
    }

    @Test
    void shouldFetchNoValueIfDoesNotExistInAnyEnv() {
      String value = ENV.fetch("UNKNOWN");
      assertEquals(null, value);
    }

    @Test
    void shouldLookupTheMatchingSystemEnvValue() {
      String value = ENV.lookup(systemEnvironmentVariableName);
      assertEquals(systemEnvironmentVariableValue, value);
    }

    @Test
    void shouldLookupTheOveridingValue() {
      String value = ENV.lookup("Path");
      assertNotEquals("OVERRIDING_VALUE", value);
    }

    @Test
    void shouldLookupKeyIfDoesNotExistInAnyEnv() {
      String value = ENV.lookup("UNKNOWN");
      assertEquals("UNKNOWN", value);
    }

  }

  @Nested
  class MultipleOverridingEnv {

    @BeforeEach
    void setup() {
      ENV.overload(new String[]{ "src/test/resources/envs/test.env", "src/test/resources/envs/destination-test.env" });
    }

    @Test
    void shouldFetchTheMatchingSystemEnvValue() {
      String value = ENV.fetch(systemEnvironmentVariableName);
      assertEquals(systemEnvironmentVariableValue, value);
    }

    @Test
    void shouldFetchTheOverridingSystemValue() {
      String value = ENV.fetch("Path");
      assertNotEquals("OVERRIDING_VALUE", value);
    }

    @Test
    void shouldFetchNoValueIfDoesNotExistInAnyEnv() {
      String value = ENV.fetch("UNKNOWN");
      assertEquals(null, value);
    }

    @Test
    void shouldLookupTheMatchingSystemEnvValue() {
      String value = ENV.lookup(systemEnvironmentVariableName);
      assertEquals(systemEnvironmentVariableValue, value);
    }

    @Test
    void shouldLookupTheOveridingValue() {
      String value = ENV.lookup("Path");
      assertNotEquals("OVERRIDING_VALUE", value);
    }

    @Test
    void shouldLookupKeyIfDoesNotExistInAnyEnv() {
      String value = ENV.lookup("UNKNOWN");
      assertEquals("UNKNOWN", value);
    }

  }

}