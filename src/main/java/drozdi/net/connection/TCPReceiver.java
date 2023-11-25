package drozdi.net.connection;

import drozdi.console.ConsoleLogger;
import drozdi.net.NetConst;
import drozdi.net.Packet;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;

public interface TCPReceiver {
  void processPacket(Packet packet);
  boolean getRunningTCP();
  void setRunningTCP(boolean running);

  default void receiveTCP(Socket tcpClientSocket, ConsoleLogger log){
    log.info("receiveTCP started");

    InputStream inputStream;
    while (getRunningTCP()){
      try {
        inputStream = tcpClientSocket.getInputStream();
        int i = 0;
        int b;
        byte[] maxData = new byte[NetConst.getPacketSizeWithID()];

        while ((b = inputStream.read()) != -1) {
          //System.out.print((char) b);
          if(b != NetConst.tcpPackerSeparator && i != NetConst.getPacketSizeWithID()) {
            maxData[i] = (byte) b;
            i++;
            continue;
          }
          byte[] receivedData = Arrays.copyOf(maxData, i);

          //log.debug("Packet received:" + Arrays.toString(receivedData));
          Packet packet = new Packet(receivedData);
          log.debug("<--" + packet.getFirstId() + " " + packet.getSecondId());
          processPacket(packet);
          i = 0;
        }
      } catch (IOException e) {
        setRunningTCP(false);
        log.info("receiveTCP stopped " + e);
        e.printStackTrace();
      }
    }
    try {
      tcpClientSocket.close();
    } catch (IOException e) {
      log.error(String.valueOf(e));
    }
  }

}
