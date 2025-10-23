package dev.deploy4j.deploy;

import dev.deploy4j.deploy.host.commands.LockHostCommands;
import dev.deploy4j.deploy.host.commands.ServerHostCommands;
import dev.deploy4j.deploy.host.ssh.SshHosts;

import java.util.List;

public class Lock extends Base {

  private final LockManager lockManager;
  private final ServerHostCommands server;
  private final LockHostCommands lock;

  public Lock(SshHosts sshHosts, LockManager lockManager, ServerHostCommands server, LockHostCommands lock) {
    super(sshHosts);
    this.lockManager = lockManager;
    this.server = server;
    this.lock = lock;
  }

  /**
   * Report lock status
   */
  public void status(DeployContext deployContext) {

    handleMissingLock(() -> {

      on(List.of(deployContext.primaryHost()), host -> {;

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
  public void acquire(DeployContext deployContext, String message) {

    lockManager.raiseIfLocked(deployContext, () -> {

      on(List.of(deployContext.primaryHost()), host -> {

        host.execute(server.ensureRunDirectory());
        host.execute(lock.acquire(message, deployContext.config().version()));

      });

      System.out.println("Acquired the deploy lock");

    });

  }

  /**
   * Release the deploy lock
   */
  public void release(DeployContext deployContext) {

    handleMissingLock(() -> {

      on(List.of(deployContext.primaryHost()), host -> {

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
