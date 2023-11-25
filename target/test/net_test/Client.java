package test.net_test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;

public class Client extends Thread{
  Socket clientSocket;
  Socket socketTCP;
  DatagramSocket socketUDP;

  private static final int SERVER_TCP_PORT = 5555;
  private static final int SERER_UDP_PORT = 12345;

  private static final int UDP_LISTENING_PORT = 5268;
  private static final int BUFFER_SIZE = 1024;

  public static void main(String[] args) {
    new Client().start();
  }
  @Override
  public void run() {
    try {
      socketTCP = new Socket(InetAddress.getLocalHost(), SERVER_TCP_PORT);
      socketUDP = new DatagramSocket();

      DatagramSocket clientSocket = new DatagramSocket(UDP_LISTENING_PORT);
      System.out.println("Client is listening on port " + UDP_LISTENING_PORT);

      byte[] receiveData = new byte[BUFFER_SIZE];
      DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);


      while (true) {
        //TCP
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String message = reader.readLine();
        OutputStream out = socketTCP.getOutputStream();
        out.write((message +  '\n').getBytes());
        out.flush();
        //UDP
        BufferedReader readerUDP = new BufferedReader(new InputStreamReader(System.in));
        System.out.print("UDP:");
        /*String message = reader.readLine();
        OutputStream out = socketUDP.
        out.write((message +  '\n').getBytes());
        out.flush();*/
      }
    } catch (IOException e) {
      try {
        socketTCP.close();
        socketUDP.close();;
      } catch (IOException ex) {
        throw new RuntimeException(ex);
      }
    }
  }
}
