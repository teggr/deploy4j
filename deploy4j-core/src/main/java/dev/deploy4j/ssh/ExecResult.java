package dev.deploy4j.ssh;

public record ExecResult(int exitStatus, String execOutput, String execErrorOutput) {
}
