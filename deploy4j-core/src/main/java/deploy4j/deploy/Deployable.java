package deploy4j.deploy;

public record Deployable(String serviceName, String version, BuildFiles buildFiles) {
}
