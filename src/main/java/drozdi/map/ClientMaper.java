package drozdi.map;

import drozdi.gameobjects.Player.Player;
import lombok.Getter;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ClientMaper extends Maper {
  @Getter
  private Set<Player> playerList = new HashSet<>();
}
