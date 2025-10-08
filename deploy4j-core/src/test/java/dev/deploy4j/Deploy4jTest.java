package dev.deploy4j;

import dev.deploy4j.configuration.*;

import java.util.List;
import java.util.Map;

import static dev.deploy4j.Commands.optionize;

class Deploy4jTest {

  public static void main(String[] args) {

    Deploy4jConfig config = new Deploy4jConfig(
      null,
      "0.0.1-SNAPSHOT",
      "deploy4j-demo",
      "teggr/deploy4j-demo",
      new RegistryConfig(
        "",
        "DOCKER_USERNAME",
        "DOCKER_PASSWORD"
      ),
      List.of(new ServerConfig("localhost")),
      new SshConfig(
        "root",
        2222,
        System.getenv("PRIVATE_KEY"),
        System.getenv("PRIVATE_KEY_PASSPHRASE"),
        false
      ),
      Map.of("DATABASE_HOST", "mysql-db"),
      new TraefikConfig(
        Map.of(
          "publish", "8080:8080"
        ),
        Map.of(
        "api.insecure", "true"
      )),
      null
    );

    try (Deploy4j deploy4j = new Deploy4j(new Context(config));) {

      deploy4j.setup();

    } catch (Exception e) {

      throw new RuntimeException(e);

    }

  }

}