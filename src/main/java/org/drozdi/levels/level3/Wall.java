package org.drozdi.levels.level3;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;

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
		hitBox = new Rectangle(position.x- panel.getShift().x, position.y, size.x, size.y);
	}

		public void nastav() {
			hitBox = new Rectangle(position.x - panel.getShift().x, position.y, size.x, size.y);
		}

		public void draw(Graphics2D g2d,Rectangle r) {
			if (getHitBox().intersects(r)) { //aby se rendrovali jen veci na obrazovce
				g2d.setColor(Color.gray);
				g2d.drawImage(FileManager_lvl3.wall, hitBox.x, hitBox.y, size.x, size.y, null);
			}
		}


	}

