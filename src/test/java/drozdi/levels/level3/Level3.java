package drozdi.levels.level3;

import drozdi.game.Window;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import drozdi.game.RelativeSize;
import drozdi.levels.level3.client.GameClient;
import drozdi.levels.level3.walls.Key;

import javax.swing.*;
import java.awt.*;
import java.util.Set;

@Data
public class Level3 {
	@Getter @Setter
	private static Thread thread;
	private Set<Key> savedKeyList;
	private int keyCount = 0;

	private drozdi.game.Window window;
	private JLabel infoLabel;

	@Getter
	private static GameClient client;
	@Getter
	private static GamePanel gamePanel;

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
		window.setFocusable(true);
		window.requestFocus();

		client = new GameClient();
		client.start();
		gamePanel = new GamePanel(this);
		window.add(gamePanel);
		window.addKeyListener(new MoveInput(gamePanel));
		window.answerPanel.repaint();
		gamePanel.start();
		System.out.println("walls -- END " + Thread.currentThread());
	}
}