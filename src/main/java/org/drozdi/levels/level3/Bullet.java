package org.drozdi.levels.level3;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.util.Objects;

import lombok.Data;
import org.drozdi.game.Test;
import org.drozdi.levels.level3.steny.Slug;
import org.drozdi.levels.level3.steny.Tower;

@Data
public class Bullet {
	public static Point size = new Point(20, 20);;
	public Point2D.Double position;
	private Panel_level3 panel;
	Point2D.Double speed;
	static int bulletSpeed = 10;
	int typStrely; // 1 je od hrace, 2 je od towers
	public Rectangle hitBox;

	public Bullet(Panel_level3 panel, Player_lvl3 player) {
		typStrely = 1;
		this.panel = panel;
		position = new Point2D.Double(player.getPosition().x + panel.getShift().x + (double) player.getSize().x / 2 - (double) size.x / 2,
				player.getPosition().y + (double) player.getSize().y / 2 - (double) size.y / 2);
		if (player.getDirection() == 2) {
			speed = new Point2D.Double(0, bulletSpeed);
		} else if (player.getDirection() == 1) {
			speed = new Point2D.Double(bulletSpeed, 0);
		} else if (player.getDirection() == 0) {
			speed = new Point2D.Double(0, -bulletSpeed);
		} else if (player.getDirection() == 3) {
			speed = new Point2D.Double(-bulletSpeed, 0);
		}
		hitBox = new Rectangle((int) (position.x - panel.getShift().x), (int) (position.y), size.x, size.y);
	}

	public Bullet(Panel_level3 panel, Tower tower, Player_lvl3 player) {
		typStrely = 2;
		this.panel = panel;
		position = new Point2D.Double(tower.getPosition().x + panel.getCellSize() - (double) size.x / 2,
				tower.getPosition().y + panel.getCellSize() - (double) size.y / 2);
		double x = player.getHitBox().x + (double) player.getSize().x / 2 - (tower.getHitBox().x + (double) tower.getHitBox().width / 2);
		double y = player.getHitBox().y + (double) player.getSize().y / 2 - (tower.getHitBox().y + (double) tower.getHitBox().height / 2);
		double pomer = (bulletSpeed / Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2)));
		speed = new Point2D.Double.Double(pomer * x, pomer * y);
		hitBox = new Rectangle((int) (position.x - panel.getShift().x), (int) (position.y), size.x, size.y);
	}

	public Bullet(Panel_level3 panel, Slug slug, Player_lvl3 player) {
		typStrely = 3;
		this.panel = panel;
		position = new Point2D.Double(slug.getPosition().x + panel.getCellSize() - (double) size.x / 2,
				slug.getPosition().y + panel.getCellSize() - size.y );
		double x = player.getHitBox().x + (double) player.getSize().x / 2 - (slug.getHitBox().x + (double) slug.getHitBox().width / 2);
		double y = player.getHitBox().y - (slug.getHitBox().y + (double) slug.getHitBox().height / 2);
		double pomer = (bulletSpeed / Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2)));
		speed = new Point2D.Double.Double(pomer * x*1.25, pomer * y*1.25);
		hitBox = new Rectangle((int) (position.x - panel.getShift().x), (int) (position.y), size.x, size.y);
	}

	void nastav() {

		position.x = position.x + speed.x;
		position.y = position.y + speed.y;

		/**for (Wall wall : panel.getWallsOnScreen()) {
			if (wall.getHitBox().intersects(hitBox)) {
				switch (typStrely) {
					case 1 -> {
						boolean test = panel.getPlayerShots().remove(this);
						System.out.println(""+ test);
					}
					case 3 -> {
						panel.getEntityShots().remove(this);
					}
				}
			}
		}**/
		hitBox = new Rectangle((int) (position.x - panel.getShift().x), (int) (position.y), size.x, size.y);
	}


	void draw(Graphics2D g2d, Rectangle r) {
		g2d.setColor(Color.cyan);
		if (typStrely == 1) {
			g2d.drawImage(FileManager_lvl3.bullet, hitBox.x, hitBox.y, size.x, size.y, null);
		} else if (typStrely == 2) {
			g2d.setColor(Color.red);
			g2d.fillRect(hitBox.x, hitBox.y, size.x, size.y);
		}
		else if (typStrely == 3) {
			g2d.setColor(Color.cyan);
			g2d.fillRect(hitBox.x, hitBox.y, size.x, size.y);
		}
		if (Test.isHitBoxBullets()) {
			g2d.setColor(Color.green);
			g2d.draw(hitBox);
		}
	}
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Bullet bullet = (Bullet) o;
		return Objects.equals(position, bullet.position) &&
				Objects.equals(panel, bullet.panel) &&
				Objects.equals(speed, bullet.speed) &&
				typStrely == bullet.typStrely;
	}
}
