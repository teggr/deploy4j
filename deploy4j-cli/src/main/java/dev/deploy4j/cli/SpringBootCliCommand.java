package dev.deploy4j.cli;

import dev.deploy4j.deploy.DeployApplicationContext;
import picocli.CommandLine;

import java.util.concurrent.Callable;

@CommandLine.Command(
  name = "spring_boot",
  description = "Spring Boot management commands",
  subcommands = {
    SpringBootCliCommand.ManageCliCommand.class
  }
)
public class SpringBootCliCommand implements Callable<Integer> {

  @CommandLine.Mixin
  private HelpOptions helpOptions = new HelpOptions();

  @Override
  public Integer call() throws Exception {
    CommandLine.usage(this, System.out);
    return 0;
  }

  @CommandLine.Command(
    name = "manage",
    description = "Manage Spring Boot Actuator endpoints",
    subcommands = {
      ManageCliCommand.HealthCliCommand.class,
      ManageCliCommand.InfoCliCommand.class,
      ManageCliCommand.EnvCliCommand.class,
      ManageCliCommand.LoggersCliCommand.class,
      ManageCliCommand.MetricsCliCommand.class,
      ManageCliCommand.ThreaddumpCliCommand.class,
      ManageCliCommand.HeapdumpCliCommand.class,
      ManageCliCommand.ScheduledtasksCliCommand.class,
      ManageCliCommand.HttptraceCliCommand.class,
      ManageCliCommand.BeansCliCommand.class,
      ManageCliCommand.ConditionsCliCommand.class,
      ManageCliCommand.ConfigpropsCliCommand.class,
      ManageCliCommand.MappingsCliCommand.class,
      ManageCliCommand.ShutdownCliCommand.class,
      ManageCliCommand.CachesCliCommand.class,
      ManageCliCommand.FlywayCliCommand.class,
      ManageCliCommand.LiquibaseCliCommand.class,
      ManageCliCommand.SessionsCliCommand.class,
      ManageCliCommand.StartupCliCommand.class,
      ManageCliCommand.EndpointCliCommand.class
    }
  )
  public static class ManageCliCommand implements Callable<Integer> {

    @CommandLine.Mixin
    private HelpOptions helpOptions = new HelpOptions();

    @Override
    public Integer call() throws Exception {
      CommandLine.usage(this, System.out);
      return 0;
    }

    @CommandLine.Command(
      name = "health",
      description = "Get health status from Spring Boot Actuator health endpoint"
    )
    public static class HealthCliCommand extends BaseCliCommand {
      @Override
      protected void execute(DeployApplicationContext deployApplicationContext) {
        deployApplicationContext.springBootManage().health(deployApplicationContext.deployContext());
      }
    }

    @CommandLine.Command(
      name = "info",
      description = "Get application info from Spring Boot Actuator info endpoint"
    )
    public static class InfoCliCommand extends BaseCliCommand {
      @Override
      protected void execute(DeployApplicationContext deployApplicationContext) {
        deployApplicationContext.springBootManage().info(deployApplicationContext.deployContext());
      }
    }

    @CommandLine.Command(
      name = "env",
      description = "Get environment properties from Spring Boot Actuator env endpoint"
    )
    public static class EnvCliCommand extends BaseCliCommand {
      @Override
      protected void execute(DeployApplicationContext deployApplicationContext) {
        deployApplicationContext.springBootManage().env(deployApplicationContext.deployContext());
      }
    }

    @CommandLine.Command(
      name = "loggers",
      description = "Get logger configurations from Spring Boot Actuator loggers endpoint"
    )
    public static class LoggersCliCommand extends BaseCliCommand {
      @Override
      protected void execute(DeployApplicationContext deployApplicationContext) {
        deployApplicationContext.springBootManage().loggers(deployApplicationContext.deployContext());
      }
    }

    @CommandLine.Command(
      name = "metrics",
      description = "Get application metrics from Spring Boot Actuator metrics endpoint"
    )
    public static class MetricsCliCommand extends BaseCliCommand {
      @CommandLine.Parameters(index = "0", arity = "0..1", description = "Specific metric name (optional, e.g., jvm.memory.used)")
      private String metricName;

      @Override
      protected void execute(DeployApplicationContext deployApplicationContext) {
        if (metricName != null && !metricName.isEmpty()) {
          deployApplicationContext.springBootManage().metrics(deployApplicationContext.deployContext(), metricName);
        } else {
          deployApplicationContext.springBootManage().metrics(deployApplicationContext.deployContext());
        }
      }
    }

    @CommandLine.Command(
      name = "threaddump",
      description = "Get thread dump from Spring Boot Actuator threaddump endpoint"
    )
    public static class ThreaddumpCliCommand extends BaseCliCommand {
      @Override
      protected void execute(DeployApplicationContext deployApplicationContext) {
        deployApplicationContext.springBootManage().threaddump(deployApplicationContext.deployContext());
      }
    }

    @CommandLine.Command(
      name = "heapdump",
      description = "Get heap dump from Spring Boot Actuator heapdump endpoint"
    )
    public static class HeapdumpCliCommand extends BaseCliCommand {
      @Override
      protected void execute(DeployApplicationContext deployApplicationContext) {
        deployApplicationContext.springBootManage().heapdump(deployApplicationContext.deployContext());
      }
    }

    @CommandLine.Command(
      name = "scheduledtasks",
      description = "Get scheduled tasks from Spring Boot Actuator scheduledtasks endpoint"
    )
    public static class ScheduledtasksCliCommand extends BaseCliCommand {
      @Override
      protected void execute(DeployApplicationContext deployApplicationContext) {
        deployApplicationContext.springBootManage().scheduledtasks(deployApplicationContext.deployContext());
      }
    }

    @CommandLine.Command(
      name = "httptrace",
      description = "Get HTTP trace from Spring Boot Actuator httptrace endpoint"
    )
    public static class HttptraceCliCommand extends BaseCliCommand {
      @Override
      protected void execute(DeployApplicationContext deployApplicationContext) {
        deployApplicationContext.springBootManage().httptrace(deployApplicationContext.deployContext());
      }
    }

    @CommandLine.Command(
      name = "beans",
      description = "Get beans from Spring Boot Actuator beans endpoint"
    )
    public static class BeansCliCommand extends BaseCliCommand {
      @Override
      protected void execute(DeployApplicationContext deployApplicationContext) {
        deployApplicationContext.springBootManage().beans(deployApplicationContext.deployContext());
      }
    }

    @CommandLine.Command(
      name = "conditions",
      description = "Get conditions from Spring Boot Actuator conditions endpoint"
    )
    public static class ConditionsCliCommand extends BaseCliCommand {
      @Override
      protected void execute(DeployApplicationContext deployApplicationContext) {
        deployApplicationContext.springBootManage().conditions(deployApplicationContext.deployContext());
      }
    }

    @CommandLine.Command(
      name = "configprops",
      description = "Get config props from Spring Boot Actuator configprops endpoint"
    )
    public static class ConfigpropsCliCommand extends BaseCliCommand {
      @Override
      protected void execute(DeployApplicationContext deployApplicationContext) {
        deployApplicationContext.springBootManage().configprops(deployApplicationContext.deployContext());
      }
    }

    @CommandLine.Command(
      name = "mappings",
      description = "Get mappings from Spring Boot Actuator mappings endpoint"
    )
    public static class MappingsCliCommand extends BaseCliCommand {
      @Override
      protected void execute(DeployApplicationContext deployApplicationContext) {
        deployApplicationContext.springBootManage().mappings(deployApplicationContext.deployContext());
      }
    }

    @CommandLine.Command(
      name = "shutdown",
      description = "Shutdown the application using Spring Boot Actuator shutdown endpoint"
    )
    public static class ShutdownCliCommand extends BaseCliCommand {
      @Override
      protected void execute(DeployApplicationContext deployApplicationContext) {
        deployApplicationContext.springBootManage().shutdown(deployApplicationContext.deployContext());
      }
    }

    @CommandLine.Command(
      name = "caches",
      description = "Get caches from Spring Boot Actuator caches endpoint"
    )
    public static class CachesCliCommand extends BaseCliCommand {
      @Override
      protected void execute(DeployApplicationContext deployApplicationContext) {
        deployApplicationContext.springBootManage().caches(deployApplicationContext.deployContext());
      }
    }

    @CommandLine.Command(
      name = "flyway",
      description = "Get flyway migrations from Spring Boot Actuator flyway endpoint"
    )
    public static class FlywayCliCommand extends BaseCliCommand {
      @Override
      protected void execute(DeployApplicationContext deployApplicationContext) {
        deployApplicationContext.springBootManage().flyway(deployApplicationContext.deployContext());
      }
    }

    @CommandLine.Command(
      name = "liquibase",
      description = "Get liquibase migrations from Spring Boot Actuator liquibase endpoint"
    )
    public static class LiquibaseCliCommand extends BaseCliCommand {
      @Override
      protected void execute(DeployApplicationContext deployApplicationContext) {
        deployApplicationContext.springBootManage().liquibase(deployApplicationContext.deployContext());
      }
    }

    @CommandLine.Command(
      name = "sessions",
      description = "Get sessions from Spring Boot Actuator sessions endpoint"
    )
    public static class SessionsCliCommand extends BaseCliCommand {
      @Override
      protected void execute(DeployApplicationContext deployApplicationContext) {
        deployApplicationContext.springBootManage().sessions(deployApplicationContext.deployContext());
      }
    }

    @CommandLine.Command(
      name = "startup",
      description = "Get startup info from Spring Boot Actuator startup endpoint"
    )
    public static class StartupCliCommand extends BaseCliCommand {
      @Override
      protected void execute(DeployApplicationContext deployApplicationContext) {
        deployApplicationContext.springBootManage().startup(deployApplicationContext.deployContext());
      }
    }

    @CommandLine.Command(
      name = "endpoint",
      description = "Access a custom actuator endpoint"
    )
    public static class EndpointCliCommand extends BaseCliCommand {
      @CommandLine.Parameters(index = "0", description = "Custom endpoint path (without the actuator base path)")
      private String endpoint;

      @Override
      protected void execute(DeployApplicationContext deployApplicationContext) {
        deployApplicationContext.springBootManage().endpoint(deployApplicationContext.deployContext(), endpoint);
      }
    }
  }
}
