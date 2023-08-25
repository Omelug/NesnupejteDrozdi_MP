package org.drozdi.levels.level3.walls;


import org.drozdi.levels.level3.FileManager_lvl3;
import org.drozdi.levels.level3.Panel_level3;
import org.drozdi.levels.level3.player.Player_lvl3;
import org.drozdi.levels.level3.Wall;

import java.awt.*;


public class Hedgehog extends Wall {
	public Hedgehog(int x, int y, int sizeX, int sizeY, Panel_level3 panel) {
		super(x, y, sizeX, sizeY, panel);
	}

	@Override
	public void draw() {
		drawOnScreen(FileManager_lvl3.hedgehog);
	}

	public void collisionControl(Player_lvl3 player) {
		if (player.getHitBox().intersects(getHitBox())) {
			player.setHitBox(new Rectangle(0, 0));
			getPanel().restart();
		}
	}

}
