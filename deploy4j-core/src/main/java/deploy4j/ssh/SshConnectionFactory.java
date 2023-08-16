package deploy4j.ssh;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class SshConnectionFactory {

  private final SshConfig sshConfig;
  private final String host;

  @SneakyThrows
  public SshConnection open() {

    log.info("opening connection: {}@{}", sshConfig.username(), host);

    JSch jSch = new JSch();
    jSch.addIdentity(sshConfig.privateKeyPath(), sshConfig.passPhrase());
    jSch.setKnownHosts(sshConfig.knownHostsPath());

    Session session = jSch.getSession(sshConfig.username(), host);

    session.connect();

    return new SshConnection(session);

  }

}
