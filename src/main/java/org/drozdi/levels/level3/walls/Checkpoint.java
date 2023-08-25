package org.drozdi.levels.level3.walls;

import org.drozdi.game.Test;
import org.drozdi.levels.level3.FileManager_lvl3;
import org.drozdi.levels.level3.Panel_level3;
import org.drozdi.levels.level3.player.Player_lvl3;
import org.drozdi.levels.level3.Wall;

import java.awt.*;

public class Checkpoint extends Wall {
	public Checkpoint(int x, int y, int sizeX, int sizeY, Panel_level3 panel) {
		super(x, y, sizeX, sizeY, panel);
	}

	public void setUp() {
		setHitBox(new Rectangle((int) (getPosition().x - getPanel().getShift().x), getPosition().y - 1, getSize().x, getSize().y + 1));
	}

	public void collisionControl(Player_lvl3 player) {
		if (player.getHitBox().intersects(getHitBox())) {
			getPanel().setInfo("Checkpoint pressed");
			//getPanel().getShift().x = getPanel().getShift().x+ (getHitBox().x - player.getHitBox().x);
			getPanel().getDefaultPosition().x = getPosition().x / getPanel().getCellSize();
			player.setCheckpoint(this);
			//getPanel().getScreenPosition().y = getPosition().y / getPanel().getCellSize();
		}
	}

	public void draw() {
		drawOnScreen(FileManager_lvl3.checkpoint);

		if (Test.isHitBoxCheckpoint()) {
			getPanel().getG2d().setColor(Color.red);
			getPanel().getG2d().draw(getHitBox());
		}

	}

}
