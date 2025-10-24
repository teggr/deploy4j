package dev.deploy4j.deploy.healthcheck;

import dev.deploy4j.deploy.DeployContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Supplier;

public class Poller {

  private static final Logger log = LoggerFactory.getLogger(Poller.class);

  private static final Integer TRAEFIK_UPDATE_DELAY = 5;

  private final DeployContext deployContext;

  public Poller(DeployContext deployContext) {
    this.deployContext = deployContext;
  }

  public void waitForHealthy(boolean pauseAfterReady, Supplier<String> block) {

    int attempt = 1;
    int maxAttempts = deployContext.config().healthcheck().maxAttempts();

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
              Thread.sleep( deployContext.config().readinessDelay() );
            }
            break;
          default:
            break;
        }

        if(attempt <= maxAttempts) {
          log.debug( "{}, retrying in {}s (attempt {}/{})...", status, attempt, attempt, maxAttempts );
          Thread.sleep( attempt * TRAEFIK_UPDATE_DELAY );
          attempt = attempt + 1;
        } else {
          throw new RuntimeException( "container not ready (" + status + ")" );
        }

      }

    } catch (Exception e) {


    }

    log.debug("Container is healthy");

  }

  public void waitForUnhealthy(boolean pauseAfterReady, Supplier<String> block) {

    int attempt = 1;
    int maxAttempts = deployContext.config().healthcheck().maxAttempts();

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
          log.debug( "{}, retrying in {}s (attempt {}/{})...", status, attempt, attempt, maxAttempts );
          Thread.sleep( attempt * TRAEFIK_UPDATE_DELAY );
          attempt = attempt + 1;
        } else {
          throw new RuntimeException( "container not unhealthy (" + status + ")" );
        }

      }

    } catch (Exception e) {


    }

    log.debug("Container is unhealthy!");

  }

}
