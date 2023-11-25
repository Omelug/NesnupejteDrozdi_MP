package drozdi.map;

import drozdi.gameobjects.Bullet;
import drozdi.FileManager;
import drozdi.gameobjects.Player.Player;
import drozdi.console.ConsoleColors;
import drozdi.console.ConsoleLogger;
import drozdi.gameobjects.walls.*;
import drozdi.net.NetConst;
import drozdi.net.Packet;
import drozdi.net.Packet.FirstPacketID;
import drozdi.net.Packet.SecondPacketID;
import drozdi.gameobjects.Player.ServerPlayer;
import lombok.Getter;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class Maper {
  static private final ConsoleLogger log = new ConsoleLogger(ConsoleColors.WHITE_BOLD_BRIGHT);

  @Getter
  private Set<Wall> walls = new HashSet<>();
  @Getter
  private Set<Hedgehog> hedgehogs = new HashSet<>();
  @Getter
  private Set<Ladder> ladders = new HashSet<>();
  @Getter
  private Set<Checkpoint> checkpoints = new HashSet<>();
  @Getter
  private Set<Bullet> playerShots = new HashSet<>();
  @Getter
  private Set<Bullet> entityShots = new HashSet<>();
  @Getter
  private Set<Tower> towers = new HashSet<>();
  @Getter
  private Set<Slug> slugs = new HashSet<>();
  @Getter
  private Set<Door> doors = new HashSet<>();
  @Getter
  private ArrayList<Key> keys = new ArrayList<>();


  public void sendMapToClient(Socket clientSocket) {
    byte[] mapData = readMapFile();

    if (mapData == null){
      log.error("Map file not found");
      return;
    }

    new Packet(FirstPacketID.MAP, SecondPacketID.START).sendTCP(clientSocket);

    int chunkSize = NetConst.getPacketSizeNoID();
    int totalChunks = mapData.length / chunkSize;

    if (mapData.length % chunkSize != 0) {
      totalChunks++;
    }

    log.info("Map file size is " + mapData.length +", it will be send in " + totalChunks+" chunks of "+ chunkSize + " bytes");

    for (int chunkNumber = 0; chunkNumber < totalChunks; chunkNumber++) {

      int offset = chunkNumber * chunkSize;
      int length = Math.min(chunkSize, mapData.length - offset);

      byte[] chunk = new byte[length];

      if (chunk.length % chunkSize != 0) {
        log.debug(" length  "+ length );
      }

      //log.debug("outputStream.toByteArray().length "+ chunk.length +" "+ chunk.length%16);

      System.arraycopy(mapData, offset, chunk, 0, length);
      new Packet(FirstPacketID.MAP, SecondPacketID.DATA, chunk).sendTCP(clientSocket);
    }
    new Packet(FirstPacketID.MAP, SecondPacketID.STOP).sendTCP(clientSocket);
  }

  public static Player getPlayerByName(String name, Set<Player> playerList) {
    if (playerList.isEmpty()){
      return null;
    }
    for (Player player : playerList) {
      if (player.getName().equals(name)){
        return player;
      }
    }
    return null;
  }


  public String generateNewName(Set<ServerPlayer> playerList) {
    String newName;
      do{
      String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789_";
      SecureRandom random = new SecureRandom();
      StringBuilder hash = new StringBuilder();

      for (int i = 0; i < ServerPlayer.MAX_NAME; i++) {
        int randomIndex = random.nextInt(characters.length());
        char randomChar = characters.charAt(randomIndex);
        hash.append(randomChar);
      }
      newName = hash.toString();
      }while (nameTaken(newName,playerList));
    return newName;
  }

  public synchronized boolean nameTaken(String newName, Set<ServerPlayer> playerList) {
    for (Player player : playerList) {
      if (player.getName().equals(newName)){
        log.warn("Player + " + newName + " already exists, generating new name");
        return true;
      }
    }
    return false;
  }

  private byte[] readMapFile() {
    try {
      InputStream inputStream = getClass().getResourceAsStream("/server_data/maps/map2.bmp");
      if (inputStream != null) {
        return inputStream.readAllBytes();
      }
    } catch (IOException e) {
      log.error("Error reading");
    }
    return null;
  }


  public synchronized void loadMap(BufferedImage map) {
    FileManager.loadResources();

    int wall = FileManager.palette.getRGB(0, 0);
    int hedgehog = FileManager.palette.getRGB(1, 0);
    int ladder = FileManager.palette.getRGB(2, 0);
    int tower = FileManager.palette.getRGB(3, 0);
    int checkpoint = FileManager.palette.getRGB(4, 0);
    int key = FileManager.palette.getRGB(5, 0);
    int door = FileManager.palette.getRGB(6, 0);
    int slug = FileManager.palette.getRGB(7, 0);

    for (int y = 0; y < map.getHeight(); y++) {
      for (int x = 0; x < map.getWidth(); x++) {
        int color = map.getRGB(x, y);
        if (color == wall) {
          walls.add(new Wall(x, y));
        } else if (color == hedgehog) {
          hedgehogs.add(new Hedgehog(x, y));
        } else if (color == ladder) {
          ladders.add(new Ladder(x, y));
        } else if (color == tower) {
          towers.add(new Tower(x, y));
        } else if (color == checkpoint) {
          checkpoints.add(new Checkpoint(x, y));
        } else if (color == key) {
          keys.add(new Key(x, y));
        } else if (color == door) {
          doors.add(new Door(x, y, 5));
        } else if (color == slug) {
          slugs.add(new Slug(x, y));
        }
      }
    }
  }


}
