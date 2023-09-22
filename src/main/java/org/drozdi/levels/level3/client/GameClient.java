package org.drozdi.levels.level3.client;

import lombok.Getter;
import lombok.Setter;
import org.drozdi.levels.level0.Level0;
import org.drozdi.levels.level3.Level3;
import org.drozdi.levels.level3.GamePanel;
import org.drozdi.levels.level3.console.ConsoleLogger;
import org.drozdi.levels.level3.error.ErrorManager;
import org.drozdi.levels.level3.error.GameError;
import org.drozdi.net.*;

import java.awt.geom.Point2D;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class GameClient extends Thread {

  @Getter @Setter
  private static InetAddress serverIp;

  static {
    try {
      serverIp = InetAddress.getByName(NetSettings.getDefaultServerIp());
    } catch (UnknownHostException e) {
      throw new RuntimeException(e);
    }
  }

  @Getter @Setter
  private static int serverUDPPort = NetSettings.defaultServerUDPPort;
  @Getter @Setter
  private static int listeningUDPPort = NetSettings.defaultClientListeningUDPPort;
  @Getter @Setter
  private static int serverTCPPort = NetSettings.defaultServerTCPPort;

  @Getter
  private static DatagramSocket socketUDP;
  @Getter
  private static DatagramSocket listeningSocketUDP;
  @Getter
  private Socket socketTCP;
  @Getter
  private static final ConsoleLogger logger = new ConsoleLogger();

  boolean receivingMapData;
  ByteArrayOutputStream outputStream;

  public GameClient(){
  }

  @Override
  public void run() {
    receivingMapData = false;
    outputStream = null;

    Thread TCPThread = new Thread(this::receiveTCP);
    TCPThread.start();

    Thread UDPThread = new Thread(this::receiveUDP);
    UDPThread.start();

  }

  private void receiveTCP() {
    logger.msgTCP("receiveTCP started");
    try {
      socketTCP = new Socket(serverIp, serverTCPPort);
      getMap();

      InputStream inputStream = socketTCP.getInputStream();
      ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

      int dataByte = 0;
      byte[] receivedData;
      boolean running = true;

      while (running) {
        try {
          dataByte = inputStream.read();
        }catch (IOException e) {
          running = false;
        }
        byteArrayOutputStream.write(dataByte);
        if ((char) dataByte == '\n') {
          byte[]receivedDataWithNewline = byteArrayOutputStream.toByteArray();
          receivedData = new byte[receivedDataWithNewline.length - 1];
          System.arraycopy(receivedDataWithNewline, 0, receivedData, 0, receivedData.length);
          byteArrayOutputStream.reset();
          if (new String(receivedData).length() >= 4) {
            String id = new String(receivedData).substring(0,4);
            Packet.PacketType packetType = Packet.lookupPacket(Integer.parseInt(id.substring(0,2)));
            int idLengthInBytes = id.getBytes().length;
            int restOfDataLength = receivedData.length - idLengthInBytes;
            byte[] restOfData = new byte[restOfDataLength];
            System.arraycopy(receivedData, idLengthInBytes, restOfData, 0, restOfDataLength);

            switch (packetType){
              case LOGIN -> {
                Packet01Login.LoginPacketType loginPacketType = Packet01Login.lookupMapPacket(Integer.parseInt(id.substring(2, 4)));
                switch (loginPacketType) {
                  case  CONNECT -> {
                    try {
                      ByteArrayInputStream byteStream = new ByteArrayInputStream(restOfData);
                      ObjectInputStream objectStream = new ObjectInputStream(byteStream);

                      Point2D.Double position = (Point2D.Double) objectStream.readObject();
                      Point2D.Double size = (Point2D.Double) objectStream.readObject();

                      GamePanel.getPlayer().setPosition(position);
                      GamePanel.getPlayer().setSize(size);

                      getPanel().getMapHelper().getPlayerList().add(GamePanel.getPlayer());

                      /**System.out.print("Players {");
                       for (PlayerMP player : getPanel().getMapHelper().getPlayerList()) {
                       System.out.print(player.getName());
                       }
                       System.out.print("}");**/
                      objectStream.close();
                      byteStream.close();

                    } catch (IOException | ClassNotFoundException e) {
                      e.printStackTrace();
                    }
                  }
                  case DISCONNECT -> {
                    disconnect(false);
                    running = false;
                  }
                }
              }
              case MAP -> {
                Packet03Map.MapPacketType mapPacketType = Packet03Map.lookupMapPacket(Integer.parseInt(id.substring(2, 4)));
                switch (mapPacketType){
                  case START -> {
                    outputStream = new ByteArrayOutputStream();
                    receivingMapData = true;
                  }
                  case DATA -> {
                    if (receivingMapData) {
                      logger.TCPInfoReceive("chunk downloaded");
                      outputStream.write(restOfData, 0, restOfData.length);
                    }
                  }
                  case STOP -> {
                    receivingMapData = false;
                    byte[] mapData = outputStream.toByteArray();
                    File outputFile = new File("src/main/resources/client_data/maps/server_map.bmp");
                    logger.TCPInfoReceive("Map downloaded (" + outputFile.exists()+ ") ->" + outputFile.getAbsolutePath());
                    try (FileOutputStream fileOutputStream = new FileOutputStream(outputFile)) {
                      fileOutputStream.write(mapData);
                    } catch (IOException e) {
                      e.printStackTrace();
                    }
                    outputStream.close();
                  }
                }
              }
            }
          }
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }finally {
      try {
        socketTCP.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    logger.msgTCP("receiveTCP stopped");
  }

  private void receiveUDP() {
    logger.msgUDP("receiveUDP started");

    try {
      socketUDP = new DatagramSocket();
      listeningSocketUDP = new DatagramSocket(NetSettings.defaultClientListeningUDPPort);

      boolean openUDP = true;
      while (openUDP) {
        byte[] data = new byte[NetSettings.getMapChunkSize() +  NetSettings.getPacketHeaderSize()];
        DatagramPacket packet = new DatagramPacket(data, data.length, serverIp, listeningUDPPort);
        try {
          listeningSocketUDP.receive(packet);
        } catch (IOException e) {
          StackTraceElement[] elements = e.getStackTrace();
          System.out.println("UDP " + elements[0]);
          if (elements[0].toString().contains("Socket closed")) {
            openUDP = false;
          }
        }

        byte[] receivedData = packet.getData();
        String message = new String(receivedData).trim();
        String id = new String(receivedData).substring(0,4);

        int idLengthInBytes = id.getBytes().length;
        int restOfDataLength = receivedData.length - id.getBytes().length;
        byte[] restOfData = new byte[restOfDataLength];
        System.arraycopy(receivedData, idLengthInBytes, restOfData, 0, restOfDataLength);
        Packet.PacketType packetType = Packet.lookupPacket(Integer.parseInt(id.substring(0,2)));
        if (Objects.requireNonNull(packetType) == Packet.PacketType.PLAYER) {
          Packet04Player.PlayerPacketType playerPacketType = Packet04Player.lookupPlayerPacket(Integer.parseInt(id.substring(2, 4)));
          //System.out.println( "move " + new String(data));
          switch (playerPacketType) {
            case MOVE -> {
              try {
                ByteArrayInputStream byteStream = new ByteArrayInputStream(restOfData);
                ObjectInputStream objectStream = new ObjectInputStream(byteStream);

                PlayerMP player = getPlayer();

                if (player != null) {
                  player.setName((String) objectStream.readObject());
                  player.setPosition((Point2D.Double) objectStream.readObject());
                  player.setDirection((Direction) objectStream.readObject());
                  player.setOnGround((boolean) objectStream.readObject());

                  objectStream.close();
                  byteStream.close();
                } else {
                  System.out.print(" player is null");
                }
              } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
              }
            }
            case NEXT_PLAYER -> {
              try {

                ByteArrayInputStream byteStream = new ByteArrayInputStream(restOfData);
                ObjectInputStream objectStream = new ObjectInputStream(byteStream);

                String name = (String) objectStream.readObject();
                PlayerMP player = getPanel().getMapHelper().getPlayerByName(name);

                //System.out.println(new String(restOfData));
                if (player != null) {
                  player.setName(name);
                  player.setPosition((Point2D.Double) objectStream.readObject());
                  player.setDirection((Direction) objectStream.readObject());
                  player.setOnGround((boolean) objectStream.readObject());

                  objectStream.close();
                  byteStream.close();
                } else {
                  System.out.print(" player is null");
                }

              } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
              }
            }
          }
        }
        if (message.equals("pong")) {
          Level0.changeConnectionStatus();
        }
      }

    } catch (SocketException e) {
      throw new RuntimeException(e);
    }

    logger.msgUDP("receiveUDP stopped");
  }

  private void getMap(){
    new Packet03Map(Packet03Map.MapPacketType.START).writeDataTCP(this);
  }

  public void sendDataUDP(byte[] data){
    DatagramPacket packet = new DatagramPacket(data, data.length, serverIp, serverUDPPort);
    System.out.println("RAW UDP DATA " + new String(data));
    try {
      socketUDP.send(packet);
    } catch (IOException e) {
      if (socketUDP.isClosed()){
        ErrorManager.newError(GameError.ErrorType.UDP,"Socket was closed");
      }else{
        e.printStackTrace();
      }
    }
  }
  public void sendDataTCP(byte[] data){
    //System.out.println("RAW DATA " + new String(data, StandardCharsets.UTF_8));
    //System.out.println(new String(data));
    try {
      OutputStream outStream = socketTCP.getOutputStream();
      PrintWriter out = new PrintWriter(outStream, true);
      out.write(new String(data, StandardCharsets.UTF_8)+'\n');
      out.flush();
    } catch (IOException e) {
      ErrorManager.newError(GameError.ErrorType.TCP, "Could not connect to server");
    }
  }

  public void login() {
    new Packet01Login(Packet01Login.LoginPacketType.CONNECT).writeDataTCP(this);
  }
  public void disconnect(boolean sendToServer) {
    if (sendToServer) {
      new Packet01Login(Packet01Login.LoginPacketType.DISCONNECT).writeDataTCP(this);
    }
    try {
      socketTCP.close();
      socketUDP.close();
      listeningSocketUDP.close();
    } catch (IOException e) {
      ErrorManager.newError(GameError.ErrorType.UNDEFINED, "Could not close connections");
    }
    getPanel().setRunning(false);
  }

  public void move() {
    new Packet04Player(Packet04Player.PlayerPacketType.MOVE).writeDataUDP(this);
  }
  public void dead(){
    getPanel().updateInfo();
  }

  //short private function for short calling
  private static GamePanel getPanel(){
    return Level3.getGamePanel();
  }
  public static PlayerMP getPlayer(){
    return GamePanel.getPlayer();
  }

}
