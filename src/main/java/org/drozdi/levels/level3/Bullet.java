package org.drozdi.levels.level3;

import lombok.Data;
import org.drozdi.game.Test;
import org.drozdi.levels.level3.client.PlayerMP;
import org.drozdi.levels.level3.walls.Slug;
import org.drozdi.levels.level3.walls.Tower;

import java.awt.*;
import java.awt.geom.Point2D;

@Data
public class Bullet {
	public static Point size = new Point(20, 20);
	public Point2D.Double position;
	private Point2D.Double speed;
	private static int bulletSpeed = 10;
	private BulletType bulletType;

	public Bullet(PlayerMP player) {
		bulletType = BulletType.PLAYER;
		position = new Point2D.Double(
				player.getPosition().x + player.getSize().x / 2 - (double) size.x / 2,
				player.getPosition().y + player.getSize().y / 2 - (double) size.y / 2);
		switch (player.getDirection()){
			case UP -> speed = new Point2D.Double(0, -bulletSpeed);
			case RIGHT -> speed = new Point2D.Double(bulletSpeed, 0);
			case DOWN -> speed = new Point2D.Double(0, bulletSpeed);
			case LEFT -> speed = new Point2D.Double(-bulletSpeed, 0);
		}
	}
	public Bullet(Tower tower, PlayerMP player) {
		bulletType = BulletType.TOWER;
		position = new Point2D.Double(tower.getPosition().x + (double) size.x / 2,
				tower.getPosition().y + (double) size.y * 0.25);
		double x = player.getHitBoxServer().x + player.getSize().x / 2 - (tower.getHitBoxServer().x + (double) tower.getHitBoxServer().width / 2);
		double y = player.getHitBoxServer().y + player.getSize().y / 2 - (tower.getHitBoxServer().y + (double) tower.getHitBoxServer().height / 2);
		double proportion = (bulletSpeed / Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2)));
		speed = new Point2D.Double.Double(proportion * x, proportion * y);
	}

	public Bullet(Slug slug, PlayerMP player) {
		bulletType = BulletType.SLUG;
		position = new Point2D.Double(slug.getPosition().x + (double) size.x / 2,
				slug.getPosition().y + size.y );
		double x = player.getHitBoxServer().x + player.getSize().x / 2 - (slug.getHitBoxServer().x + (double) slug.getHitBoxServer().width / 2);
		double y = player.getHitBoxServer().y - (slug.getHitBoxServer().y + (double) slug.getHitBoxServer().height / 2);
		double proportion = (bulletSpeed / Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2)));
		speed = new Point2D.Double.Double(proportion * x*1.25, proportion * y*1.25);
	}

	public Rectangle getHitBox(GamePanel panel) {
		return new Rectangle((int) (position.x - panel.getShift().x), (int) (position.y), size.x, size.y);
	}
	public Rectangle getHitBoxServer() {
		return new Rectangle((int) position.x, (int) position.y, size.x, size.y);
	}

	public void updatePosition() {
		position.x += speed.x;
		position.y += speed.y;
	}

	void draw(Graphics2D g2d, GamePanel panel) {
		g2d.setColor(Color.cyan);
		switch (bulletType) {
			case PLAYER ->  {
				Rectangle hitBox = getHitBoxServer();
				g2d.drawImage(FileManager_lvl3.bullet, hitBox.x, hitBox.y, size.x, size.y, null);
			}
			case TOWER -> {
				g2d.setColor(Color.red);
				Rectangle hitBox = getHitBoxServer();
				g2d.fillRect(hitBox.x, hitBox.y, size.x, size.y);
			}
			case SLUG -> {
				g2d.setColor(Color.cyan);
				Rectangle hitBox = getHitBoxServer();
				g2d.fillRect(hitBox.x, hitBox.y, size.x, size.y);
			}
		}

		if (Test.isHitBoxBullets()) {
			g2d.setColor(Color.green);
			panel.drawHitBox(getHitBox(panel));
		}
	}
}
