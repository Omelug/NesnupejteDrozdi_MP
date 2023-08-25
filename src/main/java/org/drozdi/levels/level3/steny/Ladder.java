package org.drozdi.levels.level3.steny;

import org.drozdi.levels.level3.FileManager_lvl3;
import org.drozdi.levels.level3.Panel_level3;
import org.drozdi.levels.level3.Wall;
import java.awt.Graphics2D;
import java.awt.Rectangle;

public class Ladder extends Wall {

	public Ladder(int x, int y, int sizeX, int sizeY, Panel_level3 panel) {
		super(x, y, sizeX, sizeY, panel);
	}

	public void draw(Graphics2D g2d, Rectangle r) {
		if (getHitBox().intersects(r)) {
			g2d.drawImage(FileManager_lvl3.ladder, getHitBox().x, getHitBox().y, getSize().x, getSize().y, null);
		}
	}
}