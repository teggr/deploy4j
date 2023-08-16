package deploy4j.deploy;

import deploy4j.ssh.SshCommandResult;
import deploy4j.ssh.SshConnection;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class Uploader {

  public static String uploadFiles(SshConnection sshConnection, Deployable deployable) {

    String uploadDirectory = "/root/deploy4j/" + deployable.serviceName() + "/" + deployable.version();

    SshCommandResult mkdirResult = sshConnection.executeCommand("mkdir -p " + uploadDirectory);

    if (mkdirResult.status() != 0) {
      log.info("could not create upload directory: {}", mkdirResult.err());
      throw new RuntimeException("could not create upload directory");
    } else {
      log.info("upload directory created: {}", mkdirResult.out());
    }

    List<Path> files = new ArrayList<>();
    files.add(deployable.buildFiles().dockerFilePath());
    files.addAll(deployable.buildFiles().buildContext());

    files.forEach(p -> {

      boolean isRoot = p.getParent() == null;

      String targetDirectory = uploadDirectory;

      if (!isRoot) {

        targetDirectory = uploadDirectory + "/" + remotePath(p.getParent());

        SshCommandResult mkParentDirResult = sshConnection.executeCommand("mkdir -p " + targetDirectory);

        if (mkParentDirResult.status() != 0) {
          log.info("could not create parent directory: {}", mkdirResult.err());
          throw new RuntimeException("could not create parent directory");
        } else {
          log.info("parent directory created: {}", mkdirResult.out());
        }

      }

      String from = deployable.buildFiles().workingDirectory().resolve(p).getParent().toAbsolutePath().toString();
      String to = targetDirectory;
      String fileName = p.getFileName().toString();

      sshConnection.pushFile(from, to, fileName);

    });

    return uploadDirectory;
  }

  private static String remotePath(Path p) {
    return p.toString().replaceAll("\\\\", "/");
  }

}
