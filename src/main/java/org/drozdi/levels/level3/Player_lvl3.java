package org.drozdi.levels.level3;

import java.awt.Point;
import java.awt.Color;
import java.awt.geom.Point2D;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.drozdi.game.NesnupejteDrozdi;
import org.drozdi.game.Test;
import org.drozdi.levels.level3.steny.*;

import java.awt.Rectangle;
import java.awt.Graphics2D;
import java.awt.FontMetrics;

public class Player_lvl3 {
	private Panel_level3 panel;
	@Getter @Setter
	private Point position;
	@Getter @Setter
	private Point size;
	@Getter @Setter
	private Point2D.Double speed;
	@Getter @Setter
	private Point2D.Double speedMax = new Point2D.Double(4, 6);
	@Getter @Setter
	private Rectangle hitBox;

	@Getter @Setter
	private boolean left, right, up, down;
	@Getter @Setter
	private boolean onGround = false;
	@Getter @Setter
	private int direction = 1; // 0- nahoru, 1- pravo, 2 - dolu , 3 - levo
	@Getter @Setter
	private int shot = 0;
	@Getter @Setter
	private boolean shooting = false;

	public Player_lvl3(Point screenPosition, Panel_level3 panel) {
		this.panel = panel;
		size = new Point((int) (0.9 * panel.getCellSize()), (int) (0.9 * panel.getCellSize()));
		speed = new Point2D.Double(0, 0);
		if (screenPosition == null) {
			position = new Point(panel.getX() + panel.getWidth() / 2 - size.x / 2,
					panel.getY() + panel.getHeight() / 2 - size.y / 2);
		} else {
			position = screenPosition;
		}
		hitBox = new Rectangle(position.x, position.y, size.x, size.y);
	}

	public void nastav() {
		double srovanani = (double) panel.getCellSize() /30;
		if (onGround) {
			speedMax = new Point2D.Double(6*srovanani, 6*srovanani);
		} else {
			speedMax = new Point2D.Double(4*srovanani, 6*srovanani);
		}
		// nastaveni pohybu X
		if (left && right || !left && !right)
			speed.x *= 0.8;
		else if (left)
			speed.x = speed.x -1*srovanani;
		else speed.x = speed.x + 1*srovanani;
		;
		///speed.x = speed.x*srovanani;
		//limitovani ryvhlosti X
		if (speed.x < 0.1*srovanani && speed.x > -0.1*srovanani) {
			speed.x = 0;
		}
		if (speed.x > speedMax.x) {
			speed.x = speedMax.x;
		} else if (speed.x < -speedMax.x) {
			speed.x = -speedMax.x;
		}

		if (up && onGround) {
			speed.y = -1.5*speedMax.y;
			onGround = false;
		}
		speed.y += 0.3*srovanani;
		// x kolize
		//speed.x = speed.x*srovanani;
		hitBox.x += speed.x;
		for (Wall wall : panel.getWallsOnScreen()) {
			if (hitBox.intersects(wall.getHitBox())) {
				hitBox.x -= speed.x;
				while (!wall.getHitBox().intersects(hitBox))
					hitBox.x += Math.signum(speed.x);
				hitBox.x -= Math.signum(speed.x);
				speed.x = 0;
				position.x = hitBox.x;
			}
		}
		// ladders
		onGround = false;
		for (Ladder ladder : panel.getLadders()) {
			if (hitBox.intersects(ladder.getHitBox())) {
				onGround = true;
				direction = 0;
				if (up) {
					speed.y = -speedMax.y;
				} else if (down) {
					speed.y = speedMax.y / 2;
				} else {
					speed.y = 0;
				}
			}
		}
		// y kolize
		//speed.y = speed.y*srovanani;
		hitBox.y += speed.y;
		for (Wall wall : panel.getWallsOnScreen()) {
			if (hitBox.intersects(wall.getHitBox())) {
				hitBox.y -= speed.y;
				while (!wall.getHitBox().intersects(hitBox))
					hitBox.y += Math.signum(speed.y);
				hitBox.y -= Math.signum(speed.y);
				if (speed.y > 0)
					onGround = true;
				speed.y = 0;
				position.y = hitBox.y;
			}
		}
		// nasteveni direction
		if (speed.x > 0) {
			direction = 1;
		}
		if (speed.x < 0) {
			direction = 3;
		}
		
		
		position.y += speed.y;

		hitBox.x = position.x;
		hitBox.y = position.y;

		panel.getShift().x += speed.x;
		panel.getShift().y += speed.y;
		if (down) {
			direction = 2;
		}
		shot();
		// zjistuje zda byl zasazena
		for (Bullet bullet : panel.getEntityShots()) {
			if (hitBox.intersects(bullet.getHitBox())) {
				panel.restart();
			}
		}
		// zjistuje zda dopad na hedgehog
		for (Hedgehog hedgehog : panel.getHedgehogs()) {
			hedgehog.kontrolaKolize(this);
		}
		// zjistuje zda je na checkpointu
		for (Checkpoint checkpoint : panel.getCheckpoints()) {
			checkpoint.kontrolaKolize(this);
		}
		//zda šel na door
		for (Door door : panel.getDoors()) {
			door.kontrolaKolize(this);
		}
	}

	@SuppressWarnings("unused")
	public void draw(Graphics2D g2d) {
		g2d.setColor(Color.cyan);
		FontMetrics fm = g2d.getFontMetrics();
	    int x = position.x + size.x /2 -(fm.stringWidth(NesnupejteDrozdi.account)) / 2;
		g2d.drawString(NesnupejteDrozdi.account, x, position.y -10);
		//g2d.drawString((position.x + panel.shift.x) + ";" + (position.y + panel.shift.y) + "(" + panel.playerShots.size()
		//		+ ")" + "(" + panel.entityShots.size() + ")\n+ " + "(" + onGround + ")", position.x, position.y);
		if (direction == 1) {
			g2d.drawImage(FileManager_lvl3.playerRight, position.x, position.y, size.x, size.y, null);
		} else if (direction == 3) {
			g2d.drawImage(FileManager_lvl3.playerLeft, position.x, position.y, size.x, size.y, null);
		} else if (direction == 0) {
			g2d.drawImage(FileManager_lvl3.playerUp, position.x, position.y, size.x, size.y, null);
		} else if (direction == 2) {
			g2d.drawImage(FileManager_lvl3.playerDown, position.x, position.y, size.x, size.y, null);
		}
		// hitbox
		if (Test.isHitBoxPlayer()) {
			g2d.setColor(Color.green);
			g2d.draw(hitBox);
		}

	}

	public synchronized void shot() {
		shot++;
		if (shot > 20 && (shooting || direction == 2)) {
			shot = 0;
			panel.getPlayerShots().add(new Bullet(panel, this));
		}
	}

}
