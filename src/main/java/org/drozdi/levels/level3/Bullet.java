package org.drozdi.levels.level3;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import lombok.Data;
import org.drozdi.game.Test;
import org.drozdi.levels.level3.player.Player_lvl3;
import org.drozdi.levels.level3.walls.Slug;
import org.drozdi.levels.level3.walls.Tower;

@Data
public class Bullet {
	public static Point size = new Point(20, 20);
	public Point2D.Double position;
	private Panel_level3 panel;
	private Point2D.Double speed;
	private static int bulletSpeed = 10;
	private BulletType bulletType;
	private Rectangle hitBox;

	public Bullet(Panel_level3 panel, Player_lvl3 player) {
		bulletType = BulletType.PLAYER;
		this.panel = panel;
		position = new Point2D.Double(player.getPosition().x + panel.getShift().x + player.getSize().x / 2 - (double) size.x / 2,
				player.getPosition().y + player.getSize().y / 2 - (double) size.y / 2);
		switch (player.getDirection()){
			case UP -> speed = new Point2D.Double(0, -bulletSpeed);
			case RIGHT -> speed = new Point2D.Double(bulletSpeed, 0);
			case DOWN -> speed = new Point2D.Double(0, bulletSpeed);
			case LEFT ->speed = new Point2D.Double(-bulletSpeed, 0);
		}
		hitBox = new Rectangle((int) (position.x - panel.getShift().x), (int) (position.y), size.x, size.y);
	}

	public Bullet(Panel_level3 panel, Tower tower, Player_lvl3 player) {
		bulletType = BulletType.TOWER;
		this.panel = panel;
		position = new Point2D.Double(tower.getPosition().x + panel.getCellSize() - (double) size.x / 2,
				tower.getPosition().y + panel.getCellSize() - (double) size.y / 2);
		double x = player.getHitBox().x + player.getSize().x / 2 - (tower.getHitBox().x + (double) tower.getHitBox().width / 2);
		double y = player.getHitBox().y + player.getSize().y / 2 - (tower.getHitBox().y + (double) tower.getHitBox().height / 2);
		double proportion = (bulletSpeed / Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2)));
		speed = new Point2D.Double.Double(proportion * x, proportion * y);
		hitBox = new Rectangle((int) (position.x - panel.getShift().x), (int) (position.y), size.x, size.y);
	}

	public Bullet(Panel_level3 panel, Slug slug, Player_lvl3 player) {
		bulletType = BulletType.SLUG;
		this.panel = panel;
		position = new Point2D.Double(slug.getPosition().x + panel.getCellSize() - (double) size.x / 2,
				slug.getPosition().y + panel.getCellSize() - size.y );
		double x = player.getHitBox().x + player.getSize().x / 2 - (slug.getHitBox().x + (double) slug.getHitBox().width / 2);
		double y = player.getHitBox().y - (slug.getHitBox().y + (double) slug.getHitBox().height / 2);
		double proportion = (bulletSpeed / Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2)));
		speed = new Point2D.Double.Double(proportion * x*1.25, proportion * y*1.25);
		hitBox = new Rectangle((int) (position.x - panel.getShift().x), (int) (position.y), size.x, size.y);
	}

	void setUp() {

		position.x = position.x + speed.x;
		position.y = position.y + speed.y;

		hitBox = new Rectangle((int) (position.x - panel.getShift().x), (int) (position.y), size.x, size.y);
	}


	void draw(Graphics2D g2d) {
		g2d.setColor(Color.cyan);
		switch (bulletType) {
			case PLAYER -> g2d.drawImage(FileManager_lvl3.bullet, hitBox.x, hitBox.y, size.x, size.y, null);
			case TOWER -> {
				g2d.setColor(Color.red);
				g2d.fillRect(hitBox.x, hitBox.y, size.x, size.y);
			}
			case SLUG -> {
				g2d.setColor(Color.cyan);
				g2d.fillRect(hitBox.x, hitBox.y, size.x, size.y);
			}
		}

		if (Test.isHitBoxBullets()) {
			g2d.setColor(Color.green);
			g2d.draw(hitBox);
		}
	}
}
