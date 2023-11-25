package drozdi.levels.level0;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import drozdi.game.FileManager;
import drozdi.game.NesnupejteDrozdi;
import drozdi.game.RelativeSize;
import drozdi.game.Window;
import drozdi.levels.level3.client.GameClient;

import javax.swing.*;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.file.*;
import java.time.LocalTime;

import static java.lang.Thread.currentThread;

public class Level0 {
	private final Window window;
	private final Thread thread;
	private final FileManager_lvl0 fileManagerLvl0 = new FileManager_lvl0();
	private final JLabel background;
	private static JButton checkConnection;
	private static JButton connectBtn;

	public Level0(Window window) {
		this.window = window;
		thread = currentThread();
		fileManagerLvl0.load();
		window.clean();
		window.defineWindow();
		window.setLayout(null);

		background = new JLabel();
		background.setBounds(new Rectangle(0, 0,100,170));
		background.setIcon(FileManager.loadImageIcon("Level0/drozdiTitle.png"));
		background.setBounds(0,0, RelativeSize.getMaxWindowSize().x, RelativeSize.getMaxWindowSize().y);
		window.add(background);

		base(window, background);

		window.pack();
		window.setLocationRelativeTo(null);
		window.setVisible(true);

		//TODO testvaci;

		synchronized (this) {
			try {
				wait();
			} catch (InterruptedException ex) {
				System.out.println("ERROR -- Level0");
			}
		}
		window.clean();
		System.out.println("END Level0");
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
		background.add(levelBtn3Map(RelativeSize.rectangle(15, 80, 7, 10), 0));
		background.add(levelBtn3Map(RelativeSize.rectangle(25, 80, 7, 10), 1));
		background.add(levelBtn3Map(RelativeSize.rectangle(35, 80, 7, 10), 2));
		background.add(levelBtn3Map(RelativeSize.rectangle(45, 80, 7, 10), 3));
		background.add(levelBtn3Map(RelativeSize.rectangle(55, 80, 7, 10), 4));
		background.add(levelBtn3Map(RelativeSize.rectangle(65, 80, 7, 10), 5));

		JLabel statistics = new JLabel() {
			@Override
			public void paintComponent(Graphics g) {
				Graphics2D g2d = (Graphics2D) g;
				g2d.setColor(java.awt.Color.blue);
				g2d.fillRect(0, 0, getSize().width, getSize().height);
				g2d.setColor(java.awt.Color.yellow);
				g.setFont(g.getFont().deriveFont(20f));
				g.drawString("Účet: " + NesnupejteDrozdi.account, 20, 30);
				g.setFont(g.getFont().deriveFont(12f));
				g.drawString("Časy:", 20, 100);

				g2d.drawString("Level1:           " + timeTransform(NesnupejteDrozdi.timeLevel1), 20, 120);
				g2d.drawString("Level2:           " + timeTransform(NesnupejteDrozdi.timeLevel2), 20, 140);
				for (int i = 0; i < NesnupejteDrozdi.mapTimeList.length; i++) {
					g2d.drawString("walls/map" + i + ":       " + timeTransform(NesnupejteDrozdi.mapTimeList[i]), 20, 160 + 20 * i);

				}
			}
		};
		statistics.setBounds(RelativeSize.rectangle(35, 35, 20, 40));

		JTextField newAccount = new JTextField();
		newAccount.setBounds(RelativeSize.percentageX(10, statistics.getWidth()), 50, RelativeSize.percentageX(60, statistics.getWidth()), 20);
		statistics.add(newAccount);
		newAccount.addActionListener(e -> {
			///TODO saveTime();
			NesnupejteDrozdi.account = newAccount.getText();

			File fileCheck = new File(Window.getPath(NesnupejteDrozdi.accountPath));
			if (!(fileCheck.exists() && !fileCheck.isDirectory())) {
				if (isValid(newAccount.getText()) && !newAccount.getText().isEmpty()) {
					//TODO createAccount();
				}else {
					NesnupejteDrozdi.account = "NeplatnyZnaky";
				}
			}

			NesnupejteDrozdi.accountPath = "login/" + NesnupejteDrozdi.account + ".json";
			newAccount.setText("");
			updateTimes(statistics);

			statistics.repaint();
		});

		JTextField newIpAddress = new JTextField();
		newIpAddress.setBounds(RelativeSize.percentageX(5, statistics.getWidth()), 300, RelativeSize.percentageX(50, statistics.getWidth()), 20);
		statistics.add(newIpAddress);
		newIpAddress.setText(String.valueOf(GameClient.getServerIp()));
		newIpAddress.addActionListener(e -> {
			try {
				GameClient.setServerIp(InetAddress.getByName(newIpAddress.getText()));
			} catch (UnknownHostException ex) {
				ex.printStackTrace();
			}
		});

		JTextField newUDPPort = new JTextField();
		newUDPPort.setBounds(RelativeSize.percentageX(55, statistics.getWidth()), 300,RelativeSize.percentageX(20, statistics.getWidth()), 20);
		statistics.add(newUDPPort);
		newUDPPort.setText(String.valueOf(GameClient.getServerUDPPort()));
		((AbstractDocument) newUDPPort.getDocument()).setDocumentFilter(new IntegerDocumentFilter());
		newUDPPort.addActionListener(e -> {
			GameClient.setServerUDPPort(Integer.parseInt(newUDPPort.getText()));
		});

		JTextField newTCPPort = new JTextField();
		newTCPPort.setBounds(RelativeSize.percentageX(55, statistics.getWidth()), 320,RelativeSize.percentageX(20, statistics.getWidth()), 20);
		statistics.add(newTCPPort);
		newTCPPort.setText(String.valueOf(GameClient.getServerTCPPort()));
		((AbstractDocument) newTCPPort.getDocument()).setDocumentFilter(new IntegerDocumentFilter());
		newTCPPort.addActionListener(e -> {
			GameClient.setServerTCPPort(Integer.parseInt(newTCPPort.getText()));
		});

		checkConnection = new JButton();
		checkConnection.setBounds(RelativeSize.percentageX(5, statistics.getWidth()), 345,RelativeSize.percentageX(70, statistics.getWidth()), 20);
		statistics.add(checkConnection);
		checkConnection.setText("check");
		checkConnection.addActionListener(e -> {
			//NesnupejteDrozdi.getClient().sendData("ping".getBytes());
			//NesnupejteDrozdi.getClient().getMap();
		});
		connectBtn = new JButton();
		connectBtn.setBounds(RelativeSize.percentageX(5, statistics.getWidth()), 365,RelativeSize.percentageX(70, statistics.getWidth()), 20);
		statistics.add(connectBtn);
		connectBtn.setText("connect");
		connectBtn.addActionListener(e -> {
			NesnupejteDrozdi.setProgress(4);//TODO delete magic number
			end();
		});

		background.add(statistics);

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
		 * //ulozit button JButton ulozitTlacitko = new JButton() {
		 * 
		 * @Override public void paintComponent(Graphics g) { Graphics2D g2d =
		 * (Graphics2D) g; BufferedImage img = null; try { img = ImageIO.read(new
		 * File(Okno.getPath("Level0/save.png"))); } catch (Exception e) {
		 * System.out.println("ERROR -- Level0 -- Nacteni obrazku save" + e); }
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

	public static void changeConnectionStatus(){
		checkConnection.setText(" ping-pong " + LocalTime.now());
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
			Files.write(Paths.get(Window.getPath(NesnupejteDrozdi.accountPath)), jsonString.getBytes(), StandardOpenOption.CREATE);
			System.out.println("account {"+NesnupejteDrozdi.account +"} nove zalozen");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void saveTime() {
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("Level1", NesnupejteDrozdi.timeLevel1);
		jsonObject.addProperty("Level2", NesnupejteDrozdi.timeLevel2);
		for (int i = 0; i < NesnupejteDrozdi.mapTimeList.length; i++) {
			jsonObject.addProperty("Level3__" + i, NesnupejteDrozdi.mapTimeList[i]);
		}
		String jsonString = jsonObject.toString();
		try {
			InputStream inputStream = getClass().getClassLoader().getResourceAsStream(NesnupejteDrozdi.accountPath);

			if (inputStream != null) {
				Path tempOutputFile = Files.createTempFile("temp", ".json");

				FileWriter fileWriter = new FileWriter(tempOutputFile.toFile());
				fileWriter.write(jsonString);
				fileWriter.close();

				Path resourcePathToFile = Paths.get(getClass().getClassLoader().getResource(NesnupejteDrozdi.accountPath).toURI());
				Files.copy(tempOutputFile, resourcePathToFile, StandardCopyOption.REPLACE_EXISTING);
				Files.delete(tempOutputFile);

				System.out.println("Saved JSON content to resource file: " + NesnupejteDrozdi.accountPath);
			} else {
				System.out.println("Resource not found: " + NesnupejteDrozdi.accountPath);
			}
		} catch (IOException | URISyntaxException e) {
			e.printStackTrace();
		}

		/*try {
			InputStream inputStream = FileManager.loadJsonInput(NesnupejteDrozdi.accountPath);
			if (inputStream != null) {
				Files.deleteIfExists(Paths.get(Window.getPath(NesnupejteDrozdi.accountPath)));
			}
			OutputStream outputStream = FileManager.loadJsonOutput(NesnupejteDrozdi.accountPath);//Files.newOutputStream(Paths.get(Window.getPath(NesnupejteDrozdi.accountPath)), StandardOpenOption.CREATE);
			byte[] jsonStringBytes = jsonString.getBytes();
			outputStream.write(jsonStringBytes);
			System.out.println("ulozen account {" + NesnupejteDrozdi.account + "}");
		} catch (IOException e) {
			e.printStackTrace();
		}*/

	}

	private void updateTimes(JLabel statistic) {
		JsonObject jsonObject;
		String jsonString = null;
			try {
				jsonString = new String(Files.readAllBytes(Paths.get(Window.getPath(NesnupejteDrozdi.accountPath))));
			} catch (IOException e) {
				e.printStackTrace();
		}
		try {
			jsonObject = new Gson().fromJson(jsonString, JsonObject.class);
			NesnupejteDrozdi.timeLevel1 = jsonObject.get("Level1").getAsLong();
			NesnupejteDrozdi.timeLevel2 = jsonObject.get("Level2").getAsLong();
			for (int i = 0; i < NesnupejteDrozdi.mapTimeList.length; i++) {
				NesnupejteDrozdi.mapTimeList[i] = jsonObject.get("Level3__" + i).getAsLong();
			}
			statistic.repaint();
		} catch (Exception e) {
			e.printStackTrace();
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

	private JButton levelBtn3Map(Rectangle rect, int map) {
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
			NesnupejteDrozdi.setProgress(3);
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
			NesnupejteDrozdi.setProgress(levelNumber);
			end();
		});
		window.hlPanel.setOpaque(false);
		button.setBounds(rect);
		button.setVisible(true);
		return button;
	}

	private String timeTransform(long time) {
		if (time == 2147483647) {
			return "??????";
		}
		String sekundyText = Long.toString((time % 60000) / 1000);
		if ((time % 60000) / 1000 * 60 < 10) {
			sekundyText = "0" + sekundyText;
		}
		String milisekundyText = Long.toString((time % 60000) % 1000);
		if ((time % 60000) % 1000 < 10) {
			milisekundyText = "0" + milisekundyText;
		}
		return (time / 60000 + ":" + sekundyText + ":" + milisekundyText);
	}

	public static void openWebpage(String urlString) {
		try {
			java.awt.Desktop.getDesktop().browse(new URL(urlString).toURI());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	static class IntegerDocumentFilter extends DocumentFilter {
		private static final int MAX_LENGTH = 5; // Maximum allowed length of the text

		@Override
		public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
			StringBuilder builder = new StringBuilder(string);
			for (int i = builder.length() - 1; i >= 0; i--) {
				char ch = builder.charAt(i);
				if (!Character.isDigit(ch)) {
					builder.deleteCharAt(i);
				}
			}

			if (fb.getDocument().getLength() + builder.length() <= MAX_LENGTH) {
				super.insertString(fb, offset, builder.toString(), attr);
			}
		}

		@Override
		public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
			if (text == null) {
				return;
			}

			StringBuilder builder = new StringBuilder(text);
			for (int i = builder.length() - 1; i >= 0; i--) {
				char ch = builder.charAt(i);
				if (!Character.isDigit(ch)) {
					builder.deleteCharAt(i);
				}
			}

			if (fb.getDocument().getLength() - length + builder.length() <= MAX_LENGTH) {
				super.replace(fb, offset, length, builder.toString(), attrs);
			}
		}
	}

}
