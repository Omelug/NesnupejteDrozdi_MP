package org.drozdi.levels.level2;

import org.drozdi.game.RelativeSize;
import org.drozdi.game.Window;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.util.ArrayList;
import java.util.Random;


public class Panel extends JPanel implements Runnable {
	Vozik vozik;
	Thread hraThread;
	Window mistniWindow;
	ImageIcon vozikimg;
	ArrayList<Food> pult = new ArrayList<>();
	Random random = new Random();
	// Rectangle f = new Rectangle();
	// long stopky = 2 * 60 * 60;
	long stopky = 2 *60*60;
	// nacteni obrazku
	public static ImageIcon drozdi;
	public static ImageIcon kecup;
	public static ImageIcon clock;
	public Move keyK;
	int pocetDrozdi = 0, pocetKecup = 0;
	int drozdiMax = 20;
	JLabel pocitadloDrozdi, pocitadloKecup, pocitadloStopky;
	JButton startTlacitko = new JButton();
	int konecDrozdi = 0; // pro konecne padajici drozdi
	Level2 level2;
	public Panel(Window window, Level2 level2) {
		this.level2 = level2;

		setBounds(RelativeSize.rectangle(0, 0 ,100, 85));
		setDoubleBuffered(true);

		mistniWindow = window;

		Food.size = RelativeSize.getMaximum().x/40;
		drozdi = Window.resizeImage(new ImageIcon("rsc/drozdi.png"), Food.size, Food.size);
		kecup = Window.resizeImage(new ImageIcon("rsc/Level2/kecup.png"), Food.size, Food.size);
		clock = Window.resizeImage(new ImageIcon("rsc/Level2/time.png"), Food.size, Food.size);
		
		// setUpeni voziku
		Vozik vozik = new Vozik(getWidth(), getHeight());
		this.vozik = vozik;

		keyK = new Move(vozik, this);
		addKeyListener(keyK);
		
		setFocusable(true);
		vozikimg = vozik.imgR;

		//window.questionPanel.setBounds(0, window.windowHeight - 150, window.windowWidth, 50);
		// window.questionPanel.getHeight());
		window.questionPanel.setBounds(RelativeSize.rectangle(0,85,100,5));
		window.questionLabel.setVerticalTextPosition(JLabel.CENTER);
		mistniWindow.questionLabel.setFont(new Font("Consolas", Font.PLAIN, 25));
		mistniWindow.questionLabel.setText("Nekupuj to droždí! Musíš odolat! ");

		window.answerPanel.setBounds(RelativeSize.rectangle(0,90,100,10));
		window.answerPanel.setBackground(Color.black);

		JLabel pocitadloDrozdi = new JLabel();
		pocitadloDrozdi.setLayout(null);
		pocitadloDrozdi.setBounds(0, 0, window.windowWidth * 1 / 8, 65);
		pocitadloDrozdi.setBounds(RelativeSize.rectangle(0, 0, 10, 50, window.answerPanel.getWidth(), window.answerPanel.getHeight()));
		pocitadloDrozdi.setFont(new Font("Consolas", Font.PLAIN, 25));
		pocitadloDrozdi.setForeground(Color.white);
		pocitadloDrozdi.setHorizontalAlignment(JLabel.CENTER);
		pocitadloDrozdi.setIcon(drozdi);
		window.answerPanel.add(pocitadloDrozdi);

		this.pocitadloDrozdi = pocitadloDrozdi;

		JLabel pocitadloKecup = new JLabel();
		pocitadloKecup.setLayout(null);
		pocitadloKecup.setBounds(RelativeSize.rectangle(10, 0, 10, 50, window.answerPanel.getWidth(), window.answerPanel.getHeight()));
		pocitadloKecup.setFont(new Font("Consolas", Font.PLAIN, 25));
		pocitadloKecup.setForeground(Color.white);
		pocitadloKecup.setHorizontalAlignment(JLabel.LEFT);
		pocitadloKecup.setIcon(kecup);
		this.pocitadloKecup = pocitadloKecup;
		window.answerPanel.add(pocitadloKecup);

		JLabel pocitadloStopky = new JLabel();
		pocitadloStopky.setLayout(null);
		pocitadloStopky.setBounds(RelativeSize.rectangle(85, 0, 13, 60, window.answerPanel.getWidth(), window.answerPanel.getHeight()));
		pocitadloStopky.setFont(new Font("Consolas", Font.PLAIN, 35));
		pocitadloStopky.setForeground(Color.white);
		pocitadloStopky.setHorizontalAlignment(JLabel.RIGHT);
		pocitadloStopky.setHorizontalTextPosition(JLabel.LEFT);
		pocitadloStopky.setVisible(true);
		pocitadloStopky.setIcon(clock);
		this.pocitadloStopky = pocitadloStopky;
		window.answerPanel.add(pocitadloStopky);
		// tlacitka dole
		startTlacitko.setLayout(null);
		startTlacitko.setBounds(RelativeSize.rectangle(40, 10, 20, 80, window.answerPanel.getWidth(), window.answerPanel.getHeight()));
		startTlacitko.setFont(new Font("Consolas", Font.BOLD, 25));
		startTlacitko.setText("START");
		startTlacitko.setBackground(Color.green);
		startTlacitko.setOpaque(true);
		startTlacitko.setHorizontalAlignment(JLabel.CENTER);

		window.answerPanel.add(startTlacitko);

		startTlacitko.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				JButton b = (JButton) e.getSource();
				ButtonModel m = b.getModel();
				boolean isPressed = m.isPressed();
				if (isPressed) {
					startTlacitko.setVisible(false);
					level2.setCas(System.currentTimeMillis());
					startPanel();
					startTlacitko.removeChangeListener(this);
				}
			}
		});

	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g;
		g2d.setColor(Color.gray);
		g2d.fillRect(0, 0, getWidth(), getHeight());

		vozikimg.paintIcon(this, g, vozik.misto, vozik.souradniceY);

		for (int i = 0; i < pult.size(); i++) {
			if (pult.get(i).name.equals("drozdi")) {
				drozdi.paintIcon(this, g, pult.get(i).x, pult.get(i).y);
			}
			if (pult.get(i).name.equals("kecup")) {
				kecup.paintIcon(this, g, pult.get(i).x, pult.get(i).y);
			}
		}
		if (stopky >= 0) {
			String sekundyText = Long.toString((stopky % 3600) / 60);
			if ((stopky % 3600) / 60 < 10) {
				sekundyText = "0" + sekundyText;
			}
			String milisekundyText = Long.toString(stopky % 60);
			if (stopky % 60 < 10) {
				milisekundyText = "0" + milisekundyText;
			}
			pocitadloStopky.setText(stopky / 3600 + ":" + sekundyText + ":" + milisekundyText);
		} else {
			if (konecDrozdi % 20 == 0) {
				radaDrozdi();
			}
			konecDrozdi++;
		}
		pocitadloDrozdi.setText(pocetDrozdi + "/" + drozdiMax);
		pocitadloKecup.setText("" + pocetKecup);
		if (pocetDrozdi >= drozdiMax) {
			hraThread = null;
		}
		g2d.dispose();

	}

	int FPS = 200;

	@Override
	public void run() {
		double intervalCas = (double) 1_000_000_000 / FPS; // prevod jednotek
		double delta = 0;
		long minulyCas = System.nanoTime();
		long time;

		while (hraThread != null) {
			time = System.nanoTime();
			delta += (time - minulyCas) / intervalCas;
			minulyCas = time;

			if (delta > 1) {
				update();
				repaint();
				delta--;
				stopky--;
			}
		}
		konecnyScreen();
		System.out.println("END Panelu  " + Thread.currentThread());
	}

	public void konecnyScreen() {
		mistniWindow.questionLabel.setText("Jsi toho nakoupil nějak moc, to nebude jen na vaření...");

		startTlacitko.setText("JÍT Z OBCHODU");
		startTlacitko.setBackground(Color.cyan);
		startTlacitko.setVisible(true);
		startTlacitko.addChangeListener(new ChangeListener() {

			@Override
			synchronized public void stateChanged(ChangeEvent e) {
				JButton b = (JButton) e.getSource();
				ButtonModel m = b.getModel();
				boolean isPressed = m.isPressed();
				if (isPressed) {
					startTlacitko.setVisible(false);
					startTlacitko.removeChangeListener(this);
					System.out.println("END button  " + Thread.currentThread());
					level2.saveTime();
					end();
				}
			}

		});
	}
	public void end() {
		synchronized (Level2.thread) {
			try {
				mistniWindow.clean();
				Level2.thread.notify();
			} catch (Exception e2) {
				System.out.println("ERROR  -- END button" + Thread.currentThread());
			}
		}
	}
	private void update() {
		// vozik pohyb
		if (keyK.vozikJede) {
			if (vozik.otocenDoPrava) {
				vozikimg = vozik.imgR;
				vozik.misto += vozik.speed;

				if (vozik.misto + vozik.sirka > getWidth()) {
					vozik.misto = getWidth() - vozik.sirka;
				}
			}
			if (!vozik.otocenDoPrava) {
				vozik.misto -= vozik.speed;
				vozikimg = vozik.imgL;
				if (vozik.misto < 0) {
					vozik.misto = 0;
				}
			}
		}
		// generace jidla
		if (random.nextInt(20) == 19) {
			switch (random.nextInt(2)) {
			case 0: {
				pult.add(new Food("drozdi", random.nextInt(40) * Food.size, -Food.size));
			}
			case 1: {
				pult.add(new Food("kecup", random.nextInt(40) * Food.size, -Food.size));
			}
			}
		}
		// jidlo pada
		for (int i = 0; i < pult.size(); i++) {
			pult.get(i).y += Food.speed;
			if (pult.get(i).y > getHeight()) {
				// System.out.println("zniceno");
				pult.remove(i);
			} else if (pult.get(i).collision(vozik)) {
				switch (pult.get(i).name) {
				case "drozdi": {
					pocetDrozdi++;
				}
				case "kecup": {
					pocetKecup++;
				}
				}
				pult.remove(i);
			}
		}
	}

	public void startPanel() {
		if (hraThread == null) {
			hraThread = new Thread(this);
			hraThread.start();
			System.out.println("Thread PANEL začal " + Thread.currentThread());
		}
	}

	public void radaDrozdi() {
		for (int i = 0; i < 40; i++) {
			pult.add(new Food("drozdi", i * Food.size, -Food.size));
		}
	}
}
