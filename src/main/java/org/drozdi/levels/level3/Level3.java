package org.drozdi.levels.level3;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.drozdi.game.NesnupejteDrozdi;
import org.drozdi.game.RelativeSize;
import org.drozdi.game.Window;
import org.drozdi.levels.level3.walls.Key;

import javax.swing.*;
import java.awt.*;
import java.util.Set;

@Data
public class Level3 {
	@Getter @Setter
	private static Thread thread;
	private boolean end = true;

	private static Panel_level3 gamePanel;
	private Set<Key> savedKeyList;
	private int keyCount = 0;

	private Window window;
	private JLabel infoLabel;

	private Object lock = new Object();

	public Level3(Window window) {
		this.window = window;

		window.setTitle("Šňupejte droždí  - Level 3");
		infoLabel = new JLabel();

		infoLabel.setBounds(RelativeSize.rectangle(0, 0, 100, 10));
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

		FileManager_lvl3.loadResources();

		NesnupejteDrozdi.getClient().setPanelLevel3(gamePanel);
		NesnupejteDrozdi.getClient().getMap(); //TODO pockat na
		window.repaint();

		do {
			gamePanel = new Panel_level3(this);
			window.setFocusable(true);
			window.requestFocus();
			window.add(gamePanel);
			window.addKeyListener(new MoveInput(gamePanel));
			//thread = Thread.currentThread();
			synchronized (lock) {
				try {
					lock.wait();
				} catch (InterruptedException ex) {
					System.out.println("ERROR -- Level3");
				}
			}
		} while (!end);

		System.out.println("Level3 -- END " + Thread.currentThread());
	}

	/**public void saveTime() {
		time = System.currentTimeMillis() - time;
		if (NesnupejteDrozdi.mapTimeList[map] > time) {
			NesnupejteDrozdi.mapTimeList[map]= time;
		}
		map++;
	}**/


}