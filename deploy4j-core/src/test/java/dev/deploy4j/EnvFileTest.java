package dev.deploy4j;

import dev.deploy4j.deploy.env.EnvFile;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EnvFileTest {

  @Test
  void shouldEncodeAMapForDocker() {

    // given
    Map<String, String> env = Map.of(
      "foo", "bar",
      "baz", "haz"
    );

    // when
    String encoded = new EnvFile(env).encode();

    // then
    assertEquals("baz=haz\nfoo=bar\n", encoded);

  }

  @Test
  void shouldNotEscapeChineseCharacters() {

    // given
    Map<String, String> env = Map.of(
      "foo", "你好 means hello, \"欢迎\" means welcome, that's simple! 😃 {smile}"
    );

    // when
    String encoded = new EnvFile(env).encode();

    // then
    assertEquals("foo=你好 means hello, \"欢迎\" means welcome, that's simple! 😃 {smile}\n", encoded);

  }

  @Test
  void shouldNotEscapeJapaneseCharacters() {

    // given
    Map<String, String> env = Map.of(
      "foo", "こんにちは means hello, \"ようこそ\" means welcome, that's simple! 😃 {smile}"
    );

    // when
    String encoded = new EnvFile(env).encode();


    // then
    assertEquals("foo=こんにちは means hello, \"ようこそ\" means welcome, that's simple! 😃 {smile}\n", encoded);

  }

  @Test
  void shouldNotEscapeKoreanCharacters() {

    // given
    Map<String, String> env = Map.of(
      "foo", "안녕하세요 means hello, \"어서 오십시오\" means welcome, that's simple! 😃 {smile}"
    );

    // when
    String encoded = new EnvFile(env).encode();

    // then
    assertEquals("foo=안녕하세요 means hello, \"어서 오십시오\" means welcome, that's simple! 😃 {smile}\n", encoded);

  }

  @Test
  void shouldEncodeASingleNewlineIfEmpty() {

    // when
    String encoded = new EnvFile(Map.of()).encode();

    // then
    assertEquals("\n", encoded);

  }

  @Test
  void shouldDoubleEscapeEscapedNewlines() {

    // given
    Map<String, String> env = Map.of(
      "foo", "hello\\nthere"
    );

    // when
    String encoded = new EnvFile(env).encode();

    // then
    assertEquals( "foo=hello\\\\nthere\n", encoded);

  }

  @Test
  void shouldEscapeNewlines() {

    // givem
    Map<String, String> env = Map.of(
      "foo", "hello\nthere"
    );

    // when
    String encoded = new EnvFile(env).encode();

    // then
    assertEquals("foo=hello\\nthere\n", encoded);
  }

}