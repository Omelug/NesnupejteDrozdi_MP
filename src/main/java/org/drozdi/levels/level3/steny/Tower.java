package org.drozdi.levels.level3.steny;

import org.drozdi.game.Test;
import org.drozdi.levels.level3.*;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;

public class Tower extends Wall {
	Panel_level3 panel;
	int shot = 0;

	public Tower(int x, int y, int sizeX, int sizeY, Panel_level3 panel) {
		super(x, y, sizeX * 2, sizeY * 2, panel);
		setHitBox(new Rectangle(getPosition().x - panel.getShift().x+panel.getCellSize()/2, getPosition().y, getSize().x-panel.getCellSize(), getSize().y));
		this.panel = panel;
	}

	public void nastav(Player_lvl3 player) {
		setHitBox(new Rectangle(getPosition().x - panel.getShift().x+panel.getCellSize()/2, getPosition().y, getSize().x-panel.getCellSize(), getSize().y));
		if (getHitBox().intersects(panel.getScreen())) {
			shot++;
			if (shot > 80) {
				//System.out.println("adwawd");
				shot = 0;
				panel.getEntityShots().add(new Bullet(panel, this, player));
			}
			//zjistuje zda byla zasazena
			for (Bullet bullet : panel.getPlayerShots()) {
				if (getHitBox().intersects(bullet.getHitBox())) {
					panel.getPlayerShots().remove(bullet);
					panel.getTowers().remove(this);
				}
			}
		}
		}

	public void draw(Graphics2D g2d, Rectangle r) {
		if (getHitBox().intersects(r)) {
			//g2d.setColor(Color.cyan);
			//g2d.fillRect(hitBox.x, hitBox.y, size.x, size.y);
			g2d.drawImage(FileManager_lvl3.tower,	getHitBox().x - panel.getCellSize()/2, getHitBox().y, getSize().x, getSize().y, null);
			//g2d.setColor(Color.red);
			//g2d.fillRect(getPosition().x - panel.shift.x, getPosition().y, size.x, size.y);
		}
		if (Test.hitBoxTower) {
			g2d.setColor(Color.green);
			g2d.draw(getHitBox());
		}
	}
}
