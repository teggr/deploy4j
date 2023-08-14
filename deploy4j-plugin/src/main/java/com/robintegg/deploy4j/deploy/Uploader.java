package com.robintegg.deploy4j.deploy;

import com.robintegg.deploy4j.ssh.SshConnection;

import java.nio.file.Path;
import java.util.List;

public class Uploader {
  public static UploadedFiles uploadFiles(SshConnection sshConnection, List<Path> files) {

    files.forEach(p ->
      sshConnection.pushFile(  );
    );

    return null;
  }
}
