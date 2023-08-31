package org.drozdi.levels.level3.walls;

import org.drozdi.levels.level3.FileManager_lvl3;
import org.drozdi.levels.level3.Panel_level3;
import org.drozdi.levels.level3.Wall;


public class Key extends Wall {
	public Key(int x, int y) {
		super(x, y);
	}
	@Override
	public void draw(Panel_level3 panel) {
		drawOnScreen(FileManager_lvl3.key, panel);
	}
}
