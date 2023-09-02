package org.drozdi.levels.level3.walls;

import org.drozdi.game.Test;
import org.drozdi.levels.level3.FileManager_lvl3;
import org.drozdi.levels.level3.Panel_level3;

import org.drozdi.levels.level3.Wall;
import org.drozdi.levels.level3.client.PlayerMP;

import java.awt.*;
import java.awt.geom.Rectangle2D;

public class Checkpoint extends Wall {
	public Checkpoint(int x, int y) {
		super(x, (int) (y + 0.92), 1, 0.05);
	}

	@Override
	public Rectangle getHitBox(Panel_level3 panel) {
		return new Rectangle((int) (getPosition().x - panel.getShift().x), getPosition().y - 1, (int) getSize().x, (int) (getSize().y + 1));
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

	public void draw(Panel_level3 panel) {
		drawOnScreen(FileManager_lvl3.checkpoint, panel);

		if (Test.isHitBoxCheckpoint()) {
			panel.getG2d().setColor(Color.red);
			panel.drawHitBox(getHitBox(panel));
		}
	}

}
