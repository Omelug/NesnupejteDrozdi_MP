package drozdi.gameobjects.walls;


import drozdi.FileManager;
import drozdi.clientside.Panel;
import java.awt.geom.Rectangle2D;

public class Tower extends Wall {
	public static final double MAX_DISTANCE = 200; //TODO check
	int shot = 0;

	public Tower(int x, int y) {
		super(x, y, 2, 2);
	}

	@Override
	public Rectangle2D.Float getHitBox(Panel panel) {
		return new Rectangle2D.Float (
						(getPosition().x - panel.getShift().x + 0.5F),
						getPosition().y,
						getSize().x- 1,
						getSize().y);
	}
/*
	public void shot(PlayerMP nearestPlayer, HitBoxHelper hitBoxHelper) {
		shot++;
		if (shot > 80) {
			shot = 0;
			hitBoxHelper.getMapHelper().getEntityShots().add(new Bullet(this, nearestPlayer));
		}
	}*/
	@Override
	public void draw(Panel panel) {
		Rectangle2D.Float hitBox = getHitBox(panel);
		if (panel.getScreen().intersects(hitBox)) { //TODO hitbox je maly, takze to bude platit vzdy, zkontrolovat u dalsich
			panel.getG2d().drawImage(FileManager.tower,
							(int) (getHitBox(panel).x * panel.getCellSize() - (0.5 * panel.getCellSize())),
							(int) ((getHitBox(panel).y) * panel.getCellSize()),
							(int) getSize().x * panel.getCellSize(),
							(int) getSize().y * panel.getCellSize(), null);

			/*if (Test.isHitBoxTower()) {
				panel.getG2d().setColor(Color.green);
				panel.drawHitBox(getHitBox(panel));
			}*/
		}
	}
	/*
	public void drawLine(PlayerMP player, GamePanel panel) {
		panel.getG2d().setColor(Color.orange);
		panel.getG2d().drawLine(
				(int) ((player.getPosition().x + player.getSize().x / 2) * panel.getCellSize()),
				(int) ((player.getPosition().y + player.getSize().y / 2) * panel.getCellSize()),
				(int) (((getPosition().x + 0.5)* panel.getCellSize() - panel.getShift().x)),
				(getPosition().y + 1) * panel.getCellSize());
	}*/
}
