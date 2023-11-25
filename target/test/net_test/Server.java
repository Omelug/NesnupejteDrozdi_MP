package test.net_test;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server extends Thread{
  static ServerSocket socketTCP;
  static DatagramSocket socketUDP;

  private static final int TCP_PORT = 5555;
  private static final int UDP_PORT = 12345;
  private static final int BUFFER_SIZE = 1024;

  private static ExecutorService executorService = Executors.newFixedThreadPool(20); // Adjust the thread pool size as needed


  public static void main(String[] args) {
    new Server().start();
  }

  @Override
  public void run() {
    try {
      socketTCP = new ServerSocket(TCP_PORT);
      socketUDP = new DatagramSocket(UDP_PORT);

      while (true) {
        //TCP
        Socket clientSocket = socketTCP.accept();
        ClientHandler clientHandler = new ClientHandler(clientSocket);
        executorService.execute(clientHandler);

        //UDP
        byte[] receiveData = new byte[BUFFER_SIZE];
        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
        socketUDP.receive(receivePacket);

        DatagramPacket clientPacket = new DatagramPacket(
                receivePacket.getData(),
                receivePacket.getLength(),
                receivePacket.getAddress(),
                receivePacket.getPort()
        );

        executorService.execute(new ClientHandlerUDP(clientPacket));
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private static class ClientHandler implements Runnable {
    private Socket clientSocket;

    public ClientHandler(Socket clientSocket) {
      this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
      try {
        InputStream inputStream = clientSocket.getInputStream();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        int dataByte;
        byte[] receivedData;

        while ((dataByte = inputStream.read()) != -1) {
          byteArrayOutputStream.write(dataByte);
          if ((char) dataByte == '\n') {
            byte[] receivedDataWithNewline = byteArrayOutputStream.toByteArray();
            receivedData = new byte[receivedDataWithNewline.length - 1];
            System.arraycopy(receivedDataWithNewline, 0, receivedData, 0, receivedData.length);
            System.out.println("Received: " + new String(receivedData));
            byteArrayOutputStream.reset();
          }
        }

      } catch (IOException e) {
        System.out.println("Client error : " +e);
      } finally {
        try {
          clientSocket.close();
          System.out.println("Client disconnected: " + clientSocket.getInetAddress());
        } catch (IOException e) {
          System.out.println("Client close error : " +e);
        }
      }
    }
  }
}
  class ClientHandlerUDP implements Runnable {
    private final DatagramPacket clientPacket;

    public ClientHandlerUDP(DatagramPacket clientPacket) {
      this.clientPacket = clientPacket;
    }

    @Override
    public void run() {
      DatagramSocket socket = null;

      try {
        socket = new DatagramSocket();

        // Send a response back to the client
        byte[] responseData = "Server: Response".getBytes();
        DatagramPacket responsePacket = new DatagramPacket(
                responseData,
                responseData.length,
                clientPacket.getAddress(),
                clientPacket.getPort()
        );
        socket.send(responsePacket);

      } catch (IOException e) {
        e.printStackTrace();
      } finally {
        if (socket != null) {
          socket.close();
        }
      }
    }
  }
