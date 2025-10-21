package dev.rebelcraft.ssh;

public record ExecResult(int exitStatus, String execOutput, String execErrorOutput) {
}
