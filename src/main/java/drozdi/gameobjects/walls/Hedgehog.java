package drozdi.gameobjects.walls;


import drozdi.FileManager;
import drozdi.clientside.Panel;
import drozdi.gameobjects.GameCubeFloat;
import drozdi.gameobjects.Player.Player;

public class Hedgehog extends Wall {
	public Hedgehog(int x, int y) {
		super(x, y);
	}
	@Override
	public void draw(Panel panel) {
		drawOnScreen(FileManager.hedgehog, panel);
	}

}
