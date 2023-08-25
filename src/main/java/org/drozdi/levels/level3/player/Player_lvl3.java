package org.drozdi.levels.level3.player;

import java.awt.*;
import java.awt.geom.Point2D;

import lombok.Getter;
import lombok.Setter;
import org.drozdi.game.NesnupejteDrozdi;
import org.drozdi.game.Test;
import org.drozdi.levels.level3.Bullet;
import org.drozdi.levels.level3.FileManager_lvl3;
import org.drozdi.levels.level3.Panel_level3;
import org.drozdi.levels.level3.Wall;
import org.drozdi.levels.level3.walls.*;

public class Player_lvl3 {
	private final Panel_level3 panel;
	@Getter @Setter
	private Point position;
	@Getter @Setter
	private Point2D.Double size;
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
	private Direction direction = Direction.RIGHT;
	@Getter @Setter
	private int shot = 0;
	@Getter @Setter
	private boolean shooting = false;

	public Player_lvl3(Point screenPosition, Panel_level3 panel) {
		this.panel = panel;
		size = new Point2D.Double((0.9 * panel.getCellSize()), (0.9 * panel.getCellSize()));
		speed = new Point2D.Double(0, 0);
		if (screenPosition == null) {
			position = new Point((int) (panel.getX() + panel.getWidth() / 2 - size.x / 2),
					(int) (panel.getY() + panel.getHeight() / 2 - size.y / 2));
		} else {
			position = screenPosition;
		}
		hitBox = new Rectangle(position.x, position.y, (int) size.x, (int) size.y);
	}

	public void setUp() {
		double srovanani = (double) panel.getCellSize() /30;
		if (onGround) {
			speedMax = new Point2D.Double(6*srovanani, 6*srovanani);
		} else {
			speedMax = new Point2D.Double(4*srovanani, 6*srovanani);
		}
		// setUpeni pohybu X
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

		// x collision
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
				direction = Direction.UP;
				if (up) {
					speed.y = -speedMax.y;
				} else if (down) {
					speed.y = speedMax.y / 2;
				} else {
					speed.y = 0;
				}
			}
		}

		// y collision
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

		if (speed.x > 0) {
			direction = Direction.RIGHT;
		}
		if (speed.x < 0) {
			direction = Direction.LEFT;
		}
		
		
		position.y += speed.y;

		hitBox.x = position.x;
		hitBox.y = position.y;

		panel.getShift().x += speed.x;
		panel.getShift().y += speed.y;
		if (down) {
			direction = Direction.DOWN;
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
			hedgehog.collisionControl(this);
		}
		// zjistuje zda je na checkpointu
		for (Checkpoint checkpoint : panel.getCheckpoints()) {
			checkpoint.collisionControl(this);
		}
		//zda šel na door
		for (Door door : panel.getDoors()) {
			door.collisionControl(this);
		}
	}

	public void draw(Graphics2D g2d) {
		g2d.setColor(Color.cyan);
		FontMetrics fm = g2d.getFontMetrics();
	    int x = (int) (position.x + size.x /2 -(fm.stringWidth(NesnupejteDrozdi.account)) / 2);
		g2d.drawString(NesnupejteDrozdi.account, x, position.y -10);
		g2d.drawString((position.x + panel.getShift().x) + ";" + (position.y + panel.getShift().y) + "(" + panel.getPlayerShots().size()
			+ ")" + "(" + panel.getEntityShots().size() + ")\n+ " + "(" + onGround + ")", position.x, position.y);
		switch (direction){
			case UP -> drawPlayer(g2d,FileManager_lvl3.playerUp);
			case RIGHT -> drawPlayer(g2d,FileManager_lvl3.playerRight);
			case DOWN -> drawPlayer(g2d,FileManager_lvl3.playerDown);
			case LEFT -> drawPlayer(g2d,FileManager_lvl3.playerLeft);
		}

		if (Test.isHitBoxPlayer()) {
			g2d.setColor(Color.green);
			g2d.draw(hitBox);
		}

	}

	public void drawPlayer(Graphics2D g2d, Image image){
		g2d.drawImage(image, position.x, position.y, (int) size.x, (int) size.y, null);
	}

	public synchronized void shot() {
		shot++;
		if (shot > 20 && (shooting || direction == Direction.DOWN)) {
			shot = 0;
			panel.getPlayerShots().add(new Bullet(panel, this));
		}
	}

}
