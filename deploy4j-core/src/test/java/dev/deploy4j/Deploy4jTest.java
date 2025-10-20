package dev.deploy4j;

import dev.deploy4j.cli.Cli;
import dev.deploy4j.cli.Main;
import dev.deploy4j.configuration.Configuration;
import dev.deploy4j.raw.*;

import java.util.List;
import java.util.Map;

class Deploy4jTest {

  public static void main(String[] args) {

//    Deploy4jConfig rawConfig = new Deploy4jConfig(
//      "deploy4j-demo",
//      "teggr/deploy4j-demo",
//      new RegistryConfig(
//        "",
//        "DOCKER_USERNAME",
//        "DOCKER_PASSWORD"
//      ),
//      List.of(new HostListConfig("localhost", List.of())),
//      Map.of(),
//      new SshConfig(
//        "root",
//        "2222",
//        System.getenv("PRIVATE_KEY"),
//        System.getenv("PRIVATE_KEY_PASSPHRASE"),
//        false
//      ),
//      Map.of("DATABASE_HOST", "mysql-db"),
//      Map.of(),
//      new TraefikConfig(
//        null,
//        null,
//        true,
//        Map.of(),
//        Map.of(
//          "publish", "8080:8080"
//        ),
//        Map.of(
//          "api.insecure", "true"
//        ),
//        Map.of()
//      ),
//      new HealthCheckConfig(null, null, 5, null, null)
//    );
//
//    Configuration config = new Configuration(
//      rawConfig,
//      "local",
//      "0.0.1-SNAPSHOT"
//    );
//
//    // TODO: CLI OPTIONS TO INFLUENCE THESE
//    try ( Commander commander = new Commander(config) ) {
//
////    commander.setVerbosity();
////    commander.configure( configFile, destination, version );
////    commander.specificHosts();
////    commander.specificRoles();
////    commander.specificPrimary();
//
//      Environment environment = new Environment(config.destination());
//
//      Cli cli = new Cli(environment, commander);
//
//      Main main = cli.main();
//
//      main.setup(true);
//
//    } catch (Exception e) {
//
//      throw new RuntimeException(e);
//
//    }

  }

}