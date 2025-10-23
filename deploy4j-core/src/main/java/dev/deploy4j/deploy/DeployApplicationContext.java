package dev.deploy4j.deploy;

import dev.deploy4j.deploy.host.commands.*;
import dev.deploy4j.deploy.host.ssh.SshHosts;
import dev.deploy4j.init.Initializer;

import java.util.Map;

public class DeployApplicationContext {

  private final Environment environment;
  private final Deploy deploy;
  private final Server server;
  private final Registry registry;
  private final Build build;
  private final Traefik traefik;
  private final App app;
  private final Prune prune;
  private final Accessory accessory;
  private final Lock lock;
  private final Env env;
  private final DeployContext deployContext;
  private final LockManager lockManager;
  private final Initializer initializer;
  private final Audit audit;
  private final Version version;

  public DeployApplicationContext(Environment environment, SshHosts sshHosts, DeployContext deployContext) {

    this.environment = environment;
    this.deployContext = deployContext;

    BuilderHostCommands builder = new BuilderHostCommands(deployContext.config());

    DockerHostCommands docker = new DockerHostCommands(deployContext.config());

    HealthcheckHostCommands healthcheck = new HealthcheckHostCommands(deployContext.config());

    HookHostCommands hook = new HookHostCommands(deployContext.config());

    LockHostCommands lock = new LockHostCommands(deployContext.config());

    PruneHostCommands prune = new PruneHostCommands(deployContext.config());

    RegistryHostCommands registry = new RegistryHostCommands(deployContext.config());

    ServerHostCommands server = new ServerHostCommands(deployContext.config());

    TraefikHostCommands traefik = new TraefikHostCommands(deployContext.config());

    AuditorHostCommands audit = new AuditorHostCommands(deployContext.config(), Map.of());

    AppHostCommandsFactory apps = new AppHostCommandsFactory(deployContext.config());
    AccessoryHostCommandsFactory accessories = new AccessoryHostCommandsFactory(deployContext.config());

    this.lockManager = new LockManager(sshHosts, lock, server, deployContext.config().version());

    this.app = new App(sshHosts, lockManager, audit, apps);
    this.server = new Server(sshHosts, lockManager, docker, server, audit);
    this.env = new Env(sshHosts, lockManager, traefik, this.environment, audit, apps, accessories);
    this.accessory = new Accessory(sshHosts, lockManager, registry, audit, accessories);
    this.registry = new Registry(sshHosts, registry);
    this.build = new Build(sshHosts, builder, audit);
    this.prune = new Prune(sshHosts, lockManager, prune, audit);
    this.traefik = new Traefik(sshHosts, lockManager, registry, traefik, audit);
    this.lock = new Lock(sshHosts, lockManager, server, lock);

    this.initializer = new Initializer();
    this.audit = new Audit(sshHosts, audit);
    this.version = new Version();
    this.deploy = new Deploy(sshHosts, lockManager, this.app, this.server, this.env, this.accessory, this.registry, build, this.prune, this.traefik, apps);

  }

  public Env env() {
    return env;
  }

  public Environment environment() {
    return environment;
  }

  public Server server() {
    return server;
  }

  public Registry registry() {
    return registry;
  }

  public Build build() {
    return build;
  }

  public Deploy deploy() {
    return deploy;
  }

  public Traefik traefik() {
    return traefik;
  }

  public App app() {
    return app;
  }

  public Prune prune() {
    return prune;
  }

  public Accessory accessory() {
    return accessory;
  }

  public Lock lock() {
    return lock;
  }

  public DeployContext commander() {
    return deployContext;
  }

  public LockManager lockManager() {
    return lockManager;
  }

  public Initializer initializer() {
    return initializer;
  }

  public Audit audit() {
    return audit;
  }

  public Version version() {
    return version;
  }

}
