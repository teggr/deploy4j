package deploy4j.ssh;

public record SshCommandResult(int status, String out, String err) {
}
