package drozdi.serverside;

import drozdi.FileManager;
import drozdi.console.ConsoleColors;
import drozdi.console.ConsoleLogger;
import drozdi.gameobjects.Player.ServerPlayer;
import drozdi.map.Maper;
import drozdi.map.ServerMaper;
import lombok.Getter;

import java.awt.image.BufferedImage;

public class Hitboxer {
  static private final ConsoleLogger log = new ConsoleLogger(ConsoleColors.CYAN_UNDERLINED);
  static private final int MAX_TPS = 20;
  @Getter
  private double TPS = 0;
  @Getter
  private final ServerMaper maper;
  @Getter
  long elapsedTime;

  public Hitboxer(){
    maper = new ServerMaper(this);
    BufferedImage map = FileManager.loadResource("server_data/maps/map2.bmp");
    if (map != null){
      maper.loadMap(map);
    }else{
      log.terror(" Couldn't load map ");
    }
  }

  public void tickLoop() { // 1000/elapsedTime = TPS
    long startTime, endTime, elapsedTime, sleepDuration;

    while (true){
      //TODO TOhle ud2lat vevnit5e ticku, at to udatuju ve spravem tivku
      startTime = System.currentTimeMillis();

      tick();
      maper.sendTickInfo();

      endTime = System.currentTimeMillis();
      elapsedTime = endTime - startTime;

      sleepDuration = Math.max((1000/MAX_TPS) - elapsedTime, 0); //TODO MAX_TPS ?????

      TPS = (double)1000/(double)(elapsedTime+ sleepDuration);

      if(TPS <= 19){
        log.warn("Server is slow " + TPS+ "TPS");
      }

      try {
        Thread.sleep(Math.max(sleepDuration,1)); //TODO magic 1
      } catch (InterruptedException e) {
        log.warn(e.getMessage());
      }
    }
  }

  public void tick() {
    //log.debug("Hitboxer tick");

    for (ServerPlayer player : maper.getPlayerList()) {
      player.updatePosition();
      //player.shot(this);
    }

    //TODO pred timhle udelat zobeecneni objektu
    /*for (Tower tower : maper.getTowers()) {
      PlayerMP nearest = getNearestPlayer(tower, Tower.MAX_DISTANCE);
      if (nearest != null){
        tower.shot(nearest, this);
      }
    }
    for (Slug slug : maper.getSlugs()) {
      PlayerMP nearest = getNearestPlayer(slug, Slug.MAX_DISTANCE);
      if (nearest != null){
        slug.shot(nearest, this);
      }
    }

    /** Iterator<Bullet> entityBulletIterator = maper.getEntityShots().iterator();
     while (entityBulletIterator.hasNext()) {
     Bullet bullet = entityBulletIterator.next();
     if (!mapSize.intersects(bullet.getHitBoxServer())){
     entityBulletIterator.remove();
     break;
     }
     }
     Iterator<Bullet> playerShotsIterator = maper.getPlayerShots().iterator();
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

     Iterator<Key> keyIterator = maper.getKeys().iterator();
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

     Iterator<Tower> towerIterator = maper.getTowers().iterator();
     while (towerIterator.hasNext()) {
     Tower tower = towerIterator.next();
     Iterator<Bullet> bulletIterator = maper.getPlayerShots().iterator();
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

     Iterator<Slug> slugrIterator = maper.getSlugs().iterator();
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

     for (Bullet bullet : maper.getPlayerShots()) {
     bullet.updatePosition();
     //TODO poslat hracum
     }

     for (Bullet bullet : mapHelper.getEntityShots()) {
     bullet.updatePosition();
     //TODO poslat hracum
     }**/
  }

}
