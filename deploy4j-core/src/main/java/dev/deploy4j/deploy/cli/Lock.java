package dev.deploy4j.deploy.cli;

import java.util.List;

public class Lock extends Base {

  public Lock(Cli cli, Commander commander) {
    super(cli, commander);
  }

  /**
   * Report lock status
   */
  public void status() {

    handleMissingLock(() -> {

      on(List.of(commander().primaryHost()), host -> {;

        host.execute(commander().server().ensureRunDirectory());
        System.out.println( host.capture( commander().lock().status() ) );

      });

    });

  }

  /**
   * Acquire the deploy lock
   *
   * @param message A lock message
   */
  public void acquire(String message) {

    raiseIfLocked(() -> {

      on(List.of(commander().primaryHost()), host -> {

        host.execute(commander().server().ensureRunDirectory());
        host.execute(commander().lock().acquire(message, commander().config().version()));

      });

      System.out.println("Acquired the deploy lock");

    });

  }

  /**
   * Release the deploy lock
   */
  public void release() {

    handleMissingLock(() -> {

      on(List.of(commander().primaryHost()), host -> {

        host.execute(commander().server().ensureRunDirectory());
        host.execute(commander().lock().release());

      });

      System.out.println("Released the deploy lock");

    });

  }

  // private

  private void handleMissingLock(Runnable runnable) {

    try {
      runnable.run();
    } catch (RuntimeException e) {
      if (e.getMessage().contains("No such file or directory")) {
        System.out.println("There is no deploy lock");
      } else {
        throw e;
      }
    }

  }

}
