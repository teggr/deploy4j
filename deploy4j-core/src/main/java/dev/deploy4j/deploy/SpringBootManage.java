package dev.deploy4j.deploy;

import dev.deploy4j.deploy.host.commands.SpringBootHostCommands;
import dev.deploy4j.deploy.host.ssh.SshHosts;
import dev.deploy4j.deploy.local.LocalHost;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Spring Boot management operations for deployed applications.
 * 
 * Provides commands to interact with Spring Boot Actuator endpoints
 * on deployed applications via SSH.
 */
public class SpringBootManage extends Base {

  private static final Logger log = LoggerFactory.getLogger(SpringBootManage.class);

  private final SpringBootHostCommands springBoot;

  public SpringBootManage(SshHosts sshHosts, Hooks hooks, LocalHost localHost, SpringBootHostCommands springBoot) {
    super(sshHosts, hooks, localHost);
    this.springBoot = springBoot;
  }

  /**
   * Get health status from Spring Boot Actuator health endpoint.
   */
  public void health(DeployContext deployContext) {
    executeOnSpringBootHosts(deployContext, "health", () -> springBoot.health());
  }

  /**
   * Get application info from Spring Boot Actuator info endpoint.
   */
  public void info(DeployContext deployContext) {
    executeOnSpringBootHosts(deployContext, "info", () -> springBoot.info());
  }

  /**
   * Get environment properties from Spring Boot Actuator env endpoint.
   */
  public void env(DeployContext deployContext) {
    executeOnSpringBootHosts(deployContext, "env", () -> springBoot.env());
  }

  /**
   * Get logger configurations from Spring Boot Actuator loggers endpoint.
   */
  public void loggers(DeployContext deployContext) {
    executeOnSpringBootHosts(deployContext, "loggers", () -> springBoot.loggers());
  }

  /**
   * Get application metrics from Spring Boot Actuator metrics endpoint.
   */
  public void metrics(DeployContext deployContext) {
    executeOnSpringBootHosts(deployContext, "metrics", () -> springBoot.metrics());
  }

  /**
   * Get a specific metric by name from Spring Boot Actuator metrics endpoint.
   */
  public void metrics(DeployContext deployContext, String metricName) {
    executeOnSpringBootHosts(deployContext, "metrics/" + metricName, () -> springBoot.metrics(metricName));
  }

  /**
   * Get thread dump from Spring Boot Actuator threaddump endpoint.
   */
  public void threaddump(DeployContext deployContext) {
    executeOnSpringBootHosts(deployContext, "threaddump", () -> springBoot.threaddump());
  }

  /**
   * Get heap dump from Spring Boot Actuator heapdump endpoint.
   */
  public void heapdump(DeployContext deployContext) {
    executeOnSpringBootHosts(deployContext, "heapdump", () -> springBoot.heapdump());
  }

  /**
   * Get scheduled tasks from Spring Boot Actuator scheduledtasks endpoint.
   */
  public void scheduledtasks(DeployContext deployContext) {
    executeOnSpringBootHosts(deployContext, "scheduledtasks", () -> springBoot.scheduledtasks());
  }

  /**
   * Get HTTP trace from Spring Boot Actuator httptrace endpoint (deprecated, use httpexchanges for Spring Boot 3.x+).
   * @deprecated Use {@link #httpexchanges(DeployContext)} for Spring Boot 3.x and later
   */
  @Deprecated
  public void httptrace(DeployContext deployContext) {
    executeOnSpringBootHosts(deployContext, "httptrace", () -> springBoot.httptrace());
  }

  /**
   * Get HTTP exchange information from Spring Boot Actuator httpexchanges endpoint (Spring Boot 3.x+).
   * This replaces the deprecated httptrace endpoint.
   */
  public void httpexchanges(DeployContext deployContext) {
    executeOnSpringBootHosts(deployContext, "httpexchanges", () -> springBoot.httpexchanges());
  }

  /**
   * Get beans from Spring Boot Actuator beans endpoint.
   */
  public void beans(DeployContext deployContext) {
    executeOnSpringBootHosts(deployContext, "beans", () -> springBoot.beans());
  }

  /**
   * Get conditions from Spring Boot Actuator conditions endpoint.
   */
  public void conditions(DeployContext deployContext) {
    executeOnSpringBootHosts(deployContext, "conditions", () -> springBoot.conditions());
  }

  /**
   * Get config props from Spring Boot Actuator configprops endpoint.
   */
  public void configprops(DeployContext deployContext) {
    executeOnSpringBootHosts(deployContext, "configprops", () -> springBoot.configprops());
  }

  /**
   * Get mappings from Spring Boot Actuator mappings endpoint.
   */
  public void mappings(DeployContext deployContext) {
    executeOnSpringBootHosts(deployContext, "mappings", () -> springBoot.mappings());
  }

  /**
   * Shutdown the application using Spring Boot Actuator shutdown endpoint.
   */
  public void shutdown(DeployContext deployContext) {
    executeOnSpringBootHosts(deployContext, "shutdown", () -> springBoot.shutdown());
  }

  /**
   * Get caches from Spring Boot Actuator caches endpoint.
   */
  public void caches(DeployContext deployContext) {
    executeOnSpringBootHosts(deployContext, "caches", () -> springBoot.caches());
  }

  /**
   * Get flyway migrations from Spring Boot Actuator flyway endpoint.
   */
  public void flyway(DeployContext deployContext) {
    executeOnSpringBootHosts(deployContext, "flyway", () -> springBoot.flyway());
  }

  /**
   * Get liquibase migrations from Spring Boot Actuator liquibase endpoint.
   */
  public void liquibase(DeployContext deployContext) {
    executeOnSpringBootHosts(deployContext, "liquibase", () -> springBoot.liquibase());
  }

  /**
   * Get sessions from Spring Boot Actuator sessions endpoint.
   */
  public void sessions(DeployContext deployContext) {
    executeOnSpringBootHosts(deployContext, "sessions", () -> springBoot.sessions());
  }

  /**
   * Get startup info from Spring Boot Actuator startup endpoint.
   */
  public void startup(DeployContext deployContext) {
    executeOnSpringBootHosts(deployContext, "startup", () -> springBoot.startup());
  }

  /**
   * Access a generic actuator endpoint.
   */
  public void endpoint(DeployContext deployContext, String endpoint) {
    executeOnSpringBootHosts(deployContext, endpoint, () -> springBoot.endpoint(endpoint));
  }

  // Private helper methods

  private void executeOnSpringBootHosts(DeployContext deployContext, String endpointName, 
                                        java.util.function.Supplier<dev.rebelcraft.cmd.Cmd> cmdSupplier) {
    List<String> springBootHosts = getSpringBootHosts(deployContext);
    
    if (springBootHosts.isEmpty()) {
      log.warn("No Spring Boot hosts configured. Check your spring_boot configuration.");
      return;
    }
    
    log.info("Running actuator {} on {} host(s)...", endpointName, springBootHosts.size());
    
    on(deployContext, springBootHosts, host -> {
      log.info("=== {} ===", host.hostName());
      try {
        String result = host.capture(cmdSupplier.get(), false);
        System.out.println(result);
      } catch (Exception e) {
        log.warn("Failed to get {} from {}: {}", endpointName, host.hostName(), e.getMessage());
      }
    });
  }

  private List<String> getSpringBootHosts(DeployContext deployContext) {
    // If specific hosts are set in the deploy context, use those
    if (deployContext.specificHosts() != null && !deployContext.specificHosts().isEmpty()) {
      return deployContext.specificHosts();
    }
    
    // Otherwise use the configured spring boot hosts
    return deployContext.config().springBoot().effectiveHosts();
  }
}
