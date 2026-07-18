package dev.deploy4j.it;

import dev.deploy4j.init.Initializer;

public final class InitializerRunner {

  private InitializerRunner() {
  }

  public static void main(String[] args) {
    new Initializer().init(false);
  }
}
