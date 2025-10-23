package dev.deploy4j.deploy;

import dev.deploy4j.deploy.host.ssh.SshHost;

import java.util.List;
import java.util.function.Consumer;

public class Base {

  private final Commander commander;

  public Base(Commander commander) {
    this.commander = commander;
  }

  public Commander commander() {
    return commander;
  }

  // private

  public void withLock(Runnable runnable) {
    if (commander.holdingLock()) {
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

  private void acquireLock() {

    System.out.println("Acquiring the deploy lock...");

    on(List.of(commander.primaryHost()), host -> {

      host.execute(commander.lock().acquire("Automatic deploy lock", commander.config().version()));

    });

    commander.holdingLock(true);

  }

  private void releaseLock() {
    System.out.println("Releasing the deploy lock...");

    on(List.of(commander.primaryHost()), host -> {

      host.execute(commander.lock().release());

    });

    commander.holdingLock(false);
  }

  protected void raiseIfLocked(Runnable runnable) {

    try {
      runnable.run();
    } catch (RuntimeException e) {
      if (e.getMessage().contains("cannot create directory")) {
        System.out.println("Deploy lock already in place!");

        on(List.of(commander.primaryHost()), host -> {

          System.out.println(host.capture(commander.lock().status()));

        });

        throw new RuntimeException("\"Deploy lock found. Run 'deploy4j lock help' for more information\"");
      } else {
        throw e;
      }
    }

  }

  protected void on(List<String> hosts, Consumer<SshHost> block) {

    hosts.stream()
      .map(commander::host)
      .forEach(block);

  }

  private void ensureRunAndLocksDirectory() {

    on(commander.hosts(), host -> {
      host.execute(commander.server().ensureRunDirectory());
    });

    on(List.of(commander.primaryHost()), host -> {
      host.execute(commander.lock().ensureLocksDirectory());
    });

  }

}
