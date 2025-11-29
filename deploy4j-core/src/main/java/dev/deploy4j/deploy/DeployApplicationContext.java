package dev.deploy4j.deploy;

import dev.deploy4j.deploy.host.commands.*;
import dev.deploy4j.deploy.host.ssh.SshHosts;
import dev.deploy4j.deploy.local.LocalHost;

import java.util.Map;

public class DeployApplicationContext {

  private final Deploy deploy;
  private final Server server;
  private final Registry registry;
  private final Build build;
  private final Traefik traefik;
  private final App app;
  private final Prune prune;
  private final Accessory accessory;
  private final Lock lock;
  private final DeployContext deployContext;
  private final LockManager lockManager;
  private final Audit audit;
  private final SpringBootManage springBootManage;

  public DeployApplicationContext(SshHosts sshHosts, Hooks hooks, LocalHost localHost, DeployContext deployContext) {

    this.deployContext = deployContext;

    BuilderHostCommands builder = new BuilderHostCommands(deployContext.config());

    DockerHostCommands docker = new DockerHostCommands(deployContext.config());

    HealthcheckHostCommands healthcheck = new HealthcheckHostCommands(deployContext.config());

    LockHostCommands lock = new LockHostCommands(deployContext.config());

    PruneHostCommands prune = new PruneHostCommands(deployContext.config());

    RegistryHostCommands registry = new RegistryHostCommands(deployContext.config());

    ServerHostCommands server = new ServerHostCommands(deployContext.config());

    TraefikHostCommands traefik = new TraefikHostCommands(deployContext.config());

    AuditorHostCommands audit = new AuditorHostCommands(deployContext.config(), Map.of());

    SpringBootHostCommands springBoot = new SpringBootHostCommands(deployContext.config());

    AppHostCommandsFactory apps = new AppHostCommandsFactory(deployContext.config());
    AccessoryHostCommandsFactory accessories = new AccessoryHostCommandsFactory(deployContext.config());

    this.lockManager = new LockManager(sshHosts, lock, server, deployContext.config().version());

    this.server = new Server(sshHosts, hooks, localHost, lockManager, docker, server, audit);
    this.app = new App(sshHosts, hooks, localHost, lockManager, audit, apps, server);
    this.accessory = new Accessory(sshHosts, hooks, localHost, lockManager, registry, audit, accessories, docker);
    this.registry = new Registry(sshHosts, hooks, localHost, registry);
    this.build = new Build(sshHosts, hooks, localHost, builder, audit);
    this.prune = new Prune(sshHosts, hooks, localHost, lockManager, prune, audit);
    this.traefik = new Traefik(sshHosts, hooks, localHost, lockManager, registry, traefik, audit, docker);
    this.lock = new Lock(sshHosts, hooks, localHost, lockManager, server, lock);

    this.audit = new Audit(sshHosts, hooks, localHost, audit);
    this.springBootManage = new SpringBootManage(sshHosts, hooks, localHost, springBoot);
    this.deploy = new Deploy(sshHosts, hooks, localHost, lockManager, this.app, this.server, this.accessory, this.registry, build, this.prune, this.traefik, apps);

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

  public DeployContext deployContext() {
    return deployContext;
  }

  public LockManager lockManager() {
    return lockManager;
  }

  public Audit audit() {
    return audit;
  }

  public SpringBootManage springBootManage() {
    return springBootManage;
  }

}
