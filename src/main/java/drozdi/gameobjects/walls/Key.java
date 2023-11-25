package drozdi.gameobjects.walls;


import drozdi.FileManager;
import drozdi.clientside.Panel;

public class Key extends Wall {
	public Key(int x, int y) {
		super(x, y);
	}

	@Override
	public void draw(Panel panel) {
		drawOnScreen(FileManager.key, panel);
	}
}
