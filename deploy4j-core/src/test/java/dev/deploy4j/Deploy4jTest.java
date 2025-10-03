package dev.deploy4j;

import dev.deploy4j.configuration.Deploy4jConfig;
import dev.deploy4j.configuration.ServerConfig;
import dev.deploy4j.configuration.SshConfig;

import java.util.List;
import java.util.Map;

class Deploy4jTest {

  public static void main(String[] args) {

    Deploy4jConfig config = new Deploy4jConfig(
      "deploy4j-demo",
      "teggr/deploy4j-demo:0.0.1-SNAPSHOT",
      List.of(new ServerConfig( "localhost" )),
      new SshConfig(
        2222,
        System.getenv("PRIVATE_KEY"),
        System.getenv("PRIVATE_KEY_PASSPHRASE"),
        false
      ),
      Map.of( "DATABASE_HOST", "mysql-db" ),
      null
    );

    Deploy4j deploy4j = new Deploy4j();
    deploy4j.run(config);

  }


}