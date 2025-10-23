package dev.deploy4j.deploy;

import dev.deploy4j.deploy.host.commands.LockHostCommands;
import dev.deploy4j.deploy.host.commands.ServerHostCommands;

import java.util.List;

public class Lock extends Base {

  private final ServerHostCommands server;
  private final LockHostCommands lock;

  public Lock(Commander commander, ServerHostCommands server, LockHostCommands lock) {
    super(commander);
    this.server = server;
    this.lock = lock;
  }

  /**
   * Report lock status
   */
  public void status() {

    handleMissingLock(() -> {

      on(List.of(commander().primaryHost()), host -> {;

        host.execute(server.ensureRunDirectory());
        System.out.println( host.capture( lock.status() ) );

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

        host.execute(server.ensureRunDirectory());
        host.execute(lock.acquire(message, commander().config().version()));

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

        host.execute(server.ensureRunDirectory());
        host.execute(lock.release());

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
