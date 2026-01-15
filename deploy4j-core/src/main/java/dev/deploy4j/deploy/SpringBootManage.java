package dev.deploy4j.deploy;

import dev.deploy4j.deploy.configuration.Role;
import dev.deploy4j.deploy.host.commands.AppHostCommandsFactory;
import dev.deploy4j.deploy.host.commands.SpringBootHostCommands;
import dev.deploy4j.deploy.host.commands.SpringBootHostCommandsFactory;
import dev.deploy4j.deploy.host.ssh.SshHosts;
import dev.deploy4j.deploy.local.LocalHost;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Spring Boot management operations for deployed applications.
 * 
 * Provides commands to interact with Spring Boot Actuator endpoints
 * on deployed applications via SSH and Docker.
 * 
 * Commands are executed by running a curl container attached to the deploy4j
 * Docker network, allowing access to app containers by their container name.
 */
public class SpringBootManage extends Base {

  private static final Logger log = LoggerFactory.getLogger(SpringBootManage.class);

  private final SpringBootHostCommandsFactory springBootFactory;
  private final AppHostCommandsFactory appFactory;

  public SpringBootManage(SshHosts sshHosts, Hooks hooks, LocalHost localHost, 
                          SpringBootHostCommandsFactory springBootFactory,
                          AppHostCommandsFactory appFactory) {
    super(sshHosts, hooks, localHost);
    this.springBootFactory = springBootFactory;
    this.appFactory = appFactory;
  }

  /**
   * Get health status from Spring Boot Actuator health endpoint.
   */
  public void health(DeployContext deployContext) {
    executeOnSpringBootHosts(deployContext, "health", SpringBootHostCommands::health);
  }

  /**
   * Get application info from Spring Boot Actuator info endpoint.
   */
  public void info(DeployContext deployContext) {
    executeOnSpringBootHosts(deployContext, "info", SpringBootHostCommands::info);
  }

  /**
   * Get environment properties from Spring Boot Actuator env endpoint.
   */
  public void env(DeployContext deployContext) {
    executeOnSpringBootHosts(deployContext, "env", SpringBootHostCommands::env);
  }

  /**
   * Get logger configurations from Spring Boot Actuator loggers endpoint.
   */
  public void loggers(DeployContext deployContext) {
    executeOnSpringBootHosts(deployContext, "loggers", SpringBootHostCommands::loggers);
  }

  /**
   * Get application metrics from Spring Boot Actuator metrics endpoint.
   */
  public void metrics(DeployContext deployContext) {
    executeOnSpringBootHosts(deployContext, "metrics", SpringBootHostCommands::metrics);
  }

  /**
   * Get a specific metric by name from Spring Boot Actuator metrics endpoint.
   */
  public void metrics(DeployContext deployContext, String metricName) {
    executeOnSpringBootHosts(deployContext, "metrics/" + metricName, 
      commands -> commands.metrics(metricName));
  }

  /**
   * Get thread dump from Spring Boot Actuator threaddump endpoint.
   */
  public void threaddump(DeployContext deployContext) {
    executeOnSpringBootHosts(deployContext, "threaddump", SpringBootHostCommands::threaddump);
  }

  /**
   * Get heap dump from Spring Boot Actuator heapdump endpoint.
   */
  public void heapdump(DeployContext deployContext) {
    executeOnSpringBootHosts(deployContext, "heapdump", SpringBootHostCommands::heapdump);
  }

  /**
   * Get scheduled tasks from Spring Boot Actuator scheduledtasks endpoint.
   */
  public void scheduledtasks(DeployContext deployContext) {
    executeOnSpringBootHosts(deployContext, "scheduledtasks", SpringBootHostCommands::scheduledtasks);
  }

  /**
   * Get HTTP trace from Spring Boot Actuator httptrace endpoint (deprecated, use httpexchanges for Spring Boot 3.x+).
   * @deprecated Use {@link #httpexchanges(DeployContext)} for Spring Boot 3.x and later
   */
  @Deprecated
  public void httptrace(DeployContext deployContext) {
    executeOnSpringBootHosts(deployContext, "httptrace", SpringBootHostCommands::httptrace);
  }

  /**
   * Get HTTP exchange information from Spring Boot Actuator httpexchanges endpoint (Spring Boot 3.x+).
   * This replaces the deprecated httptrace endpoint.
   */
  public void httpexchanges(DeployContext deployContext) {
    executeOnSpringBootHosts(deployContext, "httpexchanges", SpringBootHostCommands::httpexchanges);
  }

  /**
   * Get beans from Spring Boot Actuator beans endpoint.
   */
  public void beans(DeployContext deployContext) {
    executeOnSpringBootHosts(deployContext, "beans", SpringBootHostCommands::beans);
  }

  /**
   * Get conditions from Spring Boot Actuator conditions endpoint.
   */
  public void conditions(DeployContext deployContext) {
    executeOnSpringBootHosts(deployContext, "conditions", SpringBootHostCommands::conditions);
  }

  /**
   * Get config props from Spring Boot Actuator configprops endpoint.
   */
  public void configprops(DeployContext deployContext) {
    executeOnSpringBootHosts(deployContext, "configprops", SpringBootHostCommands::configprops);
  }

  /**
   * Get mappings from Spring Boot Actuator mappings endpoint.
   */
  public void mappings(DeployContext deployContext) {
    executeOnSpringBootHosts(deployContext, "mappings", SpringBootHostCommands::mappings);
  }

  /**
   * Shutdown the application using Spring Boot Actuator shutdown endpoint.
   */
  public void shutdown(DeployContext deployContext) {
    executeOnSpringBootHosts(deployContext, "shutdown", SpringBootHostCommands::shutdown);
  }

  /**
   * Get caches from Spring Boot Actuator caches endpoint.
   */
  public void caches(DeployContext deployContext) {
    executeOnSpringBootHosts(deployContext, "caches", SpringBootHostCommands::caches);
  }

  /**
   * Get flyway migrations from Spring Boot Actuator flyway endpoint.
   */
  public void flyway(DeployContext deployContext) {
    executeOnSpringBootHosts(deployContext, "flyway", SpringBootHostCommands::flyway);
  }

  /**
   * Get liquibase migrations from Spring Boot Actuator liquibase endpoint.
   */
  public void liquibase(DeployContext deployContext) {
    executeOnSpringBootHosts(deployContext, "liquibase", SpringBootHostCommands::liquibase);
  }

  /**
   * Get sessions from Spring Boot Actuator sessions endpoint.
   */
  public void sessions(DeployContext deployContext) {
    executeOnSpringBootHosts(deployContext, "sessions", SpringBootHostCommands::sessions);
  }

  /**
   * Get startup info from Spring Boot Actuator startup endpoint.
   */
  public void startup(DeployContext deployContext) {
    executeOnSpringBootHosts(deployContext, "startup", SpringBootHostCommands::startup);
  }

  /**
   * Access a generic actuator endpoint.
   */
  public void endpoint(DeployContext deployContext, String endpoint) {
    executeOnSpringBootHosts(deployContext, endpoint, 
      commands -> commands.endpoint(endpoint));
  }

  // Private helper methods

  private void executeOnSpringBootHosts(DeployContext deployContext, String endpointName,
                                        java.util.function.Function<SpringBootHostCommands, dev.rebelcraft.cmd.Cmd> cmdFunction) {

    List<String> hosts = deployContext.appHosts();

    if (hosts.isEmpty()) {
      log.warn("No Spring Boot hosts configured. Check your spring_boot configuration.");
      return;
    }

    log.info("Running actuator {} on {} host(s)...", endpointName, hosts.size());
    
    on(deployContext, hosts, host -> {

      // Get the roles for this host
      List<Role> roles = deployContext.rolesOn(host.hostName());
      
      for (Role role : roles) {

        // Get the container name for this role using the current version
        String containerName = role.containerName(deployContext.config().version());
        
        log.info("=== {} ({}) ===", host.hostName(), containerName);
        
        try {

          // Create commands for this specific container
          SpringBootHostCommands commands = springBootFactory.forContainer(containerName);
          
          // Pull the curl image first to avoid mixing docker logs with output
          host.execute(commands.pullCurlImage(), false);
          
          // Execute the actual command and capture output
          String result = host.capture(cmdFunction.apply(commands), false);
          System.out.println(result);

        } catch (Exception e) {
          log.warn("Failed to get {} from {} ({}): {}", endpointName, host.hostName(), containerName, e.getMessage());
        }

      }

    });
  }

}
