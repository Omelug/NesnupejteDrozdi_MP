package drozdi.gameobjects.walls;

import drozdi.FileManager;
import drozdi.gameobjects.GameObject;
import lombok.Data;
import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import drozdi.clientside.Panel;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = false) //TODO tohle zkontrolovat
@Data
public class Wall extends GameObject {
	private Point position;
	private Point2D.Float size;

	public Wall(int x, int y, float sizeX, float sizeY) {
		position = new Point(x, y);
		size = new Point2D.Float (sizeX, sizeY);
	}
	public Wall(int x, int y) {
		this(x , y , 1, 1);
	}

	public Rectangle2D.Float getHitBox(Panel panel) {
		return new Rectangle2D.Float (((float)(position.x) - panel.getShift().x), position.y,  size.x, size.y);
	}

	public Rectangle2D.Float getHitBoxServer() {
		return new Rectangle2D.Float(position.x , position.y, size.x, size.y);
	}


	public void draw(Panel panel) {
		drawOnScreen(FileManager.wall, panel);
		//panel.drawHitBox(getHitBox(panel));
	}
	/*public void draw(Image image, Panel panel) {
		drawOnScreen(image, panel);
	}*/
	protected void drawWall(Image image, Panel panel) {
		Rectangle2D.Float hitBox = getHitBox(panel);
		panel.getG2d().drawImage(image,
						(int) (hitBox.x * panel.getCellSize()),
						(int) (hitBox.y * panel.getCellSize()),
						(int) size.x * panel.getCellSize(),
						(int) size.y * panel.getCellSize(),
				null);
	}
	public void drawOnScreen(Image image, Panel panel) {
		if (panel.getScreen().intersects(getHitBox(panel))) {
			drawWall(image, panel);
		}
	}

}

