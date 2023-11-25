package drozdi.levels.level3.walls;

import FileManager;
import Panel;
import drozdi.levels.level3.Wall;


public class Key extends Wall {
	public Key(int x, int y) {
		super(x, y);
	}
	@Override
	public void draw(GamePanel panel) {
		drawOnScreen(FileManager_lvl3.key, panel);
	}
}
