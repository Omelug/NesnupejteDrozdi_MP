package org.drozdi.levels.level3.steny;

import org.drozdi.levels.level3.FileManager_lvl3;
import org.drozdi.levels.level3.Panel_level3;
import org.drozdi.levels.level3.Player_lvl3;
import org.drozdi.levels.level3.Wall;

import java.awt.Graphics2D;
import java.awt.Rectangle;

public class Key extends Wall {
	public Key(int x, int y, int sizeX, int sizeY, Panel_level3 panel) {
		super(x, y, sizeX, sizeY, panel);
	}
	public void kontrolaKolize(Player_lvl3 player) {
		if (player.getHitBox().intersects(getHitBox())) {
			getPanel().getKeys().remove(this);
			getPanel().getLevel3().setKeyCount(getPanel().getLevel3().getKeyCount()+1);
		}
	}

	public void draw(Graphics2D g2d, Rectangle r) {
		if (getHitBox().intersects(r)) {
			g2d.drawImage(FileManager_lvl3.key, getHitBox().x, getHitBox().y, getSize().x, getSize().y, null);
		}
	}

}
