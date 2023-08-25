package org.drozdi.levels.level3.steny;


import org.drozdi.levels.level3.FileManager_lvl3;
import org.drozdi.levels.level3.Panel_level3;
import org.drozdi.levels.level3.Player_lvl3;
import org.drozdi.levels.level3.Wall;
import java.awt.Graphics2D;
import java.awt.Rectangle;

public class Hedgehog extends Wall {
	public Hedgehog(int x, int y, int sizeX, int sizeY, Panel_level3 panel) {
		super(x, y, sizeX, sizeY, panel);
	}

	public void draw(Graphics2D g2d, Rectangle r) {
		if (getHitBox().intersects(r)) {
			g2d.drawImage(FileManager_lvl3.hedgehog, getHitBox().x, getHitBox().y, getSize().x, getSize().y, null);
		}
	}

	public void kontrolaKolize(Player_lvl3 player) {
		if (player.getHitBox().intersects(getHitBox())) {
			player.setHitBox(new Rectangle(0, 0));
			getPanel().restart();
		}
	}

}
