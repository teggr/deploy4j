package dev.deploy4j.deploy;

import dev.deploy4j.deploy.host.commands.LockHostCommands;
import dev.deploy4j.deploy.host.commands.ServerHostCommands;
import dev.deploy4j.deploy.host.ssh.SshHosts;

import java.util.List;

public class LockManager {

  private final SshHosts sshHosts;
  private final LockHostCommands lock;
  private final ServerHostCommands server;

  public LockManager(SshHosts sshHosts, LockHostCommands lock, ServerHostCommands server) {
    this.sshHosts = sshHosts;
    this.lock = lock;
    this.server = server;
  }

  public void withLock(LockContext lockContext, Runnable runnable) {
    if (lockContext.holdingLock()) {
      runnable.run();
    } else {
      ensureRunAndLocksDirectory(lockContext);

      acquireLock(lockContext);

      try {
        runnable.run();
      } finally {
        releaseLock(lockContext);
      }

    }
  }

  private void acquireLock(LockContext lockContext) {

    System.out.println("Acquiring the deploy lock...");

    sshHosts.on(List.of(lockContext.primaryHost()), host -> {

      host.execute(lock.acquire("Automatic deploy lock", lockContext.version()));

    });

    lockContext.holdingLock(true);

  }

  private void releaseLock(LockContext lockContext) {
    System.out.println("Releasing the deploy lock...");

    sshHosts.on(List.of(lockContext.primaryHost()), host -> {

      host.execute(lock.release());

    });

    lockContext.holdingLock(false);
  }

  public void raiseIfLocked(LockContext lockContext, Runnable runnable) {

    try {
      runnable.run();
    } catch (RuntimeException e) {
      if (e.getMessage().contains("cannot create directory")) {
        System.out.println("Deploy lock already in place!");

        sshHosts.on(List.of(lockContext.primaryHost()), host -> {

          System.out.println(host.capture(lock.status()));

        });

        throw new RuntimeException("\"Deploy lock found. Run 'deploy4j lock help' for more information\"");
      } else {
        throw e;
      }
    }

  }

  private void ensureRunAndLocksDirectory(LockContext lockContext) {

    sshHosts.on(lockContext.hosts(), host -> {
      host.execute(server.ensureRunDirectory());
    });

    sshHosts.on(List.of(lockContext.primaryHost()), host -> {
      host.execute(lock.ensureLocksDirectory());
    });

  }

}
