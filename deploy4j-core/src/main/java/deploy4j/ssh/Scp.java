package deploy4j.ssh;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.Session;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.*;

@Slf4j
public class Scp {

  @SneakyThrows
  public static void copyLocalToRemote(Session session, String from, String to, String fileName) {

    Channel channel = null;

    try {
      boolean ptimestamp = true;
      from = from + File.separator + fileName;

      // exec 'scp -t rfile' remotely
      String command = "scp " + (ptimestamp ? "-p" : "") + " -t " + to;
      channel = session.openChannel("exec");
      ((ChannelExec) channel).setCommand(command);

      // get I/O streams for remote scp
      OutputStream out = channel.getOutputStream();
      InputStream in = channel.getInputStream();

      channel.connect();

      if (checkAck(in) != 0) {
        throw new RuntimeException("failed post scp command checkAck()");
      }

      File _lfile = new File(from);

      if (ptimestamp) {
        command = "T" + (_lfile.lastModified() / 1000) + " 0";
        // The access time should be sent here,
        // but it is not accessible with JavaAPI ;-<
        command += (" " + (_lfile.lastModified() / 1000) + " 0\n");
        out.write(command.getBytes());
        out.flush();
        if (checkAck(in) != 0) {
          throw new RuntimeException("failed post timestamp command checkAck()");
        }
      }

      // send "C0644 filesize filename", where filename should not include '/'
      long filesize = _lfile.length();
      command = "C0644 " + filesize + " " + fileName;
//      if (from.lastIndexOf('/') > 0) {
//        command += from.substring(from.lastIndexOf('/') + 1);
//      } else {
//        command += from;
//      }

      command += "\n";
      out.write(command.getBytes());
      out.flush();

      if (checkAck(in) != 0) {
        throw new RuntimeException("failed post CO644 command checkAck()");
      }

      // send a content of lfile
      FileInputStream fis = new FileInputStream(from);
      byte[] buf = new byte[1024];
      while (true) {
        int len = fis.read(buf, 0, buf.length);
        if (len <= 0) break;
        out.write(buf, 0, len); //out.flush();
      }

      // send '\0'
      buf[0] = 0;
      out.write(buf, 0, 1);
      out.flush();

      if (checkAck(in) != 0) {
        throw new RuntimeException("failed write scp command checkAck()");
      }
      out.close();

      try {
        if (fis != null) fis.close();
      } catch (Exception ex) {
        throw new RuntimeException("failed to close file input stream");
      }

    } finally {
      if (channel != null) {
        channel.disconnect();

      }
    }

  }

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
        System.out.print(sb.toString());
      }
      if (b == 2) { // fatal error
        System.out.print(sb.toString());
      }
    }
    return b;
  }

}
