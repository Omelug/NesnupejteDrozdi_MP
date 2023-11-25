package drozdi.net.connection;

import drozdi.console.ConsoleColors;
import drozdi.console.ConsoleLogger;
import drozdi.net.NetConst;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Arrays;

import static drozdi.net.NetConst.tcpPackerSeparator;


public class TCP {
  public static final ConsoleLogger log = new ConsoleLogger(ConsoleColors.CYAN);

  public static void sendData(Socket socket, byte[] data){
    try {
      if (socket == null) {
        log.error("Socket is null");
        return;
      }

      OutputStream out = socket.getOutputStream();

      //log.debug("Sending data " + Arrays.toString(data));

      out.write(data);
      out.write(tcpPackerSeparator);


    } catch (IOException e) {
      log.error("error sendDataTCP " + e.getMessage());
      e.printStackTrace();
    }
  }
}
