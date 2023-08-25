package org.drozdi.levels.level3.walls;

import org.drozdi.game.Test;
import org.drozdi.levels.level3.*;
import org.drozdi.levels.level3.player.Player_lvl3;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;

public class Slug extends Wall {
	Panel_level3 panel;
	int shoot = 0;

	public Slug(int x, int y, int sizeX, int sizeY, Panel_level3 panel) {
		super(x, y, sizeX*2, sizeY, panel);
		setHitBox(new Rectangle(getPosition().x - panel.getShift().x+panel.getCellSize(), getPosition().y, getSize().x-panel.getCellSize(), getSize().y));
		this.panel = panel;
	}

	public void setUp(Player_lvl3 player) {
	  	setHitBox(new Rectangle(getPosition().x - panel.getShift().x, getPosition().y, getSize().x, getSize().y));
		if (getHitBox().intersects(panel.getScreen())) {
			shoot++;
			if (shoot > 50) {
				shoot = 0;
				panel.getEntityShots().add(new Bullet(panel, this, player));
			}
		}
	}

	public void draw(Graphics2D g2d, Rectangle r) {
		if (getHitBox().intersects(r)) {
			g2d.drawImage(FileManager_lvl3.slug,	getHitBox().x, getHitBox().y, getSize().x, getSize().y, null);
		}
		if (Test.isHitBoxTower()) {
			g2d.setColor(Color.green);
			g2d.draw(getHitBox());
		}
	}
}
