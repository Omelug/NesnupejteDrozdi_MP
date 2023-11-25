package drozdi.gameobjects.walls;

import drozdi.FileManager;
import drozdi.clientside.Panel;


public class Checkpoint extends Wall {
	public Checkpoint(int x, int y) {
		super(x, (int) (y + 0.92), 1, 0.05F);
	}

	/*@Override
	public Rectangle2D.Double getHitBox(GamePanel panel) {
		return new Rectangle2D.Double( (getPosition().x - panel.getShift().x), getPosition().y - 1, (int) getSize().x, (int) (getSize().y + 1));
	}
	@Override
	public Rectangle2D.Double getHitBoxServer() {
		return new Rectangle2D.Double (getPosition().x, getPosition().y - 1, getSize().x, (getSize().y + 1));
	}


	public void collisionControl(PlayerMP player) {
		if (player.getHitBoxServer().intersects(getHitBoxServer())) {
			//TODO getPanel().setInfo("Checkpoint pressed");
			//getPanel().getDefaultPosition().x = getPosition().x / getPanel().getCellSize();
			player.setCheckpoint(this);
		}
	}
	*/
	public void draw(Panel panel) {
		drawOnScreen(FileManager.checkpoint, panel);
	}
		/*if (Test.isHitBoxCheckpoint()) {
			panel.getG2d().setColor(Color.red);
			panel.drawHitBox(getHitBox(panel));
		}
	}*/

}
