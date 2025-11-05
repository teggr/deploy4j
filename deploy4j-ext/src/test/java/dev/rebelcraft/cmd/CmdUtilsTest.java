package dev.rebelcraft.cmd;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static dev.rebelcraft.cmd.CmdUtils.optionize;
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

}