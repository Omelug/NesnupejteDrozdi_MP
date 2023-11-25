package drozdi.net.connection;

import drozdi.console.ConsoleLogger;
import drozdi.net.NetConst;
import drozdi.net.Packet;

import javax.sound.midi.Receiver;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public interface UDPReceiver {
  void processUDPPacket(Packet packet, int port);
  boolean getRunningUDP();
  void setRunningUDP(boolean running);
  default void receiveUDP(DatagramSocket socketUDP, ConsoleLogger log){
    while (getRunningUDP()){
      byte[] receiveData = new byte[NetConst.getPacketSizeWithID()];
      DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);

      try {
        socketUDP.receive(receivePacket);
        Packet packet = new Packet(receivePacket.getData());
        processUDPPacket(packet, receivePacket.getPort());
      } catch (IOException e) {
        log.error("Receiving UDP " + e.getMessage());
      }
    }
  }
}
