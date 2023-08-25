package org.drozdi.levels.level3;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.awt.*;

@Data
public class Wall {

	@Getter @Setter
	private static Panel_level3 panel;

	private Point position;
	private Point size;
	private Rectangle hitBox;
		
	public Wall(int x,int y,int sizeX,int sizeY,Panel_level3 panel) {
		Wall.panel = panel;
		position = new Point(x,y);
		size = new Point(sizeX, sizeY);
		hitBox = new Rectangle((int) (position.x- panel.getShift().x), position.y, size.x, size.y);
	}

	public void setUp() {
		hitBox = new Rectangle((int) (position.x - panel.getShift().x), position.y, size.x, size.y);
	}

	public void draw() {
		drawWall(FileManager_lvl3.wall);
	}
	public void draw(Image image) {
		drawWall(image);
	}

	private void drawWall(Image image) {
		panel.getG2d().drawImage(image, hitBox.x,  hitBox.y, size.x, size.y, null);
	}

	public void drawOnScreen(Image image) {
		if (getPanel().getScreen().intersects(getHitBox())) {
			drawWall(image);
		}
	}

	}

