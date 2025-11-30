package dev.deploy4j.deploy.host.commands;

import dev.deploy4j.deploy.configuration.Configuration;
import dev.rebelcraft.cmd.Cmd;

import static dev.rebelcraft.cmd.pkgs.Docker.docker;

/**
 * Host commands for Spring Boot Actuator endpoints.
 * 
 * These commands generate docker run commands that execute curl in a container
 * attached to the deploy4j network, allowing access to app containers by their name.
 */
public class SpringBootHostCommands extends BaseHostCommands {

  private static final String CURL_IMAGE = "curlimages/curl";
  private static final String NETWORK = "deploy4j";

  private final String containerName;

  public SpringBootHostCommands(Configuration config, String containerName) {
    super(config);
    this.containerName = containerName;
  }

  /**
   * Pull the curl Docker image to ensure it's available before executing commands.
   * This prevents Docker pull logs from mixing with curl output.
   */
  public Cmd pullCurlImage() {
    return docker().pull().args(CURL_IMAGE).description("pull curl image");
  }

  /**
   * Get the health endpoint status.
   * @see <a href="https://docs.spring.io/spring-boot/reference/actuator/endpoints.html#actuator.endpoints.health">Health endpoint</a>
   */
  public Cmd health() {
    return curlEndpoint("health").description("actuator health");
  }

  /**
   * Get application info.
   * @see <a href="https://docs.spring.io/spring-boot/reference/actuator/endpoints.html#actuator.endpoints.info">Info endpoint</a>
   */
  public Cmd info() {
    return curlEndpoint("info").description("actuator info");
  }

  /**
   * Get application environment properties.
   * @see <a href="https://docs.spring.io/spring-boot/reference/actuator/endpoints.html#actuator.endpoints.env">Env endpoint</a>
   */
  public Cmd env() {
    return curlEndpoint("env").description("actuator env");
  }

  /**
   * Get logger configurations.
   * @see <a href="https://docs.spring.io/spring-boot/reference/actuator/endpoints.html#actuator.endpoints.loggers">Loggers endpoint</a>
   */
  public Cmd loggers() {
    return curlEndpoint("loggers").description("actuator loggers");
  }

  /**
   * Get application metrics.
   * @see <a href="https://docs.spring.io/spring-boot/reference/actuator/endpoints.html#actuator.endpoints.metrics">Metrics endpoint</a>
   */
  public Cmd metrics() {
    return curlEndpoint("metrics").description("actuator metrics");
  }

  /**
   * Get a specific metric by name.
   * @param metricName the name of the metric (e.g., "jvm.memory.used")
   */
  public Cmd metrics(String metricName) {
    return curlEndpoint("metrics/" + metricName).description("actuator metrics " + metricName);
  }

  /**
   * Get thread dump.
   * @see <a href="https://docs.spring.io/spring-boot/reference/actuator/endpoints.html#actuator.endpoints.threaddump">Threaddump endpoint</a>
   */
  public Cmd threaddump() {
    return curlEndpoint("threaddump").description("actuator threaddump");
  }

  /**
   * Get heap dump.
   * @see <a href="https://docs.spring.io/spring-boot/reference/actuator/endpoints.html#actuator.endpoints.heapdump">Heapdump endpoint</a>
   */
  public Cmd heapdump() {
    return curlEndpoint("heapdump").description("actuator heapdump");
  }

  /**
   * Get scheduled tasks.
   * @see <a href="https://docs.spring.io/spring-boot/reference/actuator/endpoints.html#actuator.endpoints.scheduledtasks">Scheduledtasks endpoint</a>
   */
  public Cmd scheduledtasks() {
    return curlEndpoint("scheduledtasks").description("actuator scheduledtasks");
  }

  /**
   * Get HTTP trace information (deprecated, use httpexchanges for Spring Boot 3.x+).
   * @see <a href="https://docs.spring.io/spring-boot/reference/actuator/endpoints.html#actuator.endpoints.httptrace">Httptrace endpoint</a>
   * @deprecated Use {@link #httpexchanges()} for Spring Boot 3.x and later
   */
  @Deprecated
  public Cmd httptrace() {
    return curlEndpoint("httptrace").description("actuator httptrace");
  }

  /**
   * Get HTTP exchange information (Spring Boot 3.x+).
   * This replaces the deprecated httptrace endpoint.
   * @see <a href="https://docs.spring.io/spring-boot/reference/actuator/endpoints.html#actuator.endpoints.httpexchanges">Httpexchanges endpoint</a>
   */
  public Cmd httpexchanges() {
    return curlEndpoint("httpexchanges").description("actuator httpexchanges");
  }

  /**
   * Get beans.
   * @see <a href="https://docs.spring.io/spring-boot/reference/actuator/endpoints.html#actuator.endpoints.beans">Beans endpoint</a>
   */
  public Cmd beans() {
    return curlEndpoint("beans").description("actuator beans");
  }

  /**
   * Get conditions evaluation report.
   * @see <a href="https://docs.spring.io/spring-boot/reference/actuator/endpoints.html#actuator.endpoints.conditions">Conditions endpoint</a>
   */
  public Cmd conditions() {
    return curlEndpoint("conditions").description("actuator conditions");
  }

  /**
   * Get configuration properties.
   * @see <a href="https://docs.spring.io/spring-boot/reference/actuator/endpoints.html#actuator.endpoints.configprops">Configprops endpoint</a>
   */
  public Cmd configprops() {
    return curlEndpoint("configprops").description("actuator configprops");
  }

  /**
   * Get mappings.
   * @see <a href="https://docs.spring.io/spring-boot/reference/actuator/endpoints.html#actuator.endpoints.mappings">Mappings endpoint</a>
   */
  public Cmd mappings() {
    return curlEndpoint("mappings").description("actuator mappings");
  }

  /**
   * Shutdown the application (POST request).
   * @see <a href="https://docs.spring.io/spring-boot/reference/actuator/endpoints.html#actuator.endpoints.shutdown">Shutdown endpoint</a>
   */
  public Cmd shutdown() {
    return curlEndpointPost("shutdown").description("actuator shutdown");
  }

  /**
   * Get caches.
   * @see <a href="https://docs.spring.io/spring-boot/reference/actuator/endpoints.html#actuator.endpoints.caches">Caches endpoint</a>
   */
  public Cmd caches() {
    return curlEndpoint("caches").description("actuator caches");
  }

  /**
   * Get flyway migrations.
   * @see <a href="https://docs.spring.io/spring-boot/reference/actuator/endpoints.html#actuator.endpoints.flyway">Flyway endpoint</a>
   */
  public Cmd flyway() {
    return curlEndpoint("flyway").description("actuator flyway");
  }

  /**
   * Get liquibase migrations.
   * @see <a href="https://docs.spring.io/spring-boot/reference/actuator/endpoints.html#actuator.endpoints.liquibase">Liquibase endpoint</a>
   */
  public Cmd liquibase() {
    return curlEndpoint("liquibase").description("actuator liquibase");
  }

  /**
   * Get sessions.
   * @see <a href="https://docs.spring.io/spring-boot/reference/actuator/endpoints.html#actuator.endpoints.sessions">Sessions endpoint</a>
   */
  public Cmd sessions() {
    return curlEndpoint("sessions").description("actuator sessions");
  }

  /**
   * Get startup information.
   * @see <a href="https://docs.spring.io/spring-boot/reference/actuator/endpoints.html#actuator.endpoints.startup">Startup endpoint</a>
   */
  public Cmd startup() {
    return curlEndpoint("startup").description("actuator startup");
  }

  /**
   * Generic endpoint access.
   * @param endpoint the endpoint path (without the actuator base path)
   */
  public Cmd endpoint(String endpoint) {
    return curlEndpoint(endpoint).description("actuator " + endpoint);
  }

  // Private helper methods

  /**
   * Creates a docker run command that executes curl in a container attached to the deploy4j network.
   * This allows curl to access app containers by their container name.
   */
  private Cmd curlEndpoint(String endpoint) {
    String url = config().springBoot().endpointUrl(endpoint, containerName);
    return docker().run()
      .args("--rm")
      .args("--network", NETWORK)
      .args(CURL_IMAGE)
      .args("-s", url);
  }

  private Cmd curlEndpointPost(String endpoint) {
    String url = config().springBoot().endpointUrl(endpoint, containerName);
    return docker().run()
      .args("--rm")
      .args("--network", NETWORK)
      .args(CURL_IMAGE)
      .args("-s", "-X", "POST", url);
  }
}
