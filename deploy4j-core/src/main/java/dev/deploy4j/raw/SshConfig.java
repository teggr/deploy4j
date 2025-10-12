package dev.deploy4j.raw;

public record SshConfig(
  String user,
  String port,
  String privateKey,
  String privateKeyPassphrase,
  boolean strictHostChecking
) {
}
