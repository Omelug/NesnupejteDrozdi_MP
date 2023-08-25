package org.drozdi.levels.level3.walls;

import org.drozdi.game.Test;
import org.drozdi.levels.level3.*;
import org.drozdi.levels.level3.player.Player_lvl3;

import java.awt.Color;
import java.awt.Rectangle;

public class Slug extends Wall {
	Panel_level3 panel;
	int shoot = 0;

	public Slug(int x, int y, int sizeX, int sizeY, Panel_level3 panel) {
		super(x, y, sizeX*2, sizeY, panel);
		setHitBox(new Rectangle((int) (getPosition().x - panel.getShift().x+panel.getCellSize()), getPosition().y, getSize().x-panel.getCellSize(), getSize().y));
		this.panel = panel;
	}

	public void setUp(Player_lvl3 player) {
	  	setHitBox(new Rectangle((int) (getPosition().x - panel.getShift().x), getPosition().y, getSize().x, getSize().y));
		if (getHitBox().intersects(panel.getScreen())) {
			shoot++;
			if (shoot > 50) {
				shoot = 0;
				panel.getEntityShots().add(new Bullet(panel, this, player));
			}
		}
	}

	@Override
	public void draw() {
		drawOnScreen(FileManager_lvl3.slug);
		if (Test.isHitBoxTower()) {
			getPanel().getG2d().setColor(Color.green);
			getPanel().getG2d().draw(getHitBox());
		}
	}

    public void drawLine(Player_lvl3 player) {
		getPanel().getG2d().setColor(Color.red);
		getPanel().getG2d().drawLine((int) (player.getPosition().x + player.getSize().x / 2), (int) (player.getPosition().y + player.getSize().y / 2),
				(int) (getPosition().x + getPanel().getCellSize() - getPanel().getShift().x), getPosition().y + getPanel().getCellSize());
    }
}
