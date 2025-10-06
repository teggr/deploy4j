package dev.deploy4j.configuration;

public record SshConfig(
  String userName,
  int port,
  String privateKey,
  String privateKeyPassphrase,
  boolean strictHostChecking
) {
}
