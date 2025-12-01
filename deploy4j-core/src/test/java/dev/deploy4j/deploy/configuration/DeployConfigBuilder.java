package dev.deploy4j.deploy.configuration;

import dev.deploy4j.deploy.configuration.raw.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Test builders to create DeployConfig and related raw config objects in tests.
 * Usage examples:
 *   DeployConfig cfg = DeployConfigBuilder.minimal().service("svc").build();
 *   DeployConfig cfg = new DeployConfigBuilder().service("svc").image("img").build();
 */
public class DeployConfigBuilder {

    // Fields mirror DeployConfig constructor
    private String service;
    private String image;
    private Map<String, String> labels;
    private List<String> volumes;
    private RegistryConfig registry;
    private ServersConfig servers;
    private EnvironmentConfig env;
    private String hooksPath;
    private Boolean requireDestination;
    private String primaryRole;
    private Boolean allowEmptyRoles;
    private Integer stopWaitTime;
    private Integer retainContainers;
    private String minimumVersion;
    private Integer readinessDelay;
    private String runDirectory;
    private SshConfig ssh;
    private Map<String, AccessoryConfig> accessories;
    private TraefikConfig traefik;
    private BootConfig boot;
    private HealthCheckConfig healthCheck;
    private LoggingConfig logging;
    private SpringBootConfig springBoot;

    public DeployConfigBuilder() {
        // defaults
    }

    public static DeployConfigBuilder minimal() {
        DeployConfigBuilder b = new DeployConfigBuilder();
        b.service = "test-service";
        b.image = "test-image";
        b.registry = new RegistryConfig(null, null, null);
        b.servers = new ServersConfig(List.of("host1"));
        b.env = new EnvironmentConfig(Map.of("K","V"), null, null, null);
        return b;
    }

    public DeployConfigBuilder service(String service) { this.service = service; return this; }
    public DeployConfigBuilder image(String image) { this.image = image; return this; }
    public DeployConfigBuilder labels(Map<String, String> labels) { this.labels = labels; return this; }
    public DeployConfigBuilder volumes(List<String> volumes) { this.volumes = volumes; return this; }
    public DeployConfigBuilder registry(RegistryConfig registry) { this.registry = registry; return this; }
    public DeployConfigBuilder servers(ServersConfig servers) { this.servers = servers; return this; }
    public DeployConfigBuilder env(EnvironmentConfig env) { this.env = env; return this; }
    public DeployConfigBuilder hooksPath(String hooksPath) { this.hooksPath = hooksPath; return this; }
    public DeployConfigBuilder requireDestination(Boolean requireDestination) { this.requireDestination = requireDestination; return this; }
    public DeployConfigBuilder primaryRole(String primaryRole) { this.primaryRole = primaryRole; return this; }
    public DeployConfigBuilder allowEmptyRoles(Boolean allowEmptyRoles) { this.allowEmptyRoles = allowEmptyRoles; return this; }
    public DeployConfigBuilder stopWaitTime(Integer t) { this.stopWaitTime = t; return this; }
    public DeployConfigBuilder retainContainers(Integer r) { this.retainContainers = r; return this; }
    public DeployConfigBuilder minimumVersion(String v) { this.minimumVersion = v; return this; }
    public DeployConfigBuilder readinessDelay(Integer d) { this.readinessDelay = d; return this; }
    public DeployConfigBuilder runDirectory(String runDirectory) { this.runDirectory = runDirectory; return this; }
    public DeployConfigBuilder ssh(SshConfig ssh) { this.ssh = ssh; return this; }
    public DeployConfigBuilder accessories(Map<String, AccessoryConfig> accessories) { this.accessories = accessories; return this; }
    public DeployConfigBuilder traefik(TraefikConfig traefik) { this.traefik = traefik; return this; }
    public DeployConfigBuilder boot(BootConfig boot) { this.boot = boot; return this; }
    public DeployConfigBuilder healthCheck(HealthCheckConfig healthCheck) { this.healthCheck = healthCheck; return this; }
    public DeployConfigBuilder logging(LoggingConfig logging) { this.logging = logging; return this; }
    public DeployConfigBuilder springBoot(SpringBootConfig springBoot) { this.springBoot = springBoot; return this; }

    public DeployConfig build() {
        return new DeployConfig(
            service,
            image,
            labels,
            volumes,
            registry,
            servers,
            env,
            hooksPath,
            requireDestination,
            primaryRole,
            allowEmptyRoles,
            stopWaitTime,
            retainContainers,
            minimumVersion,
            readinessDelay,
            runDirectory,
            ssh,
            accessories,
            traefik,
            boot,
            healthCheck,
            logging,
            springBoot
        );
    }

    // --- Helper builders for nested types ---

    public static class RegistryBuilder {
        private String server;
        private PlainValueOrSecretKey username;
        private PlainValueOrSecretKey password;

        public RegistryBuilder server(String s) { this.server = s; return this; }
        public RegistryBuilder usernamePlain(String u) { this.username = new PlainValueOrSecretKey(u); return this; }
        public RegistryBuilder usernameKey(String key) { this.username = new PlainValueOrSecretKey(List.of(key)); return this; }
        public RegistryBuilder passwordPlain(String p) { this.password = new PlainValueOrSecretKey(p); return this; }
        public RegistryBuilder passwordKey(String key) { this.password = new PlainValueOrSecretKey(List.of(key)); return this; }
        public RegistryConfig build() { return new RegistryConfig(server, username, password); }
    }

    public static class ServersBuilder {
        private final List<String> hosts = new ArrayList<>();
        private final Map<String, RoleConfig> roles = new HashMap<>();
        private boolean useMap = false;

        public ServersBuilder addHost(String host) { hosts.add(host); return this; }
        public ServersBuilder addHosts(List<String> hostList) { hosts.addAll(hostList); return this; }
        public ServersBuilder addRole(String name, RoleConfig roleConfig) { roles.put(name, roleConfig); useMap = true; return this; }
        public ServersConfig build() {
            if (useMap) return new ServersConfig(roles);
            return new ServersConfig(hosts);
        }
    }

    public static class EnvBuilder {
        private Map<String, String> map;
        private Map<String, String> clear;
        private List<String> secrets;
        private Map<String, EnvironmentConfig> tags;

        public EnvBuilder map(Map<String, String> map) { this.map = map; return this; }
        public EnvBuilder clear(Map<String, String> clear) { this.clear = clear; return this; }
        public EnvBuilder secrets(List<String> secrets) { this.secrets = secrets; return this; }
        public EnvBuilder tags(Map<String, EnvironmentConfig> tags) { this.tags = tags; return this; }
        public EnvironmentConfig buildAsMap() { return new EnvironmentConfig(null, null, null, map); }
        public EnvironmentConfig buildAsClear() { return new EnvironmentConfig(clear, secrets, tags, null); }
    }

    public static class RoleConfigBuilder {
        private final List<String> hosts = new ArrayList<>();
        private CustomRoleConfig custom;

        public RoleConfigBuilder addHost(String host) { hosts.add(host); return this; }
        public RoleConfigBuilder custom(CustomRoleConfig c) { this.custom = c; return this; }
        public RoleConfig build() {
            if (custom != null) return new RoleConfig(custom);
            return new RoleConfig(hosts);
        }
    }

    public static class CustomRoleBuilder {
        private final List<String> hosts = new ArrayList<>();
        private Boolean traefik;
        private String cmd;
        private EnvironmentConfig env;
        private LoggingConfig logging;
        private HealthCheckConfig healthcheck;
        private Map<String, String> options;
        private Map<String, String> labels;

        public CustomRoleBuilder addHost(String host) { hosts.add(host); return this; }
        public CustomRoleBuilder traefik(Boolean t) { this.traefik = t; return this; }
        public CustomRoleBuilder cmd(String c) { this.cmd = c; return this; }
        public CustomRoleBuilder env(EnvironmentConfig e) { this.env = e; return this; }
        public CustomRoleBuilder logging(LoggingConfig l) { this.logging = l; return this; }
        public CustomRoleBuilder healthcheck(HealthCheckConfig h) { this.healthcheck = h; return this; }
        public CustomRoleBuilder options(Map<String, String> o) { this.options = o; return this; }
        public CustomRoleBuilder labels(Map<String, String> l) { this.labels = l; return this; }
        public CustomRoleConfig build() { return new CustomRoleConfig(new ArrayList<>(hosts), traefik, cmd, env, logging, healthcheck, options, labels); }
    }

    public static class HealthCheckBuilder {
        private String cmd;
        private String interval;
        private Integer maxAttempts;
        private Integer port;
        private String path;
        private String cord;
        private Integer logLines;

        public HealthCheckBuilder cmd(String c) { this.cmd = c; return this; }
        public HealthCheckBuilder interval(String i) { this.interval = i; return this; }
        public HealthCheckBuilder maxAttempts(Integer m) { this.maxAttempts = m; return this; }
        public HealthCheckBuilder port(Integer p) { this.port = p; return this; }
        public HealthCheckBuilder path(String p) { this.path = p; return this; }
        public HealthCheckBuilder cord(String c) { this.cord = c; return this; }
        public HealthCheckBuilder logLines(Integer l) { this.logLines = l; return this; }
        public HealthCheckConfig build() { return new HealthCheckConfig(cmd, interval, maxAttempts, port, path, cord, logLines); }
    }

    // --- Accessory builder for tests ---
    public static AccessoryConfigBuilder accessory() { return new AccessoryConfigBuilder(); }

    public static class AccessoryConfigBuilder {
        private String service;
        private String image;
        private String host;
        private List<String> hosts;
        private List<String> roles;
        private String cmd;
        private String port;
        private Map<String, String> labels;
        private Map<String, String> options;
        private EnvironmentConfig env;
        private List<String> files;
        private List<String> directories;
        private List<String> volumes;
        private String network;

        public AccessoryConfigBuilder service(String s) { this.service = s; return this; }
        public AccessoryConfigBuilder image(String i) { this.image = i; return this; }
        public AccessoryConfigBuilder host(String h) { this.host = h; return this; }
        public AccessoryConfigBuilder hosts(List<String> hs) { this.hosts = hs; return this; }
        public AccessoryConfigBuilder addHost(String h) { if (this.hosts == null) this.hosts = new ArrayList<>(); this.hosts.add(h); return this; }
        public AccessoryConfigBuilder roles(List<String> r) { this.roles = r; return this; }
        public AccessoryConfigBuilder cmd(String c) { this.cmd = c; return this; }
        public AccessoryConfigBuilder port(String p) { this.port = p; return this; }
        public AccessoryConfigBuilder labels(Map<String, String> l) { this.labels = l; return this; }
        public AccessoryConfigBuilder options(Map<String, String> o) { this.options = o; return this; }
        public AccessoryConfigBuilder env(EnvironmentConfig e) { this.env = e; return this; }
        public AccessoryConfigBuilder files(List<String> f) { this.files = f; return this; }
        public AccessoryConfigBuilder addFile(String f) { if (this.files == null) this.files = new ArrayList<>(); this.files.add(f); return this; }
        public AccessoryConfigBuilder directories(List<String> d) { this.directories = d; return this; }
        public AccessoryConfigBuilder addDirectory(String d) { if (this.directories == null) this.directories = new ArrayList<>(); this.directories.add(d); return this; }
        public AccessoryConfigBuilder volumes(List<String> v) { this.volumes = v; return this; }
        public AccessoryConfigBuilder addVolume(String v) { if (this.volumes == null) this.volumes = new ArrayList<>(); this.volumes.add(v); return this; }
        public AccessoryConfigBuilder network(String n) { this.network = n; return this; }

        public AccessoryConfig build() {
            return new AccessoryConfig(service, image, host, hosts, roles, cmd, port, labels, options, env, files, directories, volumes, network);
        }
    }

}
