package org.drozdi.levels.level3;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.*;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JPanel;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.Point;
import java.awt.Color;
import lombok.Data;
import org.drozdi.game.NesnupejteDrozdi;
import org.drozdi.game.RelativeSize;
import org.drozdi.game.Test;
import org.drozdi.levels.level3.client.GameClient;
import org.drozdi.levels.level3.client.PlayerMP;
import org.drozdi.levels.level3.walls.*;

@Data
public class Panel_level3 extends JPanel{
	private PlayerMP player;
	private Timer timer;

	private Point2D.Double shift;
	private Point screenPosition;

	private long lastUpdateTime = System.nanoTime();
	private double fps = 0;
	private boolean end;
	private int cellSize;
	private Rectangle screen;
	private Level3 level3;
	private Graphics2D g2d;
	private Set<Wall> wallsOnScreen = new HashSet<>();

	private MapHelper mapHelper = new MapHelper();


	public Panel_level3(Level3 level3) {
		this.level3 = level3;
		GameClient client = NesnupejteDrozdi.getClient();
		client.setPanelLevel3(this);

		screenPosition = new Point(13, 10);
		end = false;

		setBackground(new Color(45, 25, 33));
		setBounds(RelativeSize.rectangle(0, 0, 100, 85));
		setDoubleBuffered(true);
		cellSize = (int) (getHeight() / 18.25);
		Bullet.size = new Point((int) (cellSize / 2.2), (int) (cellSize / 2.2));
		player = new PlayerMP(NesnupejteDrozdi.account);
		updateInfo();
		shift = new Point2D.Double(0, 0);
		screen = new Rectangle(0, 0, getWidth(), getHeight());
		//System.out.println("Panel {" + getWidth() +";"+ getHeight()+"}");
		mapHelper.loadMap();
		level3.setEnd(true);

		timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				try {
					update();
					repaint();
				} catch (Exception e) {
					timer.cancel();
					System.out.println("END with exception:");
					e.printStackTrace();
				}
			}
		}, 50, 15);
		client.login();
		level3.getWindow().repaint();
	}

	private void update() {
		//TODO limitace na veci na obrazovce

		for (Wall wall : mapHelper.getWalls()) {
			if (screen.intersects(wall.getHitBox(this))) {
			 wallsOnScreen.add(wall);
			}
		}

	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		long currentTime = System.nanoTime();
		double elapsedTime = (currentTime - lastUpdateTime) / 1e9;
		lastUpdateTime = currentTime;
		fps = 1.0 / elapsedTime;

		level3.getWindow().setTitle("FPS: " + String.format("%.2f", fps) +", Walls: "+wallsOnScreen.size() +"/"+mapHelper.getWalls().size()+", Towers: "+ mapHelper.getTowers().size()+", Player: "+ player.getPosition());
	}

	public void updateInfo() {
		level3.getInfoLabel().setText("Úmrtí v důsledku závislosti: " + player.getDeathCount() + "  Klíče: " + level3.getKeyCount() + "/5");
	}

	@Override
	public void paint(Graphics graphics) {
		super.paint(graphics);
		g2d = (Graphics2D) graphics;

		for (Wall wall : wallsOnScreen) {
			wall.draw(this);
		}
		for (Hedgehog hedgehog : mapHelper.getHedgehogs()) {
			hedgehog.draw(this);
		}
		for (Ladder ladder : mapHelper.getLadders()) {
			ladder.draw(this);
		}
		for (Checkpoint checkpoint : mapHelper.getCheckpoints()) {
			checkpoint.draw(this);
		}
		for (Key key : mapHelper.getKeys()) {
			key.draw(this);
		}
		for (Door door : mapHelper.getDoors()) {
			door.draw(this);
		}
		for (Tower tower : mapHelper.getTowers()) {
			tower.draw(this);
			if (Test.isLinesTower()) {
				if (player.getPosition() != null && player.getSize() != null){
					tower.drawLine(player, this);
				}else{
					System.out.println("position or size is null");
				}
			}
		}
		for (Slug slug : mapHelper.getSlugs()) {
			slug.draw(this);
			if (Test.isLinesSlug()){
				if (player.getPosition() != null && player.getSize() != null){
					slug.drawLine(player, this);
				}else{
					System.out.println("position or size is null");
				}
			}
		}

		player.drawClientPlayer(g2d, this);

		for (PlayerMP player : mapHelper.getPlayerList()) {
			player.draw(g2d, this);
		}

		for (Bullet bullet : mapHelper.getPlayerShots()) {
			bullet.draw(g2d, this);
		}
		for (Bullet bullet : mapHelper.getEntityShots()) {
			bullet.draw(g2d, this);
		}

		if (Test.isHitBoxScreen()) {
			g2d.setColor(Color.red);
			drawHitBox(screen);
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
		NesnupejteDrozdi.getClient().move();
	}

	public void end() {
		updateInfo();
		//TODO send disconnect server
		synchronized (level3.getLock()) {
			try {
				if (!end) {
					end = true;
					System.out.println("END");
					timer.cancel();
					level3.getLock().notify();
				}
			} catch (Exception e) {
				System.out.println("ERROR  -- END " + Thread.currentThread());
			}

		}
	}

	public void restart() {
		//TODO zkontrolovat restart
		System.out.println("RESTART");
		level3.setSavedKeyList(new HashSet<>());
		level3.getSavedKeyList().addAll(mapHelper.getKeys());
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
		NesnupejteDrozdi.getClient().move();
	}

	public void setShift(Point newShift) {
		Point2D.Double point = new Point2D.Double();
		point.x = newShift.x;
		point.y = newShift.y;
		setShift(point);
	}

	public void setShift(Point2D.Double shift) {
		this.shift = shift;
	}

	public void drawHitBox(Rectangle hitBox) {
		Rectangle biggerHitBox = new Rectangle(hitBox.x *cellSize, hitBox.y * cellSize, hitBox.getSize().width * cellSize,hitBox.getSize().height * cellSize);
		g2d.draw(biggerHitBox);
	}

	public void drawHitBox(Rectangle2D.Double hitBox) {
		Rectangle biggerHitBox = new Rectangle((int) (hitBox.x *cellSize), (int) (hitBox.y * cellSize), (int) (hitBox.getWidth() * cellSize), (int) (hitBox.getHeight() * cellSize));
		g2d.draw(biggerHitBox);
	}
}
