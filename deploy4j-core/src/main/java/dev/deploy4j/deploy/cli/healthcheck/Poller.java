package dev.deploy4j.deploy.cli.healthcheck;

import dev.deploy4j.deploy.cli.Commander;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Supplier;

public class Poller {

  private static final Logger log = LoggerFactory.getLogger(Poller.class);

  private static final Integer TRAEFIK_UPDATE_DELAY = 5;

  private final Commander commander;

  public Poller(Commander commander) {
    this.commander = commander;
  }

  public void waitForHealthy(boolean pauseAfterReady, Supplier<String> block) {

    int attempt = 1;
    int maxAttempts = commander.config().healthcheck().maxAttempts();

    try {

      boolean containerReady = false;

      while (!containerReady) {

        String status = block.get();

        switch (status) {

          case "healthy":
            containerReady = true;
            if( pauseAfterReady ) {
              Thread.sleep( TRAEFIK_UPDATE_DELAY * 1000 );
            }
            break;
          case "running":
            containerReady = true;
            if( pauseAfterReady ) {
              Thread.sleep( commander.config().readinessDelay() );
            }
            break;
          default:
            break;
        }

        if(attempt <= maxAttempts) {
          log.info( "{}, retrying in {}s (attempt {}/{})...", status, attempt, attempt, maxAttempts );
          Thread.sleep( attempt * TRAEFIK_UPDATE_DELAY );
          attempt = attempt + 1;
        } else {
          throw new RuntimeException( "container not ready (" + status + ")" );
        }

      }

    } catch (Exception e) {


    }

    log.info("Container is healthy");

  }

  public void waitForUnhealthy(boolean pauseAfterReady, Supplier<String> block) {

    int attempt = 1;
    int maxAttempts = commander.config().healthcheck().maxAttempts();

    try {

      boolean containerUnhealthy = false;

      while (!containerUnhealthy) {

        String status = block.get();

        switch (status) {

          case "unhealthy":
            containerUnhealthy = true;
            if( pauseAfterReady ) {
              Thread.sleep( TRAEFIK_UPDATE_DELAY * 1000 );
            }
            break;
          default:
            break;
        }

        if(attempt <= maxAttempts) {
          log.info( "{}, retrying in {}s (attempt {}/{})...", status, attempt, attempt, maxAttempts );
          Thread.sleep( attempt * TRAEFIK_UPDATE_DELAY );
          attempt = attempt + 1;
        } else {
          throw new RuntimeException( "container not unhealthy (" + status + ")" );
        }

      }

    } catch (Exception e) {


    }

    log.info("Container is unhealthy!");

  }

}
