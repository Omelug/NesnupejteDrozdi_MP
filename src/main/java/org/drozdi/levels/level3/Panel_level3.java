package org.drozdi.levels.level3;

import java.util.*;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;
import java.awt.Rectangle;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.awt.Point;
import java.awt.Color;

import lombok.Getter;
import lombok.Setter;
import org.drozdi.game.FileManager;
import org.drozdi.game.RelativeSize;
import org.drozdi.game.Test;
import org.drozdi.levels.level3.steny.*;

// https://youtu.be/Icd2gAHDSfY

public class Panel_level3 extends JPanel{
	private int frameCount = 0;
	private long lastUpdateTime = System.nanoTime();
	private double fps = 0;

	private Player_lvl3 player;
	private Timer timer;
	@Getter @Setter
	private Point shift;
	@Getter @Setter
	private long cas;
	@Getter @Setter
	boolean end;

	@Getter @Setter
	private Set<Wall> walls = new HashSet<>();
	@Getter @Setter
	private Set<Hedgehog> hedgehogs = new HashSet<>();
	@Getter @Setter
	private Set<Ladder> ladders = new HashSet<>();
	@Getter @Setter
	private Set<Checkpoint> checkpoints = new HashSet<>();
	@Getter @Setter
	private Set<Bullet> playerShots = new HashSet<>();
	@Getter @Setter
	private Set<Bullet> entityShots = new HashSet<>();
	@Getter @Setter
	private Set<Tower> towers = new HashSet<>();
	@Getter @Setter
	private Set<Slug> slugs = new HashSet<>();
	@Getter @Setter
	private Set<Door> doors = new HashSet<>();
	@Getter @Setter
	private ArrayList<Key> keys = new ArrayList<>();
	@Getter @Setter
	private Set<Wall> wallsOnScreen;

	@Getter @Setter
	private int cellSize;
	@Getter @Setter
	private Rectangle screen;
	@Getter @Setter
	private Level3 level3;

	private static boolean running;
	@Getter @Setter
	private static BufferedImage map;

	public Panel_level3(Level3 level3) {
		end = false;
		setLevel3(level3);
		start();
	}

	private void start() {
		setBackground(new Color(45, 25, 33));
		setBounds(RelativeSize.rectangle(0, 0, 100, 85));
		setDoubleBuffered(true);
		// cellSize = this.getWidth() / 40;
		cellSize = (int) (getHeight() / 18.25);
		Bullet.size = new Point((int) (cellSize / 2.2), (int) (cellSize / 2.2));

		player = new Player_lvl3(new Point(level3.getScreenPosition().x * cellSize,
				level3.getScreenPosition().y * cellSize), this);
		shift = new Point(level3.getShift().x, level3.getShift().y);

		screen = new Rectangle(0, 0, getWidth(), getHeight());
		nastaveniZdi();
		level3.setEnd(true);
		cas = System.nanoTime();
		timer = new Timer();
		running = true;
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				try {
					update();
					repaint();
				} catch (Exception e) {
					timer.cancel();
					e.printStackTrace();
					System.out.println("KONEC" + e);
				}
			}
		}, 50, 15);
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		long currentTime = System.nanoTime();
		double elapsedTime = (currentTime - lastUpdateTime) / 1e9; // Convert nanoseconds to seconds
		lastUpdateTime = currentTime;
		fps = 1.0 / elapsedTime;

		level3.getWindow().setTitle("FPS: " + String.format("%.2f", fps) +" Walls: "+wallsOnScreen.size() +"/"+walls.size()+" Towers: "+ towers.size());
	}


	private void update() {
		wallsOnScreen = new HashSet<>();
		for (Wall wall : walls) {
			wall.nastav();
			if (screen.intersects(wall.getHitBox())) {
				wallsOnScreen.add(wall);
			}
		}
		for (Hedgehog hedgehog : hedgehogs) {hedgehog.nastav();}
		for (Ladder ladder : ladders) {
			ladder.nastav();
		}
		for (Key key : keys) {
			key.nastav();
		}
		for (Checkpoint checkpoint : checkpoints) {
			checkpoint.nastav();
		}
		for (Tower tower : towers) {
			tower.nastav(player);
		}
		for (Slug slug : slugs) {
			slug.nastav(player);
		}
		for (Door door : doors) {
			door.nastav(this);
		}
		player.nastav();

		Iterator<Bullet>  entityBulletIretator = entityShots.iterator();
		while (entityBulletIretator.hasNext()) {
			Bullet bullet = entityBulletIretator.next();
			if (!screen.intersects(bullet.getHitBox())){
				entityBulletIretator.remove();
				break;
			}
		}

		Iterator<Bullet> playerShotsIterator = playerShots.iterator();
		while (playerShotsIterator.hasNext()) {
			Bullet bullet = playerShotsIterator.next();
			if (!screen.intersects(bullet.getHitBox())){
				playerShotsIterator.remove();
				break;
			}
			for (Wall wall : getWallsOnScreen()) {
				if (wall.getHitBox().intersects(bullet.getHitBox())) {
					if (bullet.getTypStrely() == 1) {
						playerShotsIterator.remove();
						break;
					}
				}
			}
		}

		Iterator<Key> keyIterator = keys.iterator();
		while (keyIterator.hasNext()) {
			Key key = keyIterator.next();
			if (player.getHitBox().intersects(key.getHitBox())) {
				keyIterator.remove();
				getLevel3().setKeyCount(getLevel3().getKeyCount()+1);
			}
		}

		Iterator<Tower> towerIterator = towers.iterator();
		while (towerIterator.hasNext()) {
			Tower tower = towerIterator.next();
			Iterator<Bullet> bulletIterator = getPlayerShots().iterator();
			while (bulletIterator.hasNext()) {
				Bullet bullet = bulletIterator.next();
				if (bullet.getHitBox().intersects(tower.getHitBox())) {
					bulletIterator.remove();
					towerIterator.remove();
					break;
				}
			}
		}

		Iterator<Slug> slugrIterator = slugs.iterator();
		while (slugrIterator.hasNext()) {
			Slug slug = slugrIterator.next();
			Iterator<Bullet> bulletIterator = getPlayerShots().iterator();
			while (bulletIterator.hasNext()) {
				Bullet bullet = bulletIterator.next();
				if (bullet.getHitBox().intersects(slug.getHitBox())) {
					bulletIterator.remove();
					slugrIterator.remove();
					break;
				}
			}
		}

		for (Bullet bullet : playerShots) {
			bullet.nastav();
		}
		for (Bullet bullet : entityShots) {
			bullet.nastav();
		}

		updateInfo();

	}

	private void updateInfo() {
		level3.infoLabel.setText(
				"Úmrtí v důsledku závislosti: " + level3.getDeathCount() + "  Klíče: " + level3.getKeyCount() + "/5");

	}

	private void nastaveniZdi() {

		//get colors from palete
		int wallBarva = FileManager_lvl3.palet.getRGB(0, 0);
		int smrt = FileManager_lvl3.palet.getRGB(1, 0);
		int ladder = FileManager_lvl3.palet.getRGB(2, 0);
		int tower = FileManager_lvl3.palet.getRGB(3, 0);
		int checkpoint = FileManager_lvl3.palet.getRGB(4, 0);
		int key = FileManager_lvl3.palet.getRGB(5, 0);
		int door = FileManager_lvl3.palet.getRGB(6, 0);
		int slug = FileManager_lvl3.palet.getRGB(7, 0);

		map = FileManager.loadResource("Level3/maps/map" + level3.getMap() + ".bmp");


		for (int y = 0; y < map.getHeight(); y++) {
			for (int x = 0; x < map.getWidth(); x++) {
				int color = map.getRGB(x, y);
				if (color == wallBarva) {
					walls.add(new Wall(x * cellSize, y * cellSize, cellSize, cellSize, this));
				} else if (color == smrt) {
					hedgehogs.add(new Hedgehog(x * cellSize, y * cellSize, cellSize, cellSize, this));
				} else if (color == ladder) {
					ladders.add(new Ladder(x * cellSize, y * cellSize, cellSize, cellSize, this));
				} else if (color == tower) {
					towers.add(new Tower(x * cellSize, y * cellSize, cellSize, cellSize, this));
				} else if (color == checkpoint) {
					checkpoints.add(new Checkpoint(x * cellSize, (int) (y * cellSize + cellSize * 0.95),
							cellSize, (int) (cellSize * 0.05), this));
				} else if (color == key) {
					if (level3.getSavedKeyList() == null) {
						keys.add(new Key(x * cellSize, y * cellSize, cellSize, cellSize, this));
					}
				} else if (color == door) {
					doors.add(new Door(x * cellSize, y * cellSize, cellSize * 2, cellSize * 2,
							this, 5));
				} else if (color == slug) {
					slugs.add(new Slug(x * cellSize, y * cellSize, cellSize, cellSize, this));
				}
			}

		}
		if (!(level3.getSavedKeyList() == null)) {
			keys = new ArrayList<>();
			keys.addAll(level3.getSavedKeyList());
		}

	}

	public void paint(Graphics g) {

		super.paint(g);

		Graphics2D g2d = (Graphics2D) g;
		if (wallsOnScreen != null) {
			for (Wall wall : wallsOnScreen) {
				wall.draw(g2d, screen);
			}
		}
		if (hedgehogs != null) {
			for (Hedgehog hedgehog : hedgehogs) {
				hedgehog.draw(g2d, screen);
			}
		}
		if (ladders != null) {
			for (Ladder ladder : ladders) {
				ladder.draw(g2d, screen);
			}
		}
		if (ladders != null) {
			for (Checkpoint checkpoint : checkpoints) {
				checkpoint.draw(g2d, screen);
			}
		}
		if (keys != null) {
			for (Key key : keys) {
				key.draw(g2d, screen);
			}
		}
		if (doors != null) {
			for (Door door : doors) {
				door.draw(g2d, screen);
			}
		}
		if (towers != null) {
			for (Tower tower : towers) {
				tower.draw(g2d, screen);
				if (Test.isLinesTowers()) {
					g2d.setColor(Color.red);
					g2d.drawLine(player.getPosition().x + player.getSize().x / 2, player.getPosition().y + player.getSize().y / 2,
							tower.getPosition().x + cellSize - shift.x, tower.getPosition().y + cellSize);
				}
			}
		}
		if (towers != null) {
			for (Slug slug : slugs) {
				slug.draw(g2d, screen);
				if (Test.isLinesTowers()) {
					g2d.setColor(Color.red);
					g2d.drawLine(player.getPosition().x + player.getSize().x / 2, player.getPosition().y + player.getSize().y / 2,
							slug.getPosition().x + cellSize - shift.x, slug.getPosition().y + cellSize);
				}
			}
		}
		player.draw(g2d);

		for (Bullet bullet : playerShots) {
			bullet.draw(g2d, screen);
		}
		for (Bullet bullet : entityShots) {
			bullet.draw(g2d, screen);
		}
		if (Test.isHitBoxScreen()) {
			g2d.setColor(Color.red);
			g2d.draw(screen);
		}
	}

	public void keyReleased(KeyEvent e) {
		switch (Character.toLowerCase(e.getKeyChar())) {
			case 'w' ->	player.setUp(false);
			case 'a' -> player.setLeft(false);
			case 's' ->	player.setDown(false);
			case 'd' -> player.setRight(false);
			case 'r' -> restart();
			case 't' -> end();
			case KeyEvent.VK_SPACE -> player.setShooting(false);
		}
	}

	public void end() {
		hedgehogs = null;
		synchronized (Level3.getThread()) {
			try {
				if (!end) {
					end = true;
					System.out.println("KONEC");
					timer.cancel();
					Level3.getThread().notify();
				}
			} catch (Exception e) {
				System.out.println("CHYBA  -- KONEC " + Thread.currentThread());
			}

		}
	}

	public void restart() {
		System.out.println("RESTART");
		level3.setSavedKeyList(new ArrayList<>());
		level3.getSavedKeyList().addAll(keys);
		level3.setEnd(false);
		end();
	}

	public void keyPressed(KeyEvent e) {
		switch (Character.toLowerCase(e.getKeyChar())) {
			case 'w' -> player.setUp(true);
			case 'a' -> player.setLeft(true);
			case 's' -> player.setDown(true);
			case 'd' -> player.setRight(true);
			case KeyEvent.VK_SPACE -> player.setShooting(true);
		}
	}

}
