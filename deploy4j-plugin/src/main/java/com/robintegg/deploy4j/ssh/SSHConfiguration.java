package com.robintegg.deploy4j.ssh;

import lombok.Getter;

@Getter
public class SSHConfiguration {

  private String username;
  private String privateKey;
  private String passPhrase;
  private String knownHosts;
  private boolean logging;

  public SSHConfiguration() {

    this.username = System.getProperty("server.ssh.username");
    this.privateKey = System.getProperty("server.ssh.privateKey");
    this.passPhrase = System.getProperty("server.ssh.passPhrase");
    this.knownHosts = System.getProperty("server.ssh.knownHosts");
    this.logging = Boolean.valueOf(System.getProperty("server.ssh.logging", "false"));

  }

  public int getPort() {
    return 22;
  }
}
