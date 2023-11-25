package drozdi.gameobjects;

import drozdi.FileManager;
import drozdi.gameobjects.Player.Player;
import drozdi.clientside.Panel;
import lombok.Data;

import java.awt.*;
import java.awt.geom.Point2D;

@Data
public class Bullet{
	public static Point size = new Point(20,20);
	private Point2D.Float speed;
	private static int bulletSpeed = 10;
	private BulletType bulletType;
	private Point2D.Float position;

	public Bullet(Player player) {
		bulletType = BulletType.PLAYER;
		position = new Point2D.Float(
						player.getPosition().x + player.getSize().x / 2 - (float) size.x / 2,
						player.getPosition().y + player.getSize().y / 2 - (float) size.y / 2);
		switch (player.getDirection()){
			case UP -> speed = new Point2D.Float(0, -bulletSpeed);
			case RIGHT -> speed = new Point2D.Float(bulletSpeed, 0);
			case DOWN -> speed = new Point2D.Float(0, bulletSpeed);
			case LEFT -> speed = new Point2D.Float(-bulletSpeed, 0);
		}
	}


  private enum BulletType {
		PLAYER,
		TOWER,
		SLUG
	}
	/*public Bullet(Tower tower, Player player) {
		bulletType = BulletType.TOWER;
		position = new Point2D.Float(tower.getPosition().x + (float) size.x / 2,
          (float) (tower.getPosition().y + (float) size.y * 0.25));
		double x = player.getHitBoxServer().x + (float) player.getSize().x / 2 - (tower.getHitBoxServer().x + (double) tower.getHitBoxServer().width / 2);
		double y = player.getHitBoxServer().y + (float) player.getSize().y / 2 - (tower.getHitBoxServer().y + (double) tower.getHitBoxServer().height / 2);
		double proportion = (bulletSpeed / Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2)));
		speed = new Point2D.Float((float) (proportion * x), (float) (proportion * y));
	}

	public Bullet(Slug slug, drozdi.levels.level3.client.PlayerMP player) {
		bulletType = BulletType.SLUG;
		position = new Point2D.Float((float) (slug.getPosition().x + (double) size.x / 2),
				slug.getPosition().y + size.y );
		double x = player.getHitBoxServer().x + player.getSize().x / 2 - (slug.getHitBoxServer().x + (double) slug.getHitBoxServer().width / 2);
		double y = player.getHitBoxServer().y - (slug.getHitBoxServer().y + (double) slug.getHitBoxServer().height / 2);
		double proportion = (bulletSpeed / Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2)));
		speed = new Point2D.Float((float) (proportion * x*1.25), (float) (proportion * y*1.25));
	}
*/
	public Rectangle getHitBox(Panel panel) {
		return new Rectangle((int) (position.x - panel.getShift().x), (int) (position.y), (int) size.x, (int) size.y);
	}
	public Rectangle getHitBoxServer() {
		return new Rectangle((int) position.x, (int) position.y, (int) size.x, (int) size.y);
	}

	public void updatePosition() {
		position.x += speed.x;
		position.y += speed.y;
	}

	public void draw(Panel panel) {
		switch (bulletType) {
			case PLAYER ->  {
				panel.getG2d().setColor(Color.cyan);
				Rectangle hitBox = getHitBoxServer();
				panel.getG2d().drawImage((Image) FileManager.bullet, hitBox.x, hitBox.y, (int) size.x, (int) size.y, null);
			}
			case TOWER -> {
				panel.getG2d().setColor(Color.red);
				Rectangle hitBox = getHitBoxServer();
				panel.getG2d().fillRect(hitBox.x, hitBox.y, (int) size.x, (int) size.y);
			}
			case SLUG -> {
				panel.getG2d().setColor(Color.cyan);
				Rectangle hitBox = getHitBoxServer();
				panel.getG2d().fillRect(hitBox.x, hitBox.y, (int) size.x, (int) size.y);
			}
		}

		/*if (drozdi.game.Test.isHitBoxBullets()) {
			g2d.setColor(Color.green);
			panel.drawHitBox(getHitBox(panel));
		}*/
	}
}

