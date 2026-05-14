package dev.deploy4j.deploy;

import dev.deploy4j.deploy.configuration.Configuration;
import dev.deploy4j.deploy.configuration.Role;
import dev.deploy4j.deploy.host.commands.AppHostCommands;
import dev.deploy4j.deploy.host.commands.AppHostCommandsFactory;
import dev.deploy4j.deploy.host.ssh.SshHost;
import dev.deploy4j.deploy.host.ssh.SshHosts;
import dev.deploy4j.deploy.local.LocalHost;
import dev.rebelcraft.cmd.Cmd;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.List;
import java.util.function.Consumer;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class DeployTest {

  @Mock
  SshHosts sshHosts;

  @Mock
  LockManager lockManager;

  @Mock
  App app;

  @Mock
  Server server;

  @Mock
  Accessory accessory;

  @Mock
  Registry registry;

  @Mock
  Build build;

  @Mock
  Prune prune;

  @Mock
  Gateway gateway;

  @Mock
  AppHostCommandsFactory apps;

  @Mock
  DeployContext deployContext;

  @Mock
  SshHost sshHost;

  @Mock
  AppHostCommands appHostCommands;

  @Mock
  Hooks hooks;

  @Mock
  private LocalHost localHost;

  Deploy deploy;

  @BeforeEach
  void setUp() {
    // make withLock run the runnable immediately
    doAnswer(invocation -> {
      Runnable r = invocation.getArgument(1);
      r.run();
      return null;
    }).when(lockManager).withLock(any(DeployContext.class), any(Runnable.class));

    // make sshHosts.on execute the consumer for each host name, passing a mocked SshHost
    doAnswer(invocation -> {
      @SuppressWarnings("unchecked")
      List<String> hosts = invocation.getArgument(0);
      @SuppressWarnings("unchecked")
      Consumer<SshHost> consumer = invocation.getArgument(1);
      for (String h : hosts) {
        when(sshHost.hostName()).thenReturn(h);
        consumer.accept(sshHost);
      }
      return null;
    }).when(sshHosts).on(anyList(), any());

    // server.test() returns a simple Cmd to avoid null invocation in tests
    when(server.test()).thenReturn(Cmd.cmd("pwd"));

    deploy = new Deploy(sshHosts, hooks, localHost, lockManager, app, server, accessory, registry, build, prune, gateway, apps);
  }

  @Test
  void setup_runs_bootstrap_and_deploys() {
    when(deployContext.hosts()).thenReturn(List.of("host1"));

    // run
    deploy.setup(deployContext);

    // verify bootstrap and env interactions and accessory boot
    verify(server).bootstrap(deployContext);
    verify(accessory).boot(deployContext, "all", true);

    // deploy flow should also have invoked registry login and build.pull and gateway.boot and prune
    verify(build).pull(deployContext);
    verify(gateway).boot(deployContext);
    verify(app).staleContainers(deployContext);
    verify(app).boot(deployContext);
    verify(prune).all(deployContext);
  }

  @Test
  void redeploy_skips_pull_when_requested_and_boots_app() {
    when(deployContext.hosts()).thenReturn(List.of("host1"));

    deploy.redeploy(deployContext, true);

    // skipPull true => build.pull not invoked
    verify(build, never()).pull(any());
    // app interactions happen
    verify(app).staleContainers(deployContext);
    verify(app).boot(deployContext);
  }

  @Test
  void redeploy_pulls_when_not_skipped_and_boots_app() {
    when(deployContext.hosts()).thenReturn(List.of("host1"));

    deploy.redeploy(deployContext, false);

    verify(build).pull(deployContext);
    verify(app).staleContainers(deployContext);
    verify(app).boot(deployContext);
  }

  @Test
  void rollback_does_not_boot_if_container_unavailable() {
    String version = "v1";

    when(deployContext.appHosts()).thenReturn(List.of("host1"));
    // rolesOn will return a single role for the host
    Role role = mock(Role.class);
    when(deployContext.rolesOn("host1")).thenReturn(List.of(role));

    // provide a mock Configuration so config().version(...) won't NPE
    Configuration configuration = mock(Configuration.class);
    when(deployContext.config()).thenReturn(configuration);

    // the apps factory returns a mock AppHostCommands
    when(apps.app(any(), anyString())).thenReturn(appHostCommands);

    // sshHost.capture should return null to trigger "Container not found"
    // disambiguate overloaded capture methods
    when(sshHost.capture(any(java.lang.String.class))).thenReturn(null);
    when(sshHost.capture(any(dev.rebelcraft.cmd.Cmd.class))).thenReturn(null);

    // run rollback
    deploy.rollback(deployContext, version);

    // since container not available, app.boot should not be called
    verify(app, never()).boot(any());
  }

  @Test
  void details_calls_gateway_app_and_accessory_details() {
    deploy.details(deployContext);

    verify(gateway).details(deployContext);
    verify(app).details(deployContext);
    verify(accessory).details(deployContext, "all", false);
  }

  @Test
  void test_connectivity_executes_server_test_on_hosts() {
    when(deployContext.hosts()).thenReturn(List.of("host1"));

    // sshHost.execute should return true
    when(sshHost.execute(any(dev.rebelcraft.cmd.Cmd.class))).thenReturn(true);

    deploy.test(deployContext);

    // verify that the server.test() command was executed on the host
    verify(sshHost).execute(server.test());
  }

}