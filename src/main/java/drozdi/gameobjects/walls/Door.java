package drozdi.gameobjects.walls;

import drozdi.FileManager;
import drozdi.clientside.Panel;
import lombok.Getter;
import lombok.Setter;

public class Door extends Wall {
	@Getter
	private final int maxKeys;
	@Getter @Setter
	private boolean open;
	public Door(int x, int y, int max) {
		super(x, y, 2, 2);
		this.maxKeys = max;
	}
	/*
	public boolean open(Server gameServer) {
		return ((maxKeys - gameServer.getKeys()) <= 0);
	}*/

	/*public void collisionControl(Player player) {
		if (open && player.getHitBoxServer().intersects(getHitBoxServer())) {
				//TODO end
				player.setDeathCount(-1);
				getPanel().getLevel3().setSavedKeyList(null);
				getPanel().getLevel3().setKeyCount(0);
				getPanel().getLevel3().setEnd(false);
				Point p = new Point((Point) getPanel().getShift().clone());
				p.x = 0;
				getPanel().setShift(p);
				getPanel().setDefaultScreenPosition ((Point) getPanel().getDefaultScreenPosition().clone());
				getPanel().end();
		}
	}
	*/
	public void draw(Panel panel) {
		if (panel.getScreen().intersects(getHitBox(panel))) {
			if (open) {
				drawWall(FileManager.door, panel);
			}else {
				drawWall(FileManager.doorOpen,panel);
			}
		}
	}
}
