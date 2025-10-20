package dev.deploy4j.cli.healthcheck;

import java.util.concurrent.atomic.AtomicBoolean;

public class Barrier {

  private AtomicBoolean ivar = new AtomicBoolean();

  public boolean close() {
    return set(false);
  }

  public boolean open() {
    return set(true);
  }

  public void waitFor() {
    if(!opened()) {
      throw new RuntimeException("Halted at barrier");
    }
  }

  // private

  public boolean opened() {
    return ivar.get();
  }

  public boolean set(boolean value) {
    ivar.set(value);
    return true;
  }

}
