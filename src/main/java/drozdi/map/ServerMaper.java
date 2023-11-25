package drozdi.map;

import drozdi.gameobjects.Player.Player;
import drozdi.gameobjects.Player.ServerPlayer;
import drozdi.serverside.Hitboxer;
import drozdi.serverside.Server;
import drozdi.serverside.ServerConfig;
import lombok.Getter;

import java.awt.geom.Point2D;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;

public class ServerMaper extends Maper{
  @Getter
  private Hitboxer hitboxer;
  @Getter
  private Set<ServerPlayer> playerList = new HashSet<>();
  @Getter
  private ServerConfig serverConfig;

  public ServerMaper(Hitboxer hitboxer) {
    this.hitboxer = hitboxer;
  }
  public synchronized void addPlayer(Socket clientSocket, ServerConfig serverConfig) {
    String name = generateNewName(playerList);
    this.serverConfig = serverConfig;

    ServerPlayer newPlayer = new ServerPlayer(name, clientSocket, this);
    newPlayer.setPosition(new Point2D.Float(serverConfig.getStartPosition().x,serverConfig.getStartPosition().y));
    getPlayerList().add(newPlayer);
  }

  public void sendTickInfo() {
    for (ServerPlayer sender : getPlayerList()){
      for (ServerPlayer receiver : getPlayerList()){
        sender.sendPosition(receiver);
      }
    }
  }
  public synchronized void removePlayer(Socket clientSocket) {
    playerList.removeIf(player -> player.getTcpClientSocket().equals(clientSocket));
  }
}
