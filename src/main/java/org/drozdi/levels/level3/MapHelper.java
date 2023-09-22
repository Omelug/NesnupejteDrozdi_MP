package org.drozdi.levels.level3;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.Synchronized;
import org.drozdi.game.FileManager;
import org.drozdi.levels.level3.client.PlayerMP;
import org.drozdi.levels.level3.server.GameServer;
import org.drozdi.levels.level3.server.HitBoxHelper;
import org.drozdi.levels.level3.walls.*;

import java.awt.image.BufferedImage;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

@Data
public class MapHelper {
    @Getter(onMethod_={@Synchronized}) @Setter(onMethod_={@Synchronized})
    private Set<PlayerMP> playerList = new HashSet<>();

    private Set<Wall> walls = new HashSet<>();
    private Set<Hedgehog> hedgehogs = new HashSet<>();
    private Set<Ladder> ladders = new HashSet<>();
    private Set<Checkpoint> checkpoints = new HashSet<>();
    private Set<Bullet> playerShots = new HashSet<>();
    private Set<Bullet> entityShots = new HashSet<>();
    private Set<Tower> towers = new HashSet<>();
    private Set<Slug> slugs = new HashSet<>();
    private Set<Door> doors = new HashSet<>();
    private ArrayList<Key> keys = new ArrayList<>();

    //TODO offline game
    /**@Synchronized
    private void loadMap(int mapNumber) {
        BufferedImage map = FileManager.loadResource("Level3/maps/map" + mapNumber + ".bmp");
        loadMap(map);
    }**/
    public void loadMap() {
        BufferedImage map = FileManager.loadResource("client_data/maps/server_map.bmp");
        loadMap(map);
    }

    @Synchronized
    private void loadMap(BufferedImage map) {
        FileManager_lvl3.loadResources();

        int wall = FileManager_lvl3.palette.getRGB(0, 0);
        int hedgehog = FileManager_lvl3.palette.getRGB(1, 0);
        int ladder = FileManager_lvl3.palette.getRGB(2, 0);
        int tower = FileManager_lvl3.palette.getRGB(3, 0);
        int checkpoint = FileManager_lvl3.palette.getRGB(4, 0);
        int key = FileManager_lvl3.palette.getRGB(5, 0);
        int door = FileManager_lvl3.palette.getRGB(6, 0);
        int slug = FileManager_lvl3.palette.getRGB(7, 0);

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

    public PlayerMP getPlayerByName(String name) {
        for (PlayerMP player : playerList) {
            if (player.getName().equals(name)){
                return player;
            }
        }
        return null;
    }

    /**@Synchronized
    public void removeByName(String name) {
        for (PlayerMP player : playerList) {
            if (player.getName().equals(name)){
                playerList.remove(player);
            }
        }
    }**/

    public void addPlayer(PlayerMP playerMP) {
        boolean found = false;
        for (PlayerMP player : playerList) {
            if (player.getName().trim().equals(playerMP.getName().trim())){
                found = true;
            }
        }
        if (!found){
            playerList.add(playerMP);
        }
    }

    public boolean playerConnected(HitBoxHelper hitBoxHelper, InetAddress address, int port) {
        PlayerMP player = hitBoxHelper.getPlayerByIp(address);
        if (player != null) {
            System.out.println("Player " + player.getName() + " is connected");
            return true;
        }
        return false;
    }

    public void removePlayer(Socket clientSocket) {
        Iterator<PlayerMP> iterator = playerList.iterator();
        while (iterator.hasNext()) {
            PlayerMP player = iterator.next();
            if (player.getClientSocket().equals(clientSocket)) {
                GameServer.getLogger().playerDisconnect(player);
                iterator.remove();
            }
        }
    }
}
