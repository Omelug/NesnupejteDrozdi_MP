package org.drozdi.levels.level2;

import org.drozdi.game.NesnupejteDrozdi;
import org.drozdi.game.Window;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import java.awt.Color;
import java.awt.Font;

public class Level2 {
	ImageIcon drozdi = Window.resizeImage(new ImageIcon("rsc/drozdi.png"), 200, 200);
	Window window;
	Vozik vozik;
	public static Panel hraPanel;
	static Thread thread;
	private long cas;
	public Level2(Window window) {
		this.window = window;
		window.setTitle("Šňupejte droždí - Level 2 - NÁKUP");
		base(window);
		window.repaint();

		thread = Thread.currentThread();
		synchronized (thread) {
			try {
				thread.wait();
			} catch (InterruptedException ex) {
				System.out.println("CHYBA -- Level2" + ex);
			}
		}
		System.out.println("Level2 -- KONEC    " + Thread.currentThread());
	}
	public void setCas(long cas){
		cas = cas;
	}
	public void saveTime() {
		cas = System.currentTimeMillis() - cas;
		if (NesnupejteDrozdi.casLevel2 > cas) {
			NesnupejteDrozdi.casLevel2 = cas;
		}
	}

	void base(Window window) {
		window.smazat();
		window.defOkno();

		window.otazkyLabel.setLayout(null);
		window.otazkyLabel.setFont(new Font("Consolas", Font.PLAIN, 35));
		window.otazkyLabel.setForeground(Color.white);
		window.otazkyLabel.setBounds(0, window.windowHeight * 5 / 8, window.windowWidth, window.windowHeight * 1 / 8);
		window.otazkyLabel.setBackground(Color.red);
		window.otazkyLabel.setOpaque(false);
		window.otazkyLabel.setVerticalTextPosition(JLabel.BOTTOM);

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
