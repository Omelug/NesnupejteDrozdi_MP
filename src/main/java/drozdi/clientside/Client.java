package drozdi.clientside;

import drozdi.console.ConsoleColors;
import drozdi.console.ConsoleLogger;
import drozdi.gameobjects.Player.Player;
import drozdi.map.Maper;
import drozdi.net.Packet;
import drozdi.net.Packet.FirstPacketID;
import drozdi.net.Packet.SecondPacketID;
import drozdi.net.connection.TCPReceiver;
import drozdi.net.connection.UDPReceiver;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.cli.*;

import java.awt.geom.Point2D;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;

public class Client implements TCPReceiver, UDPReceiver {
  static private final ConsoleLogger log = new ConsoleLogger(ConsoleColors.YELLOW);
  @Getter
  private Panel panel;
  private Window window;

  //TCP
  @Getter
  private Socket tcpClientSocket;

  //UDP
  @Getter
  private InetAddress serverIp;
  @Getter
  private int clientUdpPort, serverUdpPort;
  @Getter
  private DatagramSocket udpClientSocket;


  private boolean runningTCP;
  private boolean runningUDP;

  private boolean receivingMapData = false;
  private ByteArrayOutputStream outputStream = null;

  public static void main(String[] args) {
    Client client = new Client();

    Options options = new Options();
    CommandLineParser parser = new DefaultParser();
    
    options.addOption("ip", true, "IP address");
    options.addOption("TCPport", true, "Port number");
    options.addOption("UDPport", true, "Port number");
    options.addOption("name", true, "Player name");
    try {
      CommandLine cmd = parser.parse(options, args);

      client.serverIp = InetAddress.getByName(cmd.getOptionValue("ip"));

      int tcpPort = Integer.parseInt(cmd.getOptionValue("TCPport"));
      client.serverUdpPort = Integer.parseInt(cmd.getOptionValue("UDPport"));

      String name = cmd.getOptionValue("name");

      log.info("Client started");
      log.info("IP: " + client.serverIp+":"+ tcpPort+"&&"+ client.serverIp);
      log.info("Player name: " + name);

      client.run(tcpPort, name);

    } catch (ParseException | UnknownHostException e) {
      log.terror(e.getMessage());
    }
  }

  public void run(int tcpPort, String name) {

    //TCP
    try {
      tcpClientSocket =  new Socket(serverIp, tcpPort);
      Thread TCPThread = new Thread(() -> receiveTCP(tcpClientSocket,log));
      runningTCP = true;
      TCPThread.start();
    } catch (IOException e) {
      log.error(e.getMessage());
    }

    //UDP
    try {
      udpClientSocket = new DatagramSocket();
      clientUdpPort = udpClientSocket.getLocalPort();
      Thread UDPThread = new Thread(() -> receiveUDP(udpClientSocket,log));
      runningUDP = true;
      UDPThread.start();
    } catch (SocketException e) {
      log.error(e.getMessage());
    }

    //map
    receivingMapData = true;
    new Packet(FirstPacketID.MAP, SecondPacketID.START).sendTCP(tcpClientSocket);
    //TODO wait untip map is downloaded

    while (receivingMapData) {
      //TODO tohle jde urcite lepe
      try {
        Thread.sleep(1000);
      } catch (InterruptedException e) {
        log.error("Wait... there is error" + e.getMessage());
      }
    }


    window = new Window(this);
    panel = window.createPanel(new Panel(name, this));

    byte[] connectData = (name+";"+clientUdpPort).getBytes();
    log.debug("Connecting with upd port " + clientUdpPort);
    new Packet(FirstPacketID.LOGIN, SecondPacketID.CONNECT, connectData).sendTCP(tcpClientSocket);
    window.renderStart();
    panel.renderStart();
  }

  public void stop() {
    try {
      tcpClientSocket.close();
    } catch (IOException e) {
      log.error(e.getMessage());
    }
  }
  int i = 0;
  @Override
  public void processPacket(Packet packet) {
    switch (packet.getFirstId()){
      case LOGIN -> {
        switch (packet.getSecondId()) {
          case  CONNECT -> {
            try {
              ByteArrayInputStream byteStream = new ByteArrayInputStream(packet.getData());
              ObjectInputStream objectStream = new ObjectInputStream(byteStream);

              Point2D.Float position = (Point2D.Float) objectStream.readObject();
              Point2D.Float size = (Point2D.Float) objectStream.readObject();

              panel.getPlayer().setPosition(position);
              panel.getPlayer().setSize(size);

              //panel.getMaper().getPlayerList().add(panel.getPlayer());

              objectStream.close();
              byteStream.close();

            } catch (IOException e){
              log.error(String.valueOf(e));
            } catch (ClassNotFoundException e) {
              log.error("Invalid conncetion info - ClassNotFoundException");
            }
          }
          case DISCONNECT -> {
            //disconnect(false);
            runningTCP = false;
          }
        }
      }
      case MAP -> {
        switch (packet.getSecondId()){
          case START -> {
            outputStream = new ByteArrayOutputStream();
          }
          case DATA -> {
            if (receivingMapData) {
              //log.debug("outputStream.toByteArray().length "+ outputStream.toByteArray().length +" "+ packet.getData().length + " " + i++);
              outputStream.write(packet.getData(), 0, packet.getData().length);
              //log.info("chunk("+packet.getData().length+") downloaded");
            }
          }
          case STOP -> {
            if (receivingMapData){
              receivingMapData = false;
              byte[] mapData = outputStream.toByteArray();
              File outputFile = new File("src/main/resources/client_data/maps/server_map.bmp");
              log.info("map downloaded (" + outputFile.exists()+ ") ->" + outputFile.getAbsolutePath());

              FileOutputStream fileOutputStream;
              try {
                fileOutputStream = new FileOutputStream(outputFile);
                fileOutputStream.write(mapData);
                outputStream.close();
              } catch (IOException e) {
                log.error(e.getMessage());
              }
            }else{
              log.error("Server want stop map downloading, but client is not downloading");
            }
          }
          default -> {
            log.warn("Invalid packet"  + packet);
          }
        }
      }
      case PLAYER -> {
        switch (packet.getSecondId()){
          case POSITION -> {
            try {
              ByteArrayInputStream byteStream = new ByteArrayInputStream(packet.getData());
              ObjectInputStream objectStream = new ObjectInputStream(byteStream);

              String name = (String) objectStream.readObject();
              Player player = new Player(name); //TODO porasit prejmenovani hrace
              Point2D.Float pos = (Point2D.Float) objectStream.readObject();
              //player.setDirection((Player.Direction) objectStream.readObject());

              objectStream.close();
              byteStream.close();
              //System.out.println("" + (float) newPosition.getX());

              if (Maper.getPlayerByName(name, getPanel().getMaper().getPlayerList()) == null) {
                if (getPanel().getPlayer().getName().equals(name)){
                  getPanel().getPlayer().getPosition().x = pos.x;
                  getPanel().getPlayer().getPosition().y = pos.y;//player.getPosition().x,player.getPosition().y));
                  getPanel().getPlayer().setDirection(player.getDirection());
                }else{
                  log.warn("player " + name + " not found" ); //TODO/(zjistit jestli tohle bude pred pridanim hrace nebo by se mel pridat tady
                  getPanel().getMaper().getPlayerList().add(player);
                }
              }
            } catch (IOException | ClassNotFoundException e) {
              log.error("Player position error "+ e.getMessage());
            }
          }
        }
      }
    }
  }

  @Override
  public boolean getRunningTCP() {
    return runningTCP;
  }

  @Override
  public void setRunningTCP(boolean running) {
    runningTCP = running;
  }
  @Override
  public boolean getRunningUDP() {
    return runningUDP;
  }

  @Override
  public void setRunningUDP(boolean running) {
    runningUDP = running;
  }
  @Override
  public void processUDPPacket(Packet packet, int port) {
    //TODO port je tu asi zbytecny, ale co uz
    processPacket(packet);
  }
}
