package drozdi.gameobjects.Player;


import drozdi.clientside.Panel;
import drozdi.net.Packet;
import drozdi.net.Packet.FirstPacketID;
import drozdi.net.Packet.SecondPacketID;
import lombok.Setter;


public class ClientPlayer extends Player{
  @Setter
  private static Panel panel;
  public ClientPlayer(String name) {
    super(name);
  }

  public void move() {
    new Packet(FirstPacketID.PLAYER, SecondPacketID.MOVE, panel.getPlayer().encodeMovement()).sendTCP(panel.getClient().getTcpClientSocket());
  }
}
