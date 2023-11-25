package drozdi.levels.level2;

import drozdi.game.NesnupejteDrozdi;
import drozdi.game.Window;

import javax.swing.*;
import java.awt.*;

public class Level2 {
	ImageIcon drozdi = Window.resizeImage(new ImageIcon("rsc/drozdi.png"), 200, 200);
	Window window;
	Vozik vozik;
	public static drozdi.levels.level2.Panel hraPanel;
	static Thread thread;
	private long time;
	public Level2(Window window) {
		this.window = window;
		window.setTitle("Šňupejte droždí - Level 2 - SHOPPING");
		base(window);
		window.repaint();

		thread = Thread.currentThread();
		synchronized (thread) {
			try {
				thread.wait();
			} catch (InterruptedException ex) {
				System.out.println("ERROR -- Level2" + ex);
			}
		}
		System.out.println("Level2 -- END    " + Thread.currentThread());
	}
	public void setCas(long time){
		this.time = time;
	}
	public void saveTime() {
		time = System.currentTimeMillis() - time;
		if (NesnupejteDrozdi.timeLevel2 > time) {
			NesnupejteDrozdi.timeLevel2 = time;
		}
	}

	void base(Window window) {
		window.clean();
		window.defineWindow();

		window.questionLabel.setLayout(null);
		window.questionLabel.setFont(new Font("Consolas", Font.PLAIN, 35));
		window.questionLabel.setForeground(Color.white);
		window.questionLabel.setBounds(0, window.windowHeight * 5 / 8, window.windowWidth, window.windowHeight * 1 / 8);
		window.questionLabel.setBackground(Color.red);
		window.questionLabel.setOpaque(false);
		window.questionLabel.setVerticalTextPosition(JLabel.BOTTOM);

		window.questionPanel.setOpaque(true);
		window.questionPanel.setBackground(Color.black);

		window.answerPanel.setLayout(null);
		window.answerPanel.setOpaque(true);
		hraPanel = new Panel(window, this);
		window.addKeyListener(hraPanel.keyK);
		window.setFocusable(true);
		window.requestFocus();

		window.add(hraPanel);
		hraPanel.setVisible(true);
	}

}
