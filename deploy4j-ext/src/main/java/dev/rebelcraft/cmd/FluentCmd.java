package dev.rebelcraft.cmd;

import java.util.List;

public class FluentCmd<T extends FluentCmd<T>> extends Cmd {

  protected FluentCmd(String... base) {
    super(base);
  }

  protected FluentCmd(List<String> base) {
    super(base);
  }

  protected FluentCmd(Cmd cmd) {
    super(cmd.build());
  }

  @SuppressWarnings("unchecked")
  protected T self() {
    return (T) this;
  }

  @Override
  public T args(String... args) {
    super.args(args);
    return self();
  }

  @Override
  public T args(List<String> args) {
    super.args(args);
    return self();
  }

  @Override
  public T description(String description) {
    super.description(description);
    return self();
  }

}
