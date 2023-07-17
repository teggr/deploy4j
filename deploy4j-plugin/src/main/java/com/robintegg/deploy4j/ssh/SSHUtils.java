package com.robintegg.deploy4j.ssh;

import java.io.IOException;
import java.io.InputStream;

class SSHUtils {

  public static int checkAck(InputStream in) throws IOException {
    int b = in.read();
    // b may be 0 for success,
    //          1 for error,
    //          2 for fatal error,
    //         -1
    if (b == 0) return b;
    if (b == -1) return b;

    if (b == 1 || b == 2) {
      StringBuffer sb = new StringBuffer();
      int c;
      do {
        c = in.read();
        sb.append((char) c);
      }
      while (c != '\n');
      if (b == 1) { // error
        throw new RuntimeException(sb.toString());
      }
      if (b == 2) { // fatal error
        throw new RuntimeException(sb.toString());
      }
    }
    return b;
  }
}
