package org.drozdi.levels.level3.walls;


import org.drozdi.levels.level3.FileManager_lvl3;
import org.drozdi.levels.level3.GamePanel;
import org.drozdi.levels.level3.Wall;
import org.drozdi.levels.level3.client.PlayerMP;


public class Hedgehog extends Wall {
	public Hedgehog(int x, int y) {
		super(x, y);
	}
	@Override
	public void draw(GamePanel panel) {
		drawOnScreen(FileManager_lvl3.hedgehog, panel);
	}

	public void collisionControl(PlayerMP player) {
		if (player.getHitBoxServer().intersects(getHitBoxServer())) {
			player.dead();
		}
	}

}
