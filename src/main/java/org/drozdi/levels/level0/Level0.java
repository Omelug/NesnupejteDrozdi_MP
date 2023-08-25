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
	private Thread thread;
	private FileManager_lvl0 fileManagerLvl0 = new FileManager_lvl0();
	private JLabel background;

	public Level0(Window window) {
		this.window = window;
		thread = currentThread();
		fileManagerLvl0.load();
		window.smazat();
		window.defOkno();
		window.setLayout(null);

		background = new JLabel();
		background.setBounds(new Rectangle(0, 0,100,170));
		background.setIcon(FileManager.loadImageIcon("Level0/drozdiNahore.png"));
		background.setBounds(0,0, RelativeSize.getMaxWindowSize().x, RelativeSize.getMaxWindowSize().y);
		window.add(background);

		base(window, background);

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

	void base(Window window, JLabel background) {

		window.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent e) {
            	saveTime();
                System.exit(0);
            }
        });

		//levely tlacitka
		background.add(levelBtn(RelativeSize.rectangle(10, 35, 20, 10), 1));
		background.add(levelBtn(RelativeSize.rectangle(10, 50, 20, 10), 2));
		background.add(levelBtn(RelativeSize.rectangle(10, 65, 20, 10), 3));

		// level3 mapy
		background.add(levelBtn3Mapa(RelativeSize.rectangle(15, 80, 7, 10), 0));
		background.add(levelBtn3Mapa(RelativeSize.rectangle(25, 80, 7, 10), 1));
		background.add(levelBtn3Mapa(RelativeSize.rectangle(35, 80, 7, 10), 2));
		background.add(levelBtn3Mapa(RelativeSize.rectangle(45, 80, 7, 10), 3));
		background.add(levelBtn3Mapa(RelativeSize.rectangle(55, 80, 7, 10), 4));
		background.add(levelBtn3Mapa(RelativeSize.rectangle(65, 80, 7, 10), 5));

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

				g2d.drawString("Level1:           " + timeTransform(NesnupejteDrozdi.casLevel1), 20, 120);
				g2d.drawString("Level2:           " + timeTransform(NesnupejteDrozdi.casLevel2), 20, 140);
				for (int i = 0; i < NesnupejteDrozdi.mapTimeList.length; i++) {
					g2d.drawString("Level3/map" + i + ":       " + timeTransform(NesnupejteDrozdi.mapTimeList[i]), 20, 160 + 20 * i);

				}
			}
		};
		statistika.setBounds(RelativeSize.rectangle(35, 35, 20, 40));

		JTextField newAccount = new JTextField();
		newAccount.setBounds(RelativeSize.percentageX(10, statistika.getWidth()), 50,
				RelativeSize.percentageX(60, statistika.getWidth()), 20);
		statistika.add(newAccount);
		newAccount.addActionListener(e -> {
			saveTime();
			NesnupejteDrozdi.account = newAccount.getText();
			NesnupejteDrozdi.accountPath = "login/" + NesnupejteDrozdi.account + ".json";
			File fileCheck = new File(Window.ziskatCestu(NesnupejteDrozdi.accountPath));
			if (fileCheck.exists() && !fileCheck.isDirectory()) {
				updateTimes(statistika);
			}else {
				if (isValid(newAccount.getText()) && !newAccount.getText().isEmpty()) {
					createAccount();
					updateTimes(statistika);
				}else {
					NesnupejteDrozdi.account = "NeplatnyZnaky";
					NesnupejteDrozdi.accountPath = "login/" + NesnupejteDrozdi.account + ".json";
					updateTimes(statistika);
				}
			}
			NesnupejteDrozdi.accountPath = "login/" + NesnupejteDrozdi.account + ".json";
			newAccount.setText("");
			statistika.repaint();
			updateTimes(statistika);

		});
		background.add(statistika);

		JButton discordBtn = new JButton() {
			@Override
			public void paintComponent(Graphics graphics) {
				Graphics2D g2d = (Graphics2D) graphics;
				BufferedImage img = FileManager.loadResource("Level0/discord.png");
				g2d.drawImage(img, 0, 0, getSize().width, getSize().height, null);
			}

		};

		discordBtn.addActionListener(e -> {
			openWebpage("https://discord.gg/AzBmaPWQGR");
		});

		discordBtn.setOpaque(false);
		discordBtn.setBounds(RelativeSize.rectangle(90, 80, 7, 10));
		discordBtn.setVisible(true);
		background.add(discordBtn);

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
		 * }; ulozitTlacitko.addActionListener(e -> { saveTime(); });
		 * ulozitTlacitko.setOpaque(false);
		 * ulozitTlacitko.setBounds(RelatvniVelikost.rectangle(90, 65, 7, 10));
		 * ulozitTlacitko.setVisible(true);
		 * 
		 * pozadi.add(ulozitTlacitko);
		 **/

	}

	protected void createAccount() {
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

	private void saveTime() {
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

	private void updateTimes(JLabel statistic) {
		JsonObject jo;
		String jsonString = null;
			try {
				jsonString = new String(Files.readAllBytes(Paths.get(Window.ziskatCestu(NesnupejteDrozdi.accountPath))));
			} catch (IOException e) {
				e.printStackTrace();
		}
		try {
			jo = new Gson().fromJson(jsonString, JsonObject.class);
			NesnupejteDrozdi.casLevel1 = jo.get("Level1").getAsLong();
			NesnupejteDrozdi.casLevel2 = jo.get("Level2").getAsLong();
			for (int i = 0; i < NesnupejteDrozdi.mapTimeList.length; i++) {
				NesnupejteDrozdi.mapTimeList[i] = jo.get("Level3__" + i).getAsLong();
			}
			statistic.repaint();
		} catch (Exception e) {
			e.printStackTrace();
			//System.out.println("nacitani uctu {" + Main.account +"} dokonceno");
		}
	}

	public static boolean isValid(String filename) {
		try {
			Paths.get(filename);
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	private JButton levelBtn3Mapa(Rectangle rect, int map) {
		JButton button = new JButton() {
			@Override
			public void paintComponent(Graphics g) {
				Graphics2D g2d = (Graphics2D) g;
				BufferedImage img;
				img = FileManager.loadResource("Level0/map" + map + ".png");
				g2d.drawImage(img, 0, 0, getSize().width, getSize().height, null);
			}

		};
		button.addActionListener(e -> {
			NesnupejteDrozdi.progress = 3;
			NesnupejteDrozdi.setLevel3Level(map);
			end();
		});
		window.hlPanel.setOpaque(false);
		button.setBounds(rect);
		button.setVisible(true);
		return button;
	}

	public void end() {
		synchronized (this) {
			notify();
		}

	}

	private JButton levelBtn( Rectangle rect, int levelNumber) {
		JButton button = new JButton() {
			@Override
			public void paintComponent(Graphics g) {
				Graphics2D g2d = (Graphics2D) g;
				BufferedImage img = FileManager.loadResource("Level0/level" + levelNumber + ".png");
				g2d.drawImage(img, 0, 0, getSize().width, getSize().height, null);
			}
		};

		button.addActionListener(e -> {
			NesnupejteDrozdi.progress = levelNumber;
			end();
		});
		window.hlPanel.setOpaque(false);
		button.setBounds(rect);
		button.setVisible(true);
		return button;
	}

	private String timeTransform(long cas) {
		if (cas == 2147483647) {
			return "??????";
		}
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
