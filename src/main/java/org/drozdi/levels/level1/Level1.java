package org.drozdi.levels.level1;

import org.drozdi.game.FileManager;
import org.drozdi.game.NesnupejteDrozdi;
import org.drozdi.game.Window;
import org.drozdi.game.RelativeSize;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.*;
import java.util.Random;

import javax.swing.*;
import javax.swing.border.LineBorder;

public class Level1 implements ActionListener {
	ImageIcon drozdi = Window.resizeImage(FileManager.loadImageIcon("drozdi.png"), 200, 200);
	JButton tlacitko;
	JLabel tlacitkoLabel;
	int nasnupano = 0;
	Window mistniWindow;
	int stav = 0; // urcuje co maji delat tlacitka pri zmacknuti
	String a = null;
	boolean t1open = true;
	boolean t2open = true;
	String otazka = "Našel jsi na zemi droždí, co s tím ?";
	public JButton tlacitko1;
	public JButton tlacitko2;
	FileManager_lvl1 filemanager;
public Level1(Window window) {
		mistniWindow = window;
		
		window.smazat();
		base(window);
		window.setUpOtazku(window, window.answerPanel, window.otazkyLabel, tlacitko1, tlacitko2, otazka,
				"Vyhodit droždí do koše", "Vyšňupat droždí");


		// tlacitka nasteveni chovani
		tlacitko2.addChangeListener(e -> {
			JButton b = (JButton) e.getSource();
			ButtonModel m = b.getModel();
			boolean isPressedAndArmed = m.isPressed() && m.isArmed();
			if (t1open) {
				a = mistniWindow.otazkyLabel.getText();
			}
			if (isPressedAndArmed) {
				if (stav == 0 || stav == 1) {
					mistniWindow.otazkyLabel.setText("Na tlačítko nešahej a seber to droždí !");
					if (stav == 0) {
						stav = 1; // hrac muze klikat na drozdi
					}
				}
				if (stav == 2) {
					synchronized (this) {
						mistniWindow.smazat();
						notify();
					}
				}
				t1open = false;
			} else {
				mistniWindow.otazkyLabel.setText(a);
				t1open = true;
			}
		});
		tlacitko1.addChangeListener(e -> {
			JButton b = (JButton) e.getSource();
			ButtonModel m = b.getModel();
			boolean isPressedAndArmed = m.isPressed() && m.isArmed();
			if (t2open) {
				a = mistniWindow.otazkyLabel.getText();
			}
			if (isPressedAndArmed) {
				if (stav == 0 || stav == 1) {
					mistniWindow.otazkyLabel.setText("Bohužel, koš tu nevidím");
				}
				if (stav == 2) {
					System.out.println(stav + " tl 1");
					synchronized (this) {
						notify();
					}
				}

				t2open = false;
			} else {
				mistniWindow.otazkyLabel.setText(a);
				t2open = true;
			}
		});
		window.hlPanel.add(tlacitko);
		// prekresleni (pridani grafick7ch zmen)
		window.repaint();
		// cekani dokud nezmackne konecna tlacitko
		long cas = System.currentTimeMillis(); 
		synchronized (this) {
			try {
				wait();
			} catch (InterruptedException ex) {
				System.out.println("CHYBA -- Level1");
			}
		}
		//kontrola nejlepsiho casu
		cas = System.currentTimeMillis() - cas;
		if (NesnupejteDrozdi.casLevel1 > cas) {
			NesnupejteDrozdi.casLevel1 = cas;
		}
		System.out.println("Level1 čas: " + cas);
		window.answerPanel.remove(tlacitko1);
		window.answerPanel.remove(tlacitko2);
		window.hlPanel.remove(tlacitko);
		// window.repaint();

		System.out.println("Level1 -- KONEC");

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// System.out.println("Tlacitko");
		if (stav != 0) {
			if (nasnupano >= 100) {
				mistniWindow.setUpOtazku(mistniWindow, mistniWindow.answerPanel, mistniWindow.otazkyLabel, tlacitko1,
						tlacitko2, "Našňupáno prostě hodně droždí !! ", "Jít dál", "Jít dál");
				stav = 2; // aby se zmenily listenery tlacitka na konecnou funkci

			} else {
				nasnupano = nasnupano + new Random().nextInt(7);
				mistniWindow.otazkyLabel.setText("Našňupáno " + nasnupano + "% droždí !!");
			}
		}

	}

	void base(Window window) {
		window.defOkno();
		filemanager = new FileManager_lvl1();
		tlacitko1 = new JButton();
		tlacitko2 = new JButton();
		tlacitko = new JButton();

		window.otazkyLabel.setLayout(null);
		window.otazkyLabel.setFont(new Font("Consolas", Font.PLAIN, 35));
		window.otazkyLabel.setForeground(Color.white);
		window.otazkyLabel.setOpaque(false);
		window.otazkyLabel.setBorder(new LineBorder(Color.gray, 30));
		window.otazkyLabel.setVerticalAlignment(JLabel.TOP);
		
		window.remove(window.hlPanel);
		
		window.hlPanel = new JPanel(){
			
			@Override
			public void paintComponent(Graphics g) {
				Graphics2D g2d = (Graphics2D)g;
				g2d.fillRect(0,0,100,100);
				g2d.drawImage(FileManager_lvl1.cesta, 0, 0, getSize().width,getSize().height, null);
			}
		};
		window.add(window.hlPanel);
		window.hlPanel.setLayout(null);
		window.hlPanel.setBounds(RelativeSize.rectangle(20,0,60,75));
		window.hlPanel.setOpaque(false);
		
		window.questionPanel.setBounds(RelativeSize.rectangle(0,75,100,10));
		window.questionPanel.setBackground(Color.gray);
		window.questionPanel.setVisible(true);
		
		window.answerPanel.setLayout(null);
		window.answerPanel.setBounds(RelativeSize.rectangle(0,85,100,15));
		window.answerPanel.setBackground(Color.gray);
		window.answerPanel.setVisible(true);
		window.answerPanel.setOpaque(false);

		// drozdi talcitko
		tlacitko.setBounds(window.hlPanel.getWidth() / 2 - 100, window.hlPanel.getHeight() / 2 - 100, 200, 200);
		tlacitko.setIcon(drozdi);
		tlacitko.addActionListener(this);
		tlacitko.setOpaque(false);
		tlacitko.setContentAreaFilled(false);
		tlacitko.setFocusable(false);
		tlacitko.setBorder(null);
		tlacitko.setVisible(true);
		window.hlPanel.add(tlacitko);
		
		//reklamni panely
		JLabel reklamniPannel1 =  new JLabel() {
			@Override
			public void paintComponent(Graphics g) {
				Graphics2D g2d = (Graphics2D)g;
				g2d.drawImage(FileManager_lvl1.reklamnihoBanner, 0, 0, getSize().width,getSize().height, null);
			}
		};
		reklamniPannel1.setOpaque(false);
		reklamniPannel1.setBounds(RelativeSize.rectangle(0,0,20,75));
		window.add(reklamniPannel1);
		
		JLabel reklamniPannel2 =  new JLabel() {
			@Override
			public void paintComponent(Graphics g) {
				Graphics2D g2d = (Graphics2D)g;
				g2d.drawImage(FileManager_lvl1.reklamnihoBanner, 0, 0, getSize().width,getSize().height, null);
			}
		};
		reklamniPannel2.setOpaque(false);
		reklamniPannel2.setBounds(RelativeSize.rectangle(80,0,20,75));
		window.add(reklamniPannel2);
	}
}
