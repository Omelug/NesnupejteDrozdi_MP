package org.drozdi.levels.level3;

import lombok.Data;
import java.awt.*;
import java.awt.geom.Point2D;

@Data
public class Wall {
	private Point position;
	private Point2D.Double size;


	public Wall(int x,int y,double sizeX,double sizeY) {
		position = new Point(x, y);
		size = new Point2D.Double (sizeX, sizeY);
	}
	public Wall(int x, int y) {
		this(x , y , 1, 1);
	}

	public Rectangle getHitBox(Panel_level3 panel) {
		return new Rectangle((int) (position.x - panel.getShift().x), position.y, (int) size.x, (int) size.y);
	}

	public Rectangle getHitBoxServer() {
		return new Rectangle( position.x , position.y, (int) size.x, (int) size.y);
	}

	public void draw(Panel_level3 panel) {
		drawOnScreen(FileManager_lvl3.wall, panel);
	}
	public void draw(Image image, Panel_level3 panel) {
		drawOnScreen(image, panel);
	}
	protected void drawWall(Image image, Panel_level3 panel) {
		Rectangle hitBox = getHitBox(panel);
		panel.getG2d().drawImage(image,
				hitBox.x * panel.getCellSize(),  hitBox.y * panel.getCellSize(),
				(int) size.x * panel.getCellSize(), (int) size.y * panel.getCellSize(),
				null);
	}
	public void drawOnScreen(Image image, Panel_level3 panel) {
		if (panel.getScreen().intersects(getHitBox(panel))) {
			drawWall(image, panel);
		}
	}

}

