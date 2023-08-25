package org.drozdi.levels.level0;

import org.drozdi.game.FileManager;
import org.drozdi.game.NesnupejteDrozdi;
import org.drozdi.game.Window;
import org.drozdi.game.RelativeSize;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import static java.lang.Thread.currentThread;

public class Level0 {
	private Window window;
	Thread t;
	FileManager_lvl0 fileManagerLvl0 = new FileManager_lvl0();
	private JLabel background;


	public Level0(Window window) {
		this.window = window;
		t = currentThread();
		fileManagerLvl0.load();
		window.smazat();
		window.defOkno();
		window.setLayout(null);

		background = new JLabel();
		background.setBounds(new Rectangle(0, 0,100,170));
		background.setIcon(FileManager.loadImageIcon("Level0/drozdiNahore.png"));
		background.setBounds(0,0, RelativeSize.getMaxWindowSize().x, RelativeSize.getMaxWindowSize().y);
		window.add(background);

		zaklad(window, background);

		window.pack();
		window.setLocationRelativeTo(null);
		window.setVisible(true);

		synchronized (this) {
			try {
				wait();
			} catch (InterruptedException ex) {
				System.out.println("CHYBA -- Level0");
			}

		}
		window.smazat();
		System.out.println("KONEC Level0");
	}

	void zaklad(Window window, JLabel background) {

		window.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent e) {
            	ulozitCasy();
                System.exit(0);
            }
        });

		//levely tlacitka
		background.add(tlacitkoLevel(RelativeSize.rectangle(10, 35, 20, 10), 1));
		background.add(tlacitkoLevel(RelativeSize.rectangle(10, 50, 20, 10), 2));
		background.add(tlacitkoLevel(RelativeSize.rectangle(10, 65, 20, 10), 3));

		// level3 mapy
		background.add(tlacitkoLevel3Mapa(RelativeSize.rectangle(15, 80, 7, 10), 0));
		background.add(tlacitkoLevel3Mapa(RelativeSize.rectangle(25, 80, 7, 10), 1));
		background.add(tlacitkoLevel3Mapa(RelativeSize.rectangle(35, 80, 7, 10), 2));
		background.add(tlacitkoLevel3Mapa(RelativeSize.rectangle(45, 80, 7, 10), 3));
		background.add(tlacitkoLevel3Mapa(RelativeSize.rectangle(55, 80, 7, 10), 4));
		background.add(tlacitkoLevel3Mapa(RelativeSize.rectangle(65, 80, 7, 10), 5));

		JLabel statistika = new JLabel() {
			public void paintComponent(Graphics g) {
				Graphics2D g2d = (Graphics2D) g;
				g2d.setColor(java.awt.Color.blue);
				g2d.fillRect(0, 0, getSize().width, getSize().height);
				g2d.setColor(java.awt.Color.yellow);
				g.setFont(g.getFont().deriveFont(20f));
				g.drawString("Účet: " + NesnupejteDrozdi.account, 20, 30);
				g.setFont(g.getFont().deriveFont(12f));
				g.drawString("Časy:", 20, 100);

				g2d.drawString("Level1:           " + prevodCasu(NesnupejteDrozdi.casLevel1), 20, 120);
				g2d.drawString("Level2:           " + prevodCasu(NesnupejteDrozdi.casLevel2), 20, 140);
				for (int i = 0; i < NesnupejteDrozdi.mapTimeList.length; i++) {
					g2d.drawString("Level3/map" + i + ":       " + prevodCasu(NesnupejteDrozdi.mapTimeList[i]), 20, 160 + 20 * i);

				}
			}
		};
		statistika.setBounds(RelativeSize.rectangle(35, 35, 20, 40));

		JTextField novyUcet = new JTextField();
		novyUcet.setBounds(RelativeSize.percentageX(10, statistika.getWidth()), 50,
				RelativeSize.percentageX(60, statistika.getWidth()), 20);
		statistika.add(novyUcet);
		novyUcet.addActionListener(e -> {
			ulozitCasy();
			NesnupejteDrozdi.account = novyUcet.getText();
			NesnupejteDrozdi.accountPath = "login/" + NesnupejteDrozdi.account + ".json";
			File fileCheck = new File(Window.ziskatCestu(NesnupejteDrozdi.accountPath));
			if (fileCheck.exists() && !fileCheck.isDirectory()) {
				nacistCasy(statistika);
			}else {
				if (jeplatne(novyUcet.getText()) && !novyUcet.getText().isEmpty()) {
					zalozitUcet();
					nacistCasy(statistika);
				}else {
					NesnupejteDrozdi.account = "NeplatnyZnaky";
					NesnupejteDrozdi.accountPath = "login/" + NesnupejteDrozdi.account + ".json";
					nacistCasy(statistika);
				}
			}
			NesnupejteDrozdi.accountPath = "login/" + NesnupejteDrozdi.account + ".json";
			novyUcet.setText("");
			statistika.repaint();
			nacistCasy(statistika);

		});
		background.add(statistika);

		JButton discordTlacitko = new JButton() {
			@Override
			public void paintComponent(Graphics g) {
				Graphics2D g2d = (Graphics2D) g;
				BufferedImage img = FileManager.loadResource("Level0/discord.png");
				g2d.drawImage(img, 0, 0, getSize().width, getSize().height, null);
			}

		};

		discordTlacitko.addActionListener(e -> {
			openWebpage("https://discord.gg/AzBmaPWQGR");
		});

		discordTlacitko.setOpaque(false);
		discordTlacitko.setBounds(RelativeSize.rectangle(90, 80, 7, 10));
		discordTlacitko.setVisible(true);
		background.add(discordTlacitko);

		/**
		 * //ulozit tlacitko JButton ulozitTlacitko = new JButton() {
		 * 
		 * @Override public void paintComponent(Graphics g) { Graphics2D g2d =
		 * (Graphics2D) g; BufferedImage img = null; try { img = ImageIO.read(new
		 * File(Okno.ziskatCestu("Level0/save.png"))); } catch (Exception e) {
		 * System.out.println("CHYBA -- Level0 -- Nacteni obrazku save" + e); }
		 * g2d.drawImage(img, 0, 0, getSize().width, getSize().height, null);
		 * }
		 * 
		 * }; ulozitTlacitko.addActionListener(e -> { ulozitCasy(); });
		 * ulozitTlacitko.setOpaque(false);
		 * ulozitTlacitko.setBounds(RelatvniVelikost.rectangle(90, 65, 7, 10));
		 * ulozitTlacitko.setVisible(true);
		 * 
		 * pozadi.add(ulozitTlacitko);
		 **/

	}

	protected void zalozitUcet() {
		JsonObject jo1 = new JsonObject();
		jo1.addProperty("Level1", 2147483647);
		jo1.addProperty("Level2", 2147483647);
		for (int i = 0; i < NesnupejteDrozdi.mapTimeList.length; i++) {
			jo1.addProperty("Level3__" + i, 2147483647);
		}
		String jsonString = jo1.toString();
		try {
			Files.write(Paths.get(Window.ziskatCestu(NesnupejteDrozdi.accountPath)), jsonString.getBytes(), StandardOpenOption.CREATE);
			System.out.println("account {"+NesnupejteDrozdi.account +"} nove zalozen");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void ulozitCasy() {
		JsonObject jo = new JsonObject();
		jo.addProperty("Level1", NesnupejteDrozdi.casLevel1);
		jo.addProperty("Level2", NesnupejteDrozdi.casLevel2);
		for (int i = 0; i < NesnupejteDrozdi.mapTimeList.length; i++) {
			jo.addProperty("Level3__" + i, NesnupejteDrozdi.mapTimeList[i]);
		}
		String jsonString = jo.toString();

		try {
			InputStream inputStream = NesnupejteDrozdi.class.getClassLoader().getResourceAsStream(NesnupejteDrozdi.accountPath);
			if (inputStream != null) {
				Files.deleteIfExists(Paths.get(Window.ziskatCestu(NesnupejteDrozdi.accountPath)));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		try (OutputStream outputStream = Files.newOutputStream(Paths.get(Window.ziskatCestu(NesnupejteDrozdi.accountPath)), StandardOpenOption.CREATE)) {
			byte[] jsonStringBytes = jsonString.getBytes();
			outputStream.write(jsonStringBytes);
			System.out.println("ulozen account {" + NesnupejteDrozdi.account + "}");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void nacistCasy(JLabel statistika) {
		JsonObject jo;
		String jsonString = null;
			try {
				jsonString = new String(Files.readAllBytes(Paths.get(Window.ziskatCestu(NesnupejteDrozdi.accountPath))));
			} catch (IOException e) {
				// e.printStackTrace();
		}
		try {
			jo = new Gson().fromJson(jsonString, JsonObject.class);
			NesnupejteDrozdi.casLevel1 = jo.get("Level1").getAsLong();
			NesnupejteDrozdi.casLevel2 = jo.get("Level2").getAsLong();
			for (int i = 0; i < NesnupejteDrozdi.mapTimeList.length; i++) {
				NesnupejteDrozdi.mapTimeList[i] = jo.get("Level3__" + i).getAsLong();
			}
			statistika.repaint();
		} catch (Exception e) {
			e.printStackTrace();
			//System.out.println("nacitani uctu {" + Main.account +"} dokonceno");
		}
	}

	public static boolean jeplatne(String filename) {
		try {
			Paths.get(filename);
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	private JButton tlacitkoLevel3Mapa(Rectangle rect, int map) {
		// tlacitko Levely
		JButton tlacitko = new JButton() {
			@Override
			public void paintComponent(Graphics g) {
				Graphics2D g2d = (Graphics2D) g;
				BufferedImage img;
				img = FileManager.loadResource("Level0/map" + map + ".png");
				g2d.drawImage(img, 0, 0, getSize().width, getSize().height, null);
			}

		};
		tlacitko.addActionListener(e -> {
			NesnupejteDrozdi.progress = 3;
			NesnupejteDrozdi.setLevel3Level(map);
			konec();
		});
		window.hlPanel.setOpaque(false);
		tlacitko.setBounds(rect);
		tlacitko.setVisible(true);
		// tlacitko.repaint();
		return tlacitko;
	}

	public void konec() {
		synchronized (this) {
			notify();
		}

	}

	private JButton tlacitkoLevel( Rectangle rect, int cisloLevelu) {
		// tlacitko Levely
		JButton tlacitko = new JButton() {
			@Override
			public void paintComponent(Graphics g) {
				Graphics2D g2d = (Graphics2D) g;
				BufferedImage img = FileManager.loadResource("Level0/level" + cisloLevelu + ".png");
				g2d.drawImage(img, 0, 0, getSize().width, getSize().height, null);
			}

		};
		tlacitko.addActionListener(e -> {
			NesnupejteDrozdi.progress = cisloLevelu;
			konec();
		});
		window.hlPanel.setOpaque(false);
		tlacitko.setBounds(rect);
		tlacitko.setVisible(true);
		// tlacitko.repaint();
		return tlacitko;
	}

	private String prevodCasu(long cas) {
		if (cas == 2147483647) {
			return "??????";
		}
		// String vysledek = null;
		String sekundyText = Long.toString((cas % 60000) / 1000);
		if ((cas % 60000) / 1000 * 60 < 10) {
			sekundyText = "0" + sekundyText;
		}
		String milisekundyText = Long.toString((cas % 60000) % 1000);
		if ((cas % 60000) % 1000 < 10) {
			milisekundyText = "0" + milisekundyText;
		}
		return (cas / 60000 + ":" + sekundyText + ":" + milisekundyText);
	}

	// zkopcena metoda na otvirani
	public static void openWebpage(String urlString) {
		try {
			java.awt.Desktop.getDesktop().browse(new URL(urlString).toURI());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
