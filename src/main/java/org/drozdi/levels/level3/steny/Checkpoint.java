package org.drozdi.levels.level3.steny;

import org.drozdi.levels.level3.FileManager_lvl3;
import org.drozdi.levels.level3.Panel_level3;
import org.drozdi.levels.level3.Player_lvl3;
import org.drozdi.levels.level3.Wall;

import java.awt.Graphics2D;
import java.awt.Rectangle;

public class Checkpoint extends Wall {
	public Checkpoint(int x, int y, int sizeX, int sizeY, Panel_level3 panel) {
		super(x, y, sizeX, sizeY, panel);
	}

	public void nastav() {
		setHitBox(new Rectangle(getPosition().x - getPanel().getShift().x, getPosition().y - 1, getSize().x, getSize().y + 1));
	}

	public void kontrolaKolize(Player_lvl3 player) {
		if (player.getHitBox().intersects(getHitBox())) {
			//panel.getLevel3().shift.x =   panel.getLevel3().shift.x + (getHitBox().x - player.getHitBox().x)
			getPanel().getLevel3().getShift().x = getPanel().getShift().x+ (getHitBox().x - player.getHitBox().x);
			getPanel().getLevel3().getDefaultPosition().x = getPosition().x / getPanel().getCellSize();
			getPanel().getLevel3().getScreenPosition().y = getPosition().y / getPanel().getCellSize();
		}
	}

	public void draw(Graphics2D g2d, Rectangle r) {
		if (getHitBox().intersects(r)) {
			g2d.drawImage(FileManager_lvl3.checkpoint, getHitBox().x, getHitBox().y, getSize().x, getSize().y, null);
		}
	}

}
