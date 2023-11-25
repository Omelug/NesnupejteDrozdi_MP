package drozdi.gameobjects.Player;

import drozdi.FileManager;
import drozdi.clientside.Panel;
import drozdi.gameobjects.GameCubeFloat;
import lombok.Getter;
import lombok.Setter;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

@Getter
public class Player extends GameCubeFloat {

  protected String name;
  @Setter
  private Direction direction = Direction.RIGHT;
  @Setter
  boolean shooting;
  @Setter
  boolean up, down, left, right;

  public Player(String name) {
    this.name = name;
    setPosition(new Point2D.Float(0, 0));
    setSize(new Point2D.Float(0.9F,0.9F));
  }

  public enum Direction {
    UP, DOWN,RIGHT,LEFT
  }
  public void draw(Panel panel) {
    //System.out.println("awdawdawdaw");
    /*if (Test.isHitBoxPlayer()) {
            g2d.setColor(Color.green);
            panel.drawHitBox(getHitBox(panel));
        }
     */
    if (direction == null){
      drawPlayer(FileManager.playerRight, panel);
      return;
    }
    switch (direction){
      case UP -> drawPlayer(FileManager.playerUp, panel);
      case RIGHT -> drawPlayer(FileManager.playerRight, panel);
      case DOWN -> drawPlayer(FileManager.playerDown, panel);
      case LEFT -> drawPlayer(FileManager.playerLeft, panel);
    }
  }
  private void drawPlayer(BufferedImage image, Panel panel) {
    //System.out.println("player " + getHitBox());
    Rectangle2D.Float hitBox = getHitBox();
    //hitBox.x -= panel.getShift().x;
    //panel.getG2d().drawImage( image, (int) (hitBox.x * panel.getCellSize()), (int) (hitBox.y  * panel.getCellSize()), (int) (getSize().x * panel.getCellSize()), (int) (getSize().y* panel.getCellSize()), null);
    panel.getG2d().drawImage( image, FileManager.defaultMapPosition.x * panel.getCellSize(), (int) (hitBox.y  * panel.getCellSize()), (int) (getSize().x * panel.getCellSize()), (int) (getSize().y* panel.getCellSize()), null);
  }

  public synchronized Rectangle2D.Float getHitBox(){
    return new Rectangle2D.Float(getPosition().x, getPosition().y, getSize().x, getSize().y);
  }

  public synchronized byte[] encodeMovement() {
    byte[] data = new byte[1];
    data[0] = (byte) ((up ? 1 : 0) |
            (down ? 2 : 0) |
            (left ? 4 : 0) |
            (right ? 8 : 0));
    return data;
  }

  public synchronized void setMovement(byte[] data) {
    up = (data[0] & 1) == 1;
    down = (data[0] & 2) == 2;
    left = (data[0] & 4) == 4;
    right = (data[0] & 8) == 8;
  }

  /*

public synchronized void shot(HitBoxHelper hitBoxHelper) {
        shot++;
        if (shot > 20 && (shooting || direction == Direction.DOWN)) {
        shot = 0;
        hitBoxHelper.getMapHelper().getPlayerShots().add(new Bullet(this));
        }
        }

  public void dead() {
          deathCount++;
          //TODO dead
          }


    */
}
