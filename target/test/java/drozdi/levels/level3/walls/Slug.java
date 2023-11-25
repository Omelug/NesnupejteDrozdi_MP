package drozdi.levels.level3.walls;

import drozdi.levels.level3.Bullet;
import FileManager;
import Panel;
import drozdi.levels.level3.Wall;
import drozdi.levels.level3.client.PlayerMP;
import drozdi.levels.level3.server.HitBoxHelper;
import drozdi.game.Test;

import java.awt.*;
import java.awt.geom.Rectangle2D;

public class Slug extends Wall {
	public static final double MAX_DISTANCE = 200; //TODO control
	int shoot = 0;

	public Slug(int x, int y) {
		super(x, y, 2, 1);
	}

	@Override
	public Rectangle2D.Double getHitBox(GamePanel panel) {
	  	return new Rectangle2D.Double((getPosition().x - panel.getShift().x), getPosition().y, (int) getSize().x, (int) getSize().y);
	}

	@Override
	public void draw(GamePanel panel) {
		drawOnScreen(FileManager_lvl3.slug, panel);
		if (Test.isHitBoxTower()) {
			panel.getG2d().setColor(Color.green);
			panel.drawHitBox(getHitBox(panel));
		}
	}

    public void drawLine(PlayerMP player, GamePanel panel) {
		panel.getG2d().setColor(Color.red);
		panel.getG2d().drawLine((int) (player.getPosition().x + player.getSize().x / 2), (int) (player.getPosition().y + player.getSize().y / 2),
				(int) (getPosition().x + panel.getCellSize() - panel.getShift().x), getPosition().y + panel.getCellSize());
    }

    public void shot(PlayerMP nearestPlayer, HitBoxHelper hitBoxHelper) {
		shoot++;
		if (shoot > 50) {
			shoot = 0;
			hitBoxHelper.getMapHelper().getEntityShots().add(new Bullet(this, nearestPlayer));
		}
    }
}
