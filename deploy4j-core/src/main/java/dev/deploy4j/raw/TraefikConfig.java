package dev.deploy4j.raw;

import java.util.Map;

public record TraefikConfig(
    String image,
    String hostPort,
    boolean publish,
    Map<String, String> labels,
    Map<String, String> args,
    Map<String, String> options,
    Map<String, String> env
) {
}
