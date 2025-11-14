package dev.deploy4j.deploy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Hooks {

  private static final Logger log = LoggerFactory.getLogger(Hooks.class);

  public void runHook(String hookName) {
    // TODO:
    log.info("Hook {} started", hookName);

  }
}
