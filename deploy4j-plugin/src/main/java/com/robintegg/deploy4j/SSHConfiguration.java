package com.robintegg.deploy4j;

import lombok.Getter;

@Getter
public class SSHConfiguration {

  private String username;
  private String privateKey;
  private String passPhrase;
  private String knownHosts;

  public SSHConfiguration() {

    this.username = System.getProperty("server.ssh.username");
    this.privateKey = System.getProperty("server.ssh.privateKey");
    this.passPhrase = System.getProperty("server.ssh.passPhrase");
    this.knownHosts = System.getProperty("server.ssh.knownHosts");

  }

  public int getPort() {
    return 22;
  }
}
