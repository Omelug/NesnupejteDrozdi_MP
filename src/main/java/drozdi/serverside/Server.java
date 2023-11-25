package drozdi.serverside;

import drozdi.gameobjects.Player.ServerPlayer;
import drozdi.console.ConsoleColors;
import drozdi.console.ConsoleLogger;
import drozdi.net.Packet;
import drozdi.net.connection.UDPReceiver;
import org.apache.commons.cli.*;

import javax.swing.text.Position;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.net.*;
import java.util.Scanner;

public class Server implements Runnable, UDPReceiver {
  static private final ConsoleLogger log = new ConsoleLogger(ConsoleColors.PURPLE);
  private ServerSocket socketTCP;
  private DatagramSocket socketUDP;
  private Thread tcpStart, udpListener;
  private Hitboxer hitboxer;
  private static boolean runningUDP = true;
  ServerConfig serverConfig;

  public static void main(String[] args) {
    Server server = new Server();

    Options options = new Options();
    CommandLineParser parser = new DefaultParser();

    options.addOption("TCPport", true, "Port number");
    options.addOption("UDPport", true, "Port number");
    options.addOption("name", true, "Server name");

    try {
      CommandLine cmd = parser.parse(options, args);

      int tcpPort = Integer.parseInt(cmd.getOptionValue("TCPport"));
      int udpPort = Integer.parseInt(cmd.getOptionValue("UDPport"));
      String name = cmd.getOptionValue("name");

      log.info("Server started");
      log.info("IP: " + InetAddress.getLocalHost() + ":" + tcpPort + "&&" + udpPort);
      log.info("Player name: " + name);

      server.socketTCP = new ServerSocket(tcpPort);
      server.socketUDP = new DatagramSocket(udpPort);
      server.hitboxer = new Hitboxer();


      //TODO tohle pak zobecnicnit podle toho co člověk zvoli za mapu
      server.serverConfig = new ServerConfig();
      server.serverConfig.setStartPosition(new Point2D.Float(13,5));

      server.run();

    }catch (ParseException | IOException e) {
      log.terror(e.getMessage());
    }
  }
  public static boolean equalsToFirstORIgnoreCase(String first, String... strings) {
    if (strings == null) {
      return false;
    }
    for (String str : strings) {
      if (first.equalsIgnoreCase(str)) {
        return true;
      }
    }
    return false;
  }
  @Override
  public void run() {

    Thread serverCommands = new Thread(() -> {
      Scanner scanner = new Scanner(System.in);
      while (true) {
        String command = scanner.nextLine().trim();
        if (command.equalsIgnoreCase("help")) {
          log.info("help or h for help list");
          log.info("playerList or pl for player list, ple for extended info");
          log.info("TPS for TPS");
        }else if (equalsToFirstORIgnoreCase(command,"playerList","pl")){
          log.info("Player list:\n");
          for (ServerPlayer player : hitboxer.getMaper().getPlayerList()){
            System.out.println(player.toString());
          }
        }else if (command.equalsIgnoreCase("ple")) {
          if (hitboxer.getMaper().getPlayerList().isEmpty()){
            log.info("Player list is empty");
          }
          for (ServerPlayer player : hitboxer.getMaper().getPlayerList()){
            System.out.println(player.toString());
            System.out.println("  Position: "+ player.getPosition() +" onGround "+ player.isOnGround());
            System.out.println("  Speed: "+ player.getSpeed());
          }
        }else if (command.equalsIgnoreCase("exit")) {
          log.info("Exiting the server.");
          stopAll();
          break;
        }
        else if (command.equalsIgnoreCase("tps")) {
          log.info("TPS: " + hitboxer.getTPS());
        }
      }
      scanner.close();
    });
    serverCommands.start();

    tcpStart = new Thread(() -> {
      while (true) {
        try {
          Socket clientSocket = socketTCP.accept();
          log.info("Client connected [" + clientSocket.getInetAddress() + ":" + clientSocket.getPort()+"]");
          hitboxer.getMaper().addPlayer(clientSocket, serverConfig);
        } catch (IOException e) {
          log.error(e.getMessage());
        }
      }
    });
    tcpStart.start();

    udpListener = new Thread(() -> receiveUDP(socketUDP,log));
    udpListener.start();

    Thread hiBoxer_thread = new Thread(() -> hitboxer.tickLoop());
    hiBoxer_thread.start();

  }


  public void stopAll(){
    runningUDP = false;

    //udp.stop();
    //hitBozer.stop();
    System.exit(0);
  }

  @Override
  public void processUDPPacket(Packet packet, int port) {
    for (ServerPlayer player : hitboxer.getMaper().getPlayerList()){
      if (player.getUdpPort() == port){
        player.processPacket(packet);
        //log.debug("Player found for UDP (" +port+")");
        return;
      }
    }
    log.error("Player for UDP (" +port+")  not found Packet:" + packet );
  }

  //lombok cant generate getters and setters for interface methods
  @Override
  public boolean getRunningUDP() {
    return runningUDP;
  }
  @Override
  public void setRunningUDP(boolean running) {
    runningUDP = running;
  }

}
