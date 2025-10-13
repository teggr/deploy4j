package dev.deploy4j.cli;

import dev.deploy4j.Commander;
import dev.deploy4j.ssh.SshHost;

import java.util.List;

public class Lock {

  private final Cli cli;
  private final Commander commander;

  private boolean holdingLock = false;

  public Lock(Cli cli, Commander commander) {
    this.cli = cli;
    this.commander = commander;
  }

  public void withLock(Runnable runnable) {

    if (holdingLock()) {
      runnable.run();
    } else {
      ensureRunAndLocksDirectory();

      acquireLock();

      try {
        runnable.run();
      } finally {
        releaseLock();
      }


    }

  }

  private void releaseLock() {
    System.out.println("Releasing the deploy lock...");
    for (SshHost host : cli.on(List.of(commander.primaryHost()))) {

      host.execute(commander.lock().release());

    }
    holdingLock = false;
  }

  private void acquireLock() {

    System.out.println("Acquiring the deploy lock...");

    for (SshHost host : cli.on(List.of(commander.primaryHost()))) {

      host.execute(commander.lock().acquire("Automatic deploy lock", commander.config().version()));

    }

    holdingLock = true;

  }

  private void ensureRunAndLocksDirectory() {

    for (SshHost host : cli.on(commander.hosts())) {

      host.execute(commander.server().ensureRunDirectory());

    }

    for (SshHost host : cli.on(List.of(commander.primaryHost()))) {

      host.execute(commander.lock().ensureLocksDirectory());

    }

  }

  private boolean holdingLock() {
    return holdingLock;
  }

  /**
   * Report lock status
   */
  public void status() {

    handleMissingLock(() -> {

      for (SshHost host : cli.on(List.of(commander.primaryHost()))) {

        host.execute(commander.server().ensureRunDirectory());
        System.out.println( host.capture( commander.lock().status() ) );

      }

    });

  }

  /**
   * Acquire the deploy lock
   *
   * @param message A lock message
   */
  public void acquire(String message) {

    raiseIfLocked(() -> {

      for (SshHost host : cli.on(List.of(commander.primaryHost()))) {

        host.execute(commander.server().ensureRunDirectory());
        host.execute(commander.lock().acquire(message, commander.config().version()));

      }

      System.out.println("Acquired the deploy lock");

    });

  }

  /**
   * Release the deploy lock
   */
  public void release() {

    handleMissingLock(() -> {

      for (SshHost host : cli.on(List.of(commander.primaryHost()))) {

        host.execute(commander.server().ensureRunDirectory());
        host.execute(commander.lock().release());

      }

      System.out.println("Released the deploy lock");

    });

  }

  private void raiseIfLocked(Runnable runnable) {

    try {
      runnable.run();
    } catch (RuntimeException e) {
      if (e.getMessage().contains("cannot create directory")) {
        System.out.println("Deploy lock already in place!");

        for(SshHost host : cli.on(List.of(commander.primaryHost()))) {

          System.out.println( host.capture( commander.lock().status() ) );

        }

        throw new RuntimeException("\"Deploy lock found. Run 'deploy4j lock help' for more information\"");
      } else {
        throw e;
      }
    }

  }

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
