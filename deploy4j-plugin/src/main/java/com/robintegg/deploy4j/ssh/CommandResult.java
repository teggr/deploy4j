package com.robintegg.deploy4j.ssh;

public record CommandResult(String stdout, String stderr, int status) {
}
