package org.drozdi.levels.level3.walls;

import org.drozdi.game.Test;
import org.drozdi.levels.level3.Bullet;
import org.drozdi.levels.level3.FileManager_lvl3;
import org.drozdi.levels.level3.Panel_level3;
import org.drozdi.levels.level3.Wall;
import org.drozdi.levels.level3.client.PlayerMP;
import org.drozdi.levels.level3.server.HitBoxHelper;

import java.awt.*;

public class Tower extends Wall {
	public static final double MAX_DISTANCE = 200; //TODO check
	int shot = 0;

	public Tower(int x, int y) {
		super(x, y, 2, 2);
	}

	@Override
	public Rectangle getHitBox(Panel_level3 panel) {
		return new Rectangle((int) (getPosition().x - panel.getShift().x + 1/2),
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
	public void draw(Panel_level3 panel) {
		Rectangle hitBox = getHitBox(panel);
		if (panel.getScreen().intersects(hitBox)) { //TODO hitbox je maly, takze to bude platit vzdy, zkontrolovat u dalsich
			panel.getG2d().drawImage( FileManager_lvl3.tower,
					(int) (getHitBox(panel).x * panel.getCellSize() - (0.5 * panel.getCellSize())),
					(getHitBox(panel).y) * panel.getCellSize(),
					(int) getSize().x * panel.getCellSize(),
					(int) getSize().y * panel.getCellSize(), null);

			if (Test.isHitBoxTower()) {
				panel.getG2d().setColor(Color.green);
				panel.drawHitBox(getHitBox(panel));
			}
		}
	}
	public void drawLine(PlayerMP player, Panel_level3 panel) {
		panel.getG2d().setColor(Color.orange);
		panel.getG2d().drawLine(
				(int) ((player.getPosition().x + player.getSize().x / 2) * panel.getCellSize()),
				(int) ((player.getPosition().y + player.getSize().y / 2) * panel.getCellSize()),
				(int) (((getPosition().x + 0.5)* panel.getCellSize() - panel.getShift().x)),
				(getPosition().y + 1) * panel.getCellSize());
	}
}
