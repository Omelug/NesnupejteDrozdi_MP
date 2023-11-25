package drozdi.levels.level3.walls;

import drozdi.levels.level3.Bullet;
import FileManager;
import drozdi.levels.level3.Wall;
import drozdi.levels.level3.client.PlayerMP;
import drozdi.levels.level3.server.HitBoxHelper;
import drozdi.game.Test;
import Panel;

import java.awt.*;
import java.awt.geom.Rectangle2D;

public class Tower extends Wall {
	public static final double MAX_DISTANCE = 200; //TODO check
	int shot = 0;

	public Tower(int x, int y) {
		super(x, y, 2, 2);
	}

	@Override
	public Rectangle2D.Double getHitBox(GamePanel panel) {
		return new Rectangle2D.Double ((getPosition().x - panel.getShift().x + (double) 1/2),
				getPosition().y, (int) (getSize().x- 1), (int) getSize().y);
	}

	public void shot(PlayerMP nearestPlayer, HitBoxHelper hitBoxHelper) {
		shot++;
		if (shot > 80) {
			shot = 0;
			hitBoxHelper.getMapHelper().getEntityShots().add(new Bullet(this, nearestPlayer));
		}
	}
	@Override
	public void draw(GamePanel panel) {
		Rectangle2D.Double hitBox = getHitBox(panel);
		if (panel.getScreen().intersects(hitBox)) { //TODO hitbox je maly, takze to bude platit vzdy, zkontrolovat u dalsich
			panel.getG2d().drawImage(FileManager_lvl3.tower,
							(int) (getHitBox(panel).x * panel.getCellSize() - (0.5 * panel.getCellSize())),
							(int) ((getHitBox(panel).y) * panel.getCellSize()),
							(int) getSize().x * panel.getCellSize(),
							(int) getSize().y * panel.getCellSize(), null);

			if (Test.isHitBoxTower()) {
				panel.getG2d().setColor(Color.green);
				panel.drawHitBox(getHitBox(panel));
			}
		}
	}
	public void drawLine(PlayerMP player, GamePanel panel) {
		panel.getG2d().setColor(Color.orange);
		panel.getG2d().drawLine(
				(int) ((player.getPosition().x + player.getSize().x / 2) * panel.getCellSize()),
				(int) ((player.getPosition().y + player.getSize().y / 2) * panel.getCellSize()),
				(int) (((getPosition().x + 0.5)* panel.getCellSize() - panel.getShift().x)),
				(getPosition().y + 1) * panel.getCellSize());
	}
}
