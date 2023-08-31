package org.drozdi.levels.level3.walls;

import lombok.*;
import org.drozdi.levels.level3.FileManager_lvl3;
import org.drozdi.levels.level3.Panel_level3;
import org.drozdi.levels.level3.Wall;
import org.drozdi.levels.level3.client.PlayerMP;
import org.drozdi.levels.level3.server.GameServer;

public class Door extends Wall {
	@Getter
	private final int maxKeys;
	@Getter @Setter
	private boolean open;
	public Door(int x, int y, int max) {
		super(x, y, 2, 2);
		this.maxKeys = max;
	}

	public boolean open(GameServer gameServer) {
		return ((maxKeys - gameServer.getKeys()) <= 0);
	}

	public void collisionControl(PlayerMP player) {
		if (open && player.getHitBoxServer().intersects(getHitBoxServer())) {
				//TODO end
				/**player.setDeathCount(-1);
				getPanel().getLevel3().setSavedKeyList(null);
				getPanel().getLevel3().setKeyCount(0);
				getPanel().getLevel3().setEnd(false);
				Point p = new Point((Point) getPanel().getShift().clone());
				p.x = 0;
				getPanel().setShift(p);
				getPanel().setDefaultScreenPosition ((Point) getPanel().getDefaultScreenPosition().clone());
				getPanel().end();**/
		}
	}

	public void draw(Panel_level3 panel) {
		if (panel.getScreen().intersects(getHitBox(panel))) {
			if (open) {
				drawWall(FileManager_lvl3.door, panel);
			}else {
				drawWall(FileManager_lvl3.doorOpen,panel);
			}
		}
	}
}
