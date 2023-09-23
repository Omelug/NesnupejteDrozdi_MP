package org.drozdi.levels.level3.server;

import lombok.Data;
import lombok.Getter;
import org.drozdi.levels.level3.GamePanel;
import org.drozdi.levels.level3.client.PlayerMP;
import org.drozdi.levels.level3.console.ConsoleLogger;
import org.drozdi.levels.level3.error.ErrorManager;
import org.drozdi.levels.level3.error.GameError;
import org.drozdi.net.*;

import java.awt.geom.Point2D;
import java.io.*;
import java.net.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Data
public class GameServer extends Thread{
  private static final int TCP_PORT = NetSettings.defaultServerTCPPort;
  private static final int UDP_PORT = NetSettings.defaultServerUDPPort;

  private DatagramSocket socketUDP;
  private ServerSocket socketTCP;
  private String mapPath;
  private HitBoxHelper hitBoxHelper;
  private int keys;
  @Getter
  public static final ConsoleLogger logger = new ConsoleLogger();
  private static ExecutorService executorService = Executors.newFixedThreadPool(10);

  public GameServer(){
    mapPath = "/server_data/maps/map2.bmp";
    hitBoxHelper = new HitBoxHelper(this);
    hitBoxHelper.getMapHelper().loadMap();
    try {
      socketUDP = new DatagramSocket(UDP_PORT);
    } catch (SocketException e) {
      throw new RuntimeException(e);
    }
  }

  private byte[] readMapFile() {
    try {
      InputStream inputStream = getClass().getResourceAsStream(mapPath);
      if (inputStream != null) {
        return inputStream.readAllBytes();
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }

  private void sendMapToClient(Socket clientSocket) {
    byte[] mapData = readMapFile();

    //System.out.println("All map: "+ Arrays.toString(mapData));
    new Packet03Map(Packet03Map.MapPacketType.START).writeDataTCP(this, clientSocket);

    int chunkSize = NetSettings.getMapChunkSize();
    int totalChunks = (int) Math.ceil((double) mapData.length / chunkSize);

    for (int chunkNumber = 0; chunkNumber < totalChunks; chunkNumber++) {

      int offset = chunkNumber * chunkSize;
      int length = Math.min(chunkSize, mapData.length - offset);

      byte[] chunk = new byte[length];
      System.arraycopy(mapData, offset, chunk, 0, length);

      new Packet03Map(Packet03Map.MapPacketType.DATA).writeDataTCP(this, clientSocket, chunk);
    }
    new Packet03Map(Packet03Map.MapPacketType.STOP).writeDataTCP(this, clientSocket);
  }

  private void updateTick() {
    while (true){
      long startTime = System.currentTimeMillis();

      hitBoxHelper.update();
      hitBoxHelper.sendAllData();

      long endTime = System.currentTimeMillis();
      long elapsedTime = endTime - startTime;
      long sleepDuration = Math.max(50 - elapsedTime, 0);// 20 TPS max
      ServerSeparated.serverWindow.setTPS(1000/(double) (elapsedTime+elapsedTime));
      try {
        Thread.sleep(sleepDuration);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }

  @Override
  public void run() {
    logger.serverStart();
    Thread updateThread = new Thread(this::updateTick);
    updateThread.start();

    Thread TCPThread = new Thread(this::receiveTCP);
    TCPThread.start();

    Thread UDPThread = new Thread(this::receiveUDP);
    UDPThread.start();

    /**Thread UDPThread = new Thread(this::receiveUDP);
    UDPThread.start();

    Thread TCPThread = new Thread(this::receiveTCP);
    TCPThread.start();**/

  }

  private void receiveUDP() {
    logger.msgUDP("receiveUDP started");
    try {
      while (true) {
        byte[] data = new byte[NetSettings.getMapChunkSize() +  NetSettings.getPacketHeaderSize()];
        DatagramPacket packet = new DatagramPacket(data, data.length);
        try {
          socketUDP.receive(packet);
        } catch (IOException e) {
          e.getStackTrace();
          System.out.println("UDP EROOR");
        }
        byte[] receivedData = packet.getData();
        String id = new String(receivedData).substring(0,4);
        Packet.PacketType packetType = Packet.lookupPacket(Integer.parseInt(id.substring(0,2)));
        //System.out.println(id + " "+ new String(receivedData));
        if (packetType == Packet.PacketType.PLAYER) {
          Packet04Player.PlayerPacketType playerPacketType = Packet04Player.lookupPlayerPacket(Integer.parseInt(id.substring(2, 4)));
          if (Objects.requireNonNull(playerPacketType) == Packet04Player.PlayerPacketType.MOVE) {
            try {
              int idLengthInBytes = id.getBytes().length;
              int restOfDataLength = receivedData.length - id.getBytes().length;

              byte[] restOfData = new byte[restOfDataLength];
              System.arraycopy(receivedData, idLengthInBytes, restOfData, 0, restOfDataLength);

              ByteArrayInputStream byteStream = new ByteArrayInputStream(restOfData);
              ObjectInputStream objectStream = new ObjectInputStream(byteStream);

              PlayerMP player = hitBoxHelper.getPlayerByIp(packet.getAddress());

              if (player == null) {
                logger.msgUDP("" + "player is null");
              }
              player.setUp((boolean) objectStream.readObject());
              player.setRight((boolean) objectStream.readObject());
              player.setDown((boolean) objectStream.readObject());
              player.setLeft((boolean) objectStream.readObject());
              player.setDown((boolean) objectStream.readObject());
              player.setShooting((boolean) objectStream.readObject());

              objectStream.close();
              byteStream.close();

            } catch (IOException | ClassNotFoundException e) {
              e.printStackTrace();
            }
          }
        }
      }
    }catch (Exception e) {
      e.printStackTrace();
    }finally {
      logger.msgUDP("socketUDP close");
      socketUDP.close();
    }
    logger.msgUDP("receiveUDP stopped");
  }

  private void receiveTCP() {
    try {
      socketTCP = new ServerSocket(TCP_PORT);
      while (true) {
        Socket clientSocket = socketTCP.accept();
        TCPClientHandler clientHandler = new TCPClientHandler(this,clientSocket);
        executorService.execute(clientHandler);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  private static class TCPClientHandler implements Runnable {
    private final Socket clientSocket;
    private final GameServer server;

    public TCPClientHandler(GameServer server, Socket clientSocket) {
      this.clientSocket = clientSocket;
      this.server = server;
    }

    @Override
    public void run() {
      System.out.println("TCPClientHandler started");
      try {
        InputStream inputStream = clientSocket.getInputStream();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        int dataByte;
        byte[] receivedData;

        whileLoop: while (((dataByte = inputStream.read()) != -1)) {
          byteArrayOutputStream.write(dataByte);
          if ((char) dataByte  == '\n') {
            byte[]receivedDataWithNewline = byteArrayOutputStream.toByteArray();
            receivedData = new byte[receivedDataWithNewline.length - 1];
            System.arraycopy(receivedDataWithNewline, 0, receivedData, 0, receivedData.length);
            byteArrayOutputStream.reset();

            String id = new String(receivedData).substring(0,4);
            Packet.PacketType packetType = Packet.lookupPacket(Integer.parseInt(id.substring(0,2)));
            int idLengthInBytes = id.getBytes().length;
            int restOfDataLength = receivedData.length - idLengthInBytes;
            byte[] restOfData = new byte[restOfDataLength];
            System.arraycopy(receivedData, idLengthInBytes, restOfData, 0, restOfDataLength);

            ErrorManager.newError(GameError.ErrorType.TCP, id + " " + new String(restOfData));

            switch (packetType){
              case LOGIN -> {
                Packet01Login.LoginPacketType loginPacketType = Packet01Login.lookupMapPacket(Integer.parseInt(id.substring(2, 4)));
                switch (loginPacketType){
                  case CONNECT -> {
                    HitBoxHelper hitBoxHelper =server.getHitBoxHelper();
                    String name;
                    int UDPPort;
                    String[] parts = new String(restOfData).trim().split(":");
                    if (parts.length == 2) {
                      name = parts[0];
                      UDPPort = Integer.parseInt(parts[1]);
                    } else {
                      System.err.println("Invalid input format");
                      break; //TODO error
                    }

                    if (hitBoxHelper.getMapHelper().playerConnected(hitBoxHelper, clientSocket.getInetAddress(), clientSocket.getPort())) {
                      hitBoxHelper.getMapHelper().removePlayer(clientSocket);
                    }
                    PlayerMP playerMP = new PlayerMP(name,clientSocket,UDPPort);
                    new Packet01Login(Packet01Login.LoginPacketType.CONNECT).sendToPlayer(server, playerMP, clientSocket);
                    hitBoxHelper.getMapHelper().addPlayer(playerMP);

                    logger.playerConnect(playerMP);

                     /*System.out.print("Players {");
                              for (PlayerMP player : getHitBoxHelper().getMapHelper().getPlayerList()) {
                                  System.out.print(player.getName());
                              }
                              System.out.print("}");*/
                  }
                  case DISCONNECT -> {
                    GameServer.logger.TCPInfoReceive("Client wants be disconnected");
                    server.getHitBoxHelper().getMapHelper().removePlayer(clientSocket);
                    break whileLoop;
                  }
                }
              }
              case MAP -> {
                Packet03Map.MapPacketType mapPacketType = Packet03Map.lookupMapPacket(Integer.parseInt(id.substring(2, 4)));
                switch (mapPacketType){
                  case START -> {
                    logger.TCPInfoReceive("sentMap START to " + clientSocket);
                    server.sendMapToClient(clientSocket);
                  }
                  case STOP -> {
                    //TODO user wants stop downloading of the map, stop it
                  }
                }
              }
            }
          }
        }
      } catch (IOException e) {
        e.printStackTrace();
      } finally {
        try {
          clientSocket.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
      System.out.println("TCPClientHandler deleted");
    }
  }
  public void sendDataUDP(byte[] data, InetAddress ipAddress, int port ){
    DatagramPacket packet = new DatagramPacket(data, data.length, ipAddress, port);
    try {
      socketUDP.send(packet);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  public void sendDataTCP(byte[] data, Socket clientSocket){
    //System.out.println("RAW DATA " + Arrays.toString(data));
    try {
      OutputStream outStream = clientSocket.getOutputStream();

      outStream.write(data);
      outStream.write("\n".getBytes());
      outStream.flush();
    } catch (IOException e) {
      e.printStackTrace();
      System.out.println("ERROR: " + new String(data));

    }
  }

  public void disconnectAll() {
    for (PlayerMP playerMP : hitBoxHelper.getMapHelper().getPlayerList()) {
      new Packet01Login(Packet01Login.LoginPacketType.DISCONNECT).writeDataTCP(this, playerMP.getClientSocket());
    }
    hitBoxHelper.getMapHelper().setPlayerList(new HashSet<>());
  }
}