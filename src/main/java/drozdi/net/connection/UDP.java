package drozdi.net.connection;

import drozdi.console.ConsoleColors;
import drozdi.console.ConsoleLogger;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class UDP {
  public static final ConsoleLogger log = new ConsoleLogger(ConsoleColors.BLUE);

  public static void sendData(DatagramSocket clientSocket, InetAddress serverAddress, int serverPort, byte[] bytes) {
    try {
      DatagramPacket sendPacket = new DatagramPacket(bytes, bytes.length, serverAddress, serverPort);
      clientSocket.send(sendPacket);
    } catch (Exception e) {
      log.error("UDP send packet errored" + e.getMessage());
      //System.exit(485);
      //clientSocket.close();
    }
  }
}
