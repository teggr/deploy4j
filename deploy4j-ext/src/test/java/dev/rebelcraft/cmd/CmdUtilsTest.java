package dev.rebelcraft.cmd;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static dev.rebelcraft.cmd.CmdUtils.optionize;
import static dev.rebelcraft.cmd.CmdUtils.optionizeFlexible;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.in;

class CmdUtilsTest {

  @Nested
  class Optionize {

    @Test
    void shouldCreateSeparateOptionsWithoutJoiningChar() {

      List<String> options = optionize(
        Map.of("publish","8080", "detach","true"),
        null
      );

      assertThat(options)
        .containsOnlyOnce("--publish", "\"8080\"", "--detach");

    }

    @Test
    void shouldNotCreateOptionsWithNullValues() {

      Map<String, String> input = new HashMap<>();
      input.put("publish",null);
      input.put("detach",null);

      List<String> options = optionize(
        input,
        null
      );

      assertThat(options).isEmpty();

    }

    @Test
    void shouldCreateSeparateOptionsWithEmptyValuesWithoutJoiningChar() {

      List<String> options = optionize(
        Map.of("publish","", "detach",""),
        null
      );

      assertThat(options)
        .containsOnly("--publish", "\"\"", "--detach", "\"\"");

    }

    @Test
    void shouldCreateOptionsWithJoiningChar() {

      List<String> options = optionize(
        Map.of("publish","8080", "detach","true"),
        "="
      );

      assertThat(options)
        .containsOnlyOnce("--publish=\"8080\"", "--detach");

    }

  }

  @Nested
  class OptionizeFlexible {

    @Test
    void shouldHandleSingleStringValues() {
      Map<String, Object> input = Map.of("publish", "8080", "detach", "true");
      
      List<String> options = optionizeFlexible(input);

      assertThat(options)
        .containsOnlyOnce("--publish", "\"8080\"", "--detach");
    }

    @Test
    void shouldHandleListValues() {
      Map<String, Object> input = Map.of(
        "publish", List.of("443:443", "80:80"),
        "volume", List.of("/etc/letsencrypt/acme.json:/etc/letsencrypt/acme.json")
      );
      
      List<String> options = optionizeFlexible(input);

      assertThat(options)
        .contains("--publish", "\"443:443\"", "--publish", "\"80:80\"")
        .contains("--volume", "\"/etc/letsencrypt/acme.json:/etc/letsencrypt/acme.json\"");
    }

    @Test
    void shouldHandleMixedStringAndListValues() {
      Map<String, Object> input = Map.of(
        "publish", List.of("443:443", "80:80"),
        "name", "gateway",
        "detach", "true"
      );
      
      List<String> options = optionizeFlexible(input);

      assertThat(options)
        .contains("--publish", "\"443:443\"", "--publish", "\"80:80\"")
        .contains("--name", "\"gateway\"")
        .contains("--detach");
    }

    @Test
    void shouldHandleWithJoiningChar() {
      Map<String, Object> input = Map.of(
        "publish", List.of("443:443", "80:80"),
        "name", "gateway"
      );
      
      List<String> options = optionizeFlexible(input, "=");

      assertThat(options)
        .contains("--publish=\"443:443\"", "--publish=\"80:80\"")
        .contains("--name=\"gateway\"");
    }

    @Test
    void shouldSkipNullValues() {
      Map<String, Object> input = new HashMap<>();
      input.put("publish", "8080");
      input.put("detach", null);
      
      List<String> options = optionizeFlexible(input);

      assertThat(options)
        .containsOnly("--publish", "\"8080\"");
    }

    @Test
    void shouldSkipNullItemsInList() {
      Map<String, Object> input = new HashMap<>();
      List<String> publishList = new java.util.ArrayList<>();
      publishList.add("443:443");
      publishList.add(null);
      publishList.add("80:80");
      input.put("publish", publishList);
      
      List<String> options = optionizeFlexible(input);

      assertThat(options)
        .containsExactly("--publish", "\"443:443\"", "--publish", "\"80:80\"");
    }

    @Test
    void shouldHandleEmptyList() {
      Map<String, Object> input = Map.of(
        "publish", List.of()
      );
      
      List<String> options = optionizeFlexible(input);

      assertThat(options).isEmpty();
    }

    @Test
    void shouldReturnEmptyForNullInput() {
      List<String> options = optionizeFlexible(null);

      assertThat(options).isEmpty();
    }

    @Test
    void shouldReturnEmptyForEmptyInput() {
      List<String> options = optionizeFlexible(Map.of());

      assertThat(options).isEmpty();
    }
  }

}