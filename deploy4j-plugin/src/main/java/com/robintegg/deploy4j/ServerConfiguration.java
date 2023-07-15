package com.robintegg.deploy4j;

import lombok.Getter;

@Getter
public class ServerConfiguration {

  private String host;

  public ServerConfiguration() {

    this.host = System.getProperty("server.host");

  }

}
