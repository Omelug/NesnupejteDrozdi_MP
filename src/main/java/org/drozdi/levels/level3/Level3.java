package org.drozdi.levels.level3;

import javax.swing.*;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.drozdi.game.NesnupejteDrozdi;
import org.drozdi.game.Window;
import org.drozdi.game.RelativeSize;
import org.drozdi.levels.level3.steny.Key;

import java.awt.Color;
import java.awt.Font;
import java.awt.Point;
import java.util.ArrayList;

@Data
public class Level3 {
	public static Panel_level3 gamePanel;
	@Getter @Setter
	private static Thread thread;
	private boolean end = true;
	private Point defaultPosition, screenPosition, defaultScreenPosition;
	private Point shift;
	private ArrayList<Key> savedKeyList;
	JLabel infoLabel;
	private int deathCount = 0;
	private int keyCount = 0;
	int map;
	long cas;
	private JFrame window;
	public Level3(Window window) {
		this.window = window;
		map = NesnupejteDrozdi.getLevel3Level();
		window.setTitle("Šňupejte droždí  - Level 3 - CESTA DOMŮ");
		zaklad(window);
		new FileManager_lvl3();

		defaultScreenPosition = new Point(13, 10);
		screenPosition = new Point(13, 10);
		defaultPosition = (Point) screenPosition.clone();
		shift = new Point(0, 0);
		//window.repaint();
		cas = System.currentTimeMillis();
		do {
			panelBase(window);
			thread = Thread.currentThread();
			
			synchronized (thread) {
				try {
					thread.wait();
				} catch (InterruptedException ex) {
					System.out.println("CHYBA -- Level3");
				}
			}
			deathCount++;
		} while (!end);
		NesnupejteDrozdi.setLevel3Level(map);
		System.out.println("Level3 -- KONEC    " + Thread.currentThread());
	}
	public void ulozeniCasu() {
		cas = System.currentTimeMillis() - cas;
		if (NesnupejteDrozdi.mapTimeList[map] > cas) {
			NesnupejteDrozdi.mapTimeList[map]= cas;
		}
		map++;
	}
	private void zaklad(Window window) {
		window.smazat();
		infoLabel = new JLabel();

		infoLabel.setText("Úmrtí v důsledku závislosti:  "+ defaultPosition + "Klíče: " + keyCount+ "/5");
		infoLabel.setBounds(RelativeSize.rectangle(0, 0, 100, 15));
		infoLabel.setHorizontalAlignment(JLabel.CENTER);
		infoLabel.setVerticalTextPosition(JLabel.CENTER);
		infoLabel.setFont(new Font("Consolas", Font.PLAIN, 35));
		infoLabel.setForeground(Color.white);
		
		window.answerPanel.setLayout(null);
		window.answerPanel.setBackground(new Color(77,68,68));
		window.answerPanel.setBounds(RelativeSize.rectangle(0, 85, 100, 15));
		window.answerPanel.add(infoLabel);
		
		window.setVisible(true);
		window.setFocusable(true);
		window.add(window.answerPanel);
		
	}

	void panelBase(Window window) {
		gamePanel = new Panel_level3(this);
		window.setFocusable(true);
		window.requestFocus();
		window.add(gamePanel);
		window.addKeyListener(new MoveInput(gamePanel));
	}
}