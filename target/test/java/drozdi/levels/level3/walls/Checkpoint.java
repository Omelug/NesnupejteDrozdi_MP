package drozdi.levels.level3.walls;

import FileManager;
import Panel;
import drozdi.levels.level3.Wall;
import drozdi.levels.level3.client.PlayerMP;
import drozdi.game.Test;

import java.awt.*;
import java.awt.geom.Rectangle2D;

public class Checkpoint extends Wall {
	public Checkpoint(int x, int y) {
		super(x, (int) (y + 0.92), 1, 0.05);
	}

	@Override
	public Rectangle2D.Double getHitBox(GamePanel panel) {
		return new Rectangle2D.Double( (getPosition().x - panel.getShift().x), getPosition().y - 1, (int) getSize().x, (int) (getSize().y + 1));
	}
	@Override
	public Rectangle2D.Double getHitBoxServer() {
		return new Rectangle2D.Double (getPosition().x, getPosition().y - 1, getSize().x, (getSize().y + 1));
	}


	public void collisionControl(PlayerMP player) {
		if (player.getHitBoxServer().intersects(getHitBoxServer())) {
			//TODO getPanel().setInfo("Checkpoint pressed");
			//getPanel().getDefaultPosition().x = getPosition().x / getPanel().getCellSize();
			player.setCheckpoint(this);
		}
	}

	public void draw(GamePanel panel) {
		drawOnScreen(FileManager_lvl3.checkpoint, panel);

		if (Test.isHitBoxCheckpoint()) {
			panel.getG2d().setColor(Color.red);
			panel.drawHitBox(getHitBox(panel));
		}
	}

}
