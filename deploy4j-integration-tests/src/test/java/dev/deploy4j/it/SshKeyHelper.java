package dev.deploy4j.it;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermission;
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPublicKey;
import java.util.Base64;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.Set;

final class SshKeyHelper {

  private SshKeyHelper() {
  }

  static GeneratedKeyPair generate() throws IOException, GeneralSecurityException {
    KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
    generator.initialize(2048);
    KeyPair keyPair = generator.generateKeyPair();

    Path directory = Files.createTempDirectory("deploy4j-it-ssh");
    Path privateKeyPath = directory.resolve("id_rsa");
    Path authorizedKeysPath = directory.resolve("authorized_keys");

    Files.writeString(privateKeyPath, toPem(keyPair), StandardCharsets.US_ASCII);
    Files.writeString(authorizedKeysPath, toAuthorizedKey(keyPair) + System.lineSeparator(), StandardCharsets.US_ASCII);
    restrictPermissions(privateKeyPath);

    return new GeneratedKeyPair(directory, privateKeyPath, authorizedKeysPath);
  }

  private static String toPem(KeyPair keyPair) {
    String encoded = Base64.getMimeEncoder(64, new byte[]{'\n'})
      .encodeToString(keyPair.getPrivate().getEncoded());
    return """
      -----BEGIN PRIVATE KEY-----
      %s
      -----END PRIVATE KEY-----
      """.formatted(encoded);
  }

  private static String toAuthorizedKey(KeyPair keyPair) throws IOException {
    RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();

    ByteArrayOutputStream output = new ByteArrayOutputStream();
    writeSshBytes(output, "ssh-rsa".getBytes(StandardCharsets.US_ASCII));
    writeSshBytes(output, publicKey.getPublicExponent().toByteArray());
    writeSshBytes(output, publicKey.getModulus().toByteArray());

    return "ssh-rsa %s deploy4j-it".formatted(Base64.getEncoder().encodeToString(output.toByteArray()));
  }

  private static void writeSshBytes(ByteArrayOutputStream output, byte[] value) throws IOException {
    writeInt(output, value.length);
    output.write(value);
  }

  private static void writeInt(ByteArrayOutputStream output, int value) {
    output.write((value >>> 24) & 0xff);
    output.write((value >>> 16) & 0xff);
    output.write((value >>> 8) & 0xff);
    output.write(value & 0xff);
  }

  private static void restrictPermissions(Path privateKeyPath) throws IOException {
    try {
      Set<PosixFilePermission> permissions = EnumSet.of(
        PosixFilePermission.OWNER_READ,
        PosixFilePermission.OWNER_WRITE
      );
      Files.setPosixFilePermissions(privateKeyPath, permissions);
    } catch (UnsupportedOperationException ignored) {
      // Ignore on non-POSIX filesystems.
    }
  }

  record GeneratedKeyPair(Path directory, Path privateKeyPath, Path authorizedKeysPath) implements AutoCloseable {

    @Override
    public void close() throws IOException {
      if (!Files.exists(directory)) {
        return;
      }
      try (var stream = Files.walk(directory)) {
        stream.sorted(Comparator.reverseOrder()).forEach(path -> {
          try {
            Files.deleteIfExists(path);
          } catch (IOException e) {
            throw new RuntimeException(e);
          }
        });
      } catch (RuntimeException e) {
        if (e.getCause() instanceof IOException ioException) {
          throw ioException;
        }
        throw e;
      }
    }
  }
}
