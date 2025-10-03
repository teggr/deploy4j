package dev.deploy4j.configuration;

public record SshConfig(
  int port,
  String privateKey,
  String privateKeyPassphrase,
  boolean strictHostChecking
) {
}
