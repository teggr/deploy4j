package dev.deploy4j.jdemo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.wait.strategy.LogMessageWaitStrategy;

import java.io.File;
import java.time.Duration;
import java.time.temporal.ChronoUnit;

@Slf4j
class Deploy4jDemoApplicationTest {

  public static void main(String[] args) {

    SpringApplication.from(Deploy4jDemoApplication::main)
      .with(TestConfig.class).run(args);

  }

  @TestConfiguration
  static class TestConfig {

    @Bean
    @ServiceConnection
    public PostgreSQLContainer<?> postgreSQLContainer() {
      File dataDir = new File(".data");
      dataDir.mkdirs();
      log.info("dataDir: {}", dataDir.getAbsolutePath());
      return new PostgreSQLContainer<>("postgres:18-alpine")
        .withDatabaseName("testdb")
        .withUsername("testuser")
        .withPassword("testpass")
        .withFileSystemBind(
          dataDir.getAbsolutePath(), "/var/lib/postgresql/18/docker", BindMode.READ_WRITE)
        //.waitingFor(Wait.forListeningPorts(5432));
        // Custom waiter
        .waitingFor((new LogMessageWaitStrategy())
          .withRegEx(".*database system is ready to accept connections.*\\s")
          .withTimes(1)
          .withStartupTimeout(Duration.of(60L, ChronoUnit.SECONDS))
        );
    }

  }

}