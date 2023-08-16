package deploy4j.ssh;

public record SshConfig(String username, String privateKeyPath, String passPhrase, String knownHostsPath) {
}
