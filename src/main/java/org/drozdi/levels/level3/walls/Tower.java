package org.drozdi.levels.level3.walls;

import org.drozdi.game.Test;
import org.drozdi.levels.level3.*;
import org.drozdi.levels.level3.player.Player_lvl3;

import java.awt.Color;
import java.awt.Rectangle;

public class Tower extends Wall {
	Panel_level3 panel;
	int shot = 0;

	public Tower(int x, int y, int sizeX, int sizeY, Panel_level3 panel) {
		super(x, y, sizeX * 2, sizeY * 2, panel);
		setHitBox(new Rectangle((int) (getPosition().x - panel.getShift().x+panel.getCellSize()/2), getPosition().y, getSize().x-panel.getCellSize(), getSize().y));
		this.panel = panel;
	}

	public void setUp(Player_lvl3 player) {
		setHitBox(new Rectangle((int) (getPosition().x - panel.getShift().x+panel.getCellSize()/2), getPosition().y, getSize().x-panel.getCellSize(), getSize().y));
		if (getHitBox().intersects(panel.getScreen())) {
			shot++;
			if (shot > 80) {
				shot = 0;
				panel.getEntityShots().add(new Bullet(panel, this, player));
			}
		}
	}
	@Override
	public void draw() {
		getPanel().getG2d().drawImage(FileManager_lvl3.tower,	getHitBox().x - panel.getCellSize()/2, getHitBox().y, getSize().x, getSize().y, null);
		if (Test.isHitBoxTower()) {
			getPanel().getG2d().setColor(Color.green);
			getPanel().getG2d().draw(getHitBox());
		}
	}
	public void drawLine(Player_lvl3 player) {
		getPanel().getG2d().setColor(Color.orange);
		getPanel().getG2d().drawLine((int) (player.getPosition().x + player.getSize().x / 2), (int) (player.getPosition().y + player.getSize().y / 2),
				(int) (getPosition().x + getPanel().getCellSize() - getPanel().getShift().x), getPosition().y + getPanel().getCellSize());
	}
}
