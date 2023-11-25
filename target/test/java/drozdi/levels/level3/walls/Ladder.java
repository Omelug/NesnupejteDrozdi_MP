package drozdi.levels.level3.walls;

import FileManager;
import Panel;
import drozdi.levels.level3.Wall;

public class Ladder extends Wall {
	public Ladder(int x, int y) {
		super(x, y);
	}

	@Override
	public void draw(GamePanel panel) {
		drawOnScreen(FileManager_lvl3.ladder, panel);
	}
}