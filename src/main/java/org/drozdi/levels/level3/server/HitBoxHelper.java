package org.drozdi.levels.level3.server;

import lombok.Data;
import org.drozdi.levels.level3.MapHelper;
import org.drozdi.levels.level3.Wall;
import org.drozdi.levels.level3.client.PlayerMP;
import org.drozdi.levels.level3.walls.Slug;
import org.drozdi.levels.level3.walls.Tower;
import org.drozdi.net.Packet04Player;

import java.awt.*;
import java.awt.geom.Point2D;
import java.net.InetAddress;

@Data
public class HitBoxHelper {
    public static final Point2D.Double defaultPosition = new Point2D.Double(13, 10);
    private Rectangle mapSize;
    private final GameServer gameServer;

    private MapHelper mapHelper = new MapHelper();

    public HitBoxHelper(GameServer gameServer){
        this.gameServer = gameServer;
        mapHelper.loadMap();
    }

    public void update() {
        for (Tower tower : mapHelper.getTowers()) {
            PlayerMP nearest = getNearestPlayer(tower, Tower.MAX_DISTANCE);
            if (nearest != null){
                tower.shot(nearest, this);
            }
        }
        for (Slug slug : mapHelper.getSlugs()) {
            PlayerMP nearest = getNearestPlayer(slug, Slug.MAX_DISTANCE);
            if (nearest != null){
                slug.shot(nearest, this);
            }
        }

        for (PlayerMP player : mapHelper.getPlayerList()) {
            player.updatePosition(this);
            player.shot(this);
        }

       /** Iterator<Bullet> entityBulletIterator = mapHelper.getEntityShots().iterator();
        while (entityBulletIterator.hasNext()) {
            Bullet bullet = entityBulletIterator.next();
            if (!mapSize.intersects(bullet.getHitBoxServer())){
                entityBulletIterator.remove();
                break;
            }
        }
        Iterator<Bullet> playerShotsIterator = mapHelper.getPlayerShots().iterator();
        while (playerShotsIterator.hasNext()) {
            Bullet bullet = playerShotsIterator.next();
            //TODO zjistit jestli je na mappe
            if (!screen.intersects(bullet.getHitBoxServer())){
                playerShotsIterator.remove();
                break;
            }
            for (Wall wall : mapHelper.getWalls()) {
                if (wall.getHitBoxServer().intersects(bullet.getHitBoxServer())) {
                    if (bullet.getBulletType() == BulletType.PLAYER) {
                        playerShotsIterator.remove();
                        break;
                    }
                }
            }
        }

        Iterator<Key> keyIterator = mapHelper.getKeys().iterator();
        while (keyIterator.hasNext()) {
            Key key = keyIterator.next();
            for (PlayerMP player : mapHelper.getPlayerList()) {
                if (player.getHitBoxServer().intersects(key.getHitBoxServer())) {
                    keyIterator.remove();
                    gameServer.setKeys(gameServer.getKeys()+1);
                    //TODO poslat packety updateInfo();
                }
            }
        }

        Iterator<Tower> towerIterator = mapHelper.getTowers().iterator();
        while (towerIterator.hasNext()) {
            Tower tower = towerIterator.next();
            Iterator<Bullet> bulletIterator = mapHelper.getPlayerShots().iterator();
            while (bulletIterator.hasNext()) {
                Bullet bullet = bulletIterator.next();
                if (bullet.getHitBoxServer().intersects(tower.getHitBoxServer())) {
                    bulletIterator.remove();
                    towerIterator.remove();
                    //TODO poslat hracum
                    break;
                }
            }
        }

        Iterator<Slug> slugrIterator = mapHelper.getSlugs().iterator();
        while (slugrIterator.hasNext()) {
            Slug slug = slugrIterator.next();
            Iterator<Bullet> bulletIterator = mapHelper.getEntityShots().iterator();
            while (bulletIterator.hasNext()) {
                Bullet bullet = bulletIterator.next();
                if (bullet.getHitBoxServer().intersects(slug.getHitBoxServer())) {
                    bulletIterator.remove();
                    slugrIterator.remove();
                    //TODO poslat hracum
                    break;
                }
            }
        }

        for (Bullet bullet : mapHelper.getPlayerShots()) {
            bullet.updatePosition();
            //TODO poslat hracum
        }

        for (Bullet bullet : mapHelper.getEntityShots()) {
            bullet.updatePosition();
            //TODO poslat hracum
        }**/

    }
    public PlayerMP getNearestPlayer(Wall wall, double maxDistance) {
        double distance = maxDistance;
        PlayerMP nearestPlayer = null;
        for (PlayerMP player : mapHelper.getPlayerList()) {
            double newTry = getDistance(
                    new Point((int) player.getPosition().getX(), (int) player.getPosition().getY())
                    ,wall.getPosition());
            if (newTry < distance){
                distance = newTry;
                nearestPlayer = player;
            }
        }
        return nearestPlayer;
    }
    public static double getDistance(Point point1, Point point2) {
        double deltaX = point2.x - point1.x;
        double deltaY = point2.y - point1.y;
        return Math.sqrt(deltaX * deltaX + deltaY * deltaY);
    }

    public PlayerMP getPlayerByIpAndPort(InetAddress address, int port) {
        PlayerMP foundPlayer = null;
        for (PlayerMP player : mapHelper.getPlayerList()) {
            if (player.getIpAddress().equals(address) && player.getPort() == port) {
                foundPlayer = player;
                break;
            }
        }
        return foundPlayer;
    }

    public void sendAllData() {
        for (PlayerMP player : mapHelper.getPlayerList()) {
            new Packet04Player(Packet04Player.PlayerPacketType.NEXT_PLAYER).writeAllClients(getGameServer(), player);
        }
    }
}
