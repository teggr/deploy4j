package com.robintegg.deploy4j.cli;

public class CUrl {

  public static Http http() {
    return new Http();
  }

  public static class Http extends AbstractCliCommand {

    public Http() {
      super("curl");
    }

    public Http args(String args) {
      append("-" + args);
      return this;
    }


    public Http url(String url) {
      append(url);
      return this;
    }

  }

}
