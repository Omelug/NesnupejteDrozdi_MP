package drozdi.gameobjects.walls;


import drozdi.FileManager;
import drozdi.clientside.Panel;

public class Ladder extends Wall {
	public Ladder(int x, int y) {
		super(x, y);
	}
	@Override
	public void draw(Panel panel) {
		drawOnScreen(FileManager.ladder, panel);
	}
}