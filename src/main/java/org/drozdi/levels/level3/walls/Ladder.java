package org.drozdi.levels.level3.walls;

import org.drozdi.levels.level3.FileManager_lvl3;
import org.drozdi.levels.level3.GamePanel;
import org.drozdi.levels.level3.Wall;

public class Ladder extends Wall {
	public Ladder(int x, int y) {
		super(x, y);
	}

	@Override
	public void draw(GamePanel panel) {
		drawOnScreen(FileManager_lvl3.ladder, panel);
	}
}