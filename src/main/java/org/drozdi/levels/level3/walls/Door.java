package org.drozdi.levels.level3.walls;

import org.drozdi.levels.level3.FileManager_lvl3;
import org.drozdi.levels.level3.Panel_level3;
import org.drozdi.levels.level3.player.Player_lvl3;
import org.drozdi.levels.level3.Wall;
import java.awt.Point;

public class Door extends Wall {
	public int maxKeys;
	public int open;

	public Door(int x, int y, int sizeX, int sizeY, Panel_level3 panel, int max) {
		super(x, y, sizeX, sizeY, panel);
		this.maxKeys = max;
		open = this.maxKeys;
	}

	public void setUp(Panel_level3 panel) {
		setUp();
		open = maxKeys - panel.getLevel3().getKeyCount();
	}

	public void collisionControl(Player_lvl3 player) {
		if (open <= 0) {
			if (player.getHitBox().intersects(getHitBox())) {
				getPanel().getLevel3().saveTime();
				getPanel().getLevel3().setDeathCount(-1);
				getPanel().getLevel3().setSavedKeyList(null);
				getPanel().getLevel3().setDeathCount(-1);
				getPanel().getLevel3().setKeyCount(0);
				getPanel().getLevel3().setEnd(false);
				Point p = new Point((Point) getPanel().getShift().clone());
				p.x = 0;
				getPanel().setShift(p);
				getPanel().setDefaultScreenPosition ((Point) getPanel().getDefaultScreenPosition().clone());
				getPanel().end();
			}
		}

	}

	public void draw() {
		if (getPanel().getScreen().intersects(getHitBox())) {
			if (open > 0) {
				getPanel().getG2d().drawImage(FileManager_lvl3.door, getHitBox().x, getHitBox().y, getHitBox().x, getHitBox().y, null);
			}else {
				getPanel().getG2d().drawImage(FileManager_lvl3.doorOpen, getHitBox().x, getHitBox().y, getHitBox().x, getHitBox().y, null);
			}
		}
	}
}
