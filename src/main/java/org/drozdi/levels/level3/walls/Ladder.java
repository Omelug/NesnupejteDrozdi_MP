package org.drozdi.levels.level3.walls;

import org.drozdi.levels.level3.FileManager_lvl3;
import org.drozdi.levels.level3.Panel_level3;
import org.drozdi.levels.level3.Wall;

public class Ladder extends Wall {

	public Ladder(int x, int y, int sizeX, int sizeY, Panel_level3 panel) {
		super(x, y, sizeX, sizeY, panel);
	}

	@Override
	public void draw() {
		drawOnScreen(FileManager_lvl3.ladder);
	}
}