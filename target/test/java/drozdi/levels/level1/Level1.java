package drozdi.levels.level1;

import drozdi.game.Window;
import drozdi.game.FileManager;
import drozdi.game.NesnupejteDrozdi;
import drozdi.game.RelativeSize;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

public class Level1 implements ActionListener {
	ImageIcon drozdi = drozdi.game.Window.resizeImage(FileManager.loadImageIcon("drozdi.png"), 200, 200);
	private int snorted = 0;
	private int status = 0; // urcuje co maji delat tlacitka pri zmacknuti
	private String a = null;
	private boolean t1open = true;
	private boolean t2open = true;
	private final drozdi.game.Window window;
	private final JButton button1;
	private final JButton button2;
	private FileManager_lvl1 fileManagerLvl1;

public Level1(drozdi.game.Window window) {
	this.window = window;
	window.setTitle("Šňupejte droždí - Level 1 - PATH TO ADDICTION");
	window.clean();
	base(window);

	button1 = new JButton();
	button1.addChangeListener(event -> {
		JButton button = (JButton) event.getSource();
		ButtonModel buttonModel = button.getModel();
		boolean isPressedAndArmed = buttonModel.isPressed() && buttonModel.isArmed();
		if (t2open) {
			a = window.questionLabel.getText();
		}
		if (isPressedAndArmed) {
			if (status == 0 || status == 1) {
				window.questionLabel.setText("Unfortunately, I dont see any trashcan");
			}
			if (status == 2) {
				System.out.println(status + " tl 1");
				synchronized (this) {
					notify();
				}
			}

			t2open = false;
		} else {
			window.questionLabel.setText(a);
			t2open = true;
		}
	});
	window.hlPanel.add(button1);

	button2 = new JButton();
	button2.addChangeListener(event -> {
		JButton button = (JButton) event.getSource();
		ButtonModel buttonModel = button.getModel();
		boolean isPressedAndArmed = buttonModel.isPressed() && buttonModel.isArmed();
		if (t1open) {
			a = window.questionLabel.getText();
		}
		if (isPressedAndArmed) {
			if (status == 0 || status == 1) {
				window.questionLabel.setText("Na tlačítko nešahej a seber to droždí !");
				if (status == 0) {
					status = 1; // hrac muze klikat na drozdi
				}
			}
			if (status == 2) {
				synchronized (this) {
					window.clean();
					notify();
				}
			}
			t1open = false;
		} else {
			window.questionLabel.setText(a);
			t1open = true;
		}
	});
	window.hlPanel.add(button2);

	String question = "Našel jsi na zemi droždí, co s tím ?";
	String answer = "Vyhodit droždí do koše";
	String answer2 = "Vyšňupat droždí";
	window.setupQuestion(window.answerPanel, window.questionLabel, button1, button2, question, answer, answer2);

	window.repaint();


	long time = System.currentTimeMillis();
	synchronized (this) {
		try {
			wait();
		} catch (InterruptedException ex) {
			System.out.println("ERROR -- Level1");
		}
	}

	time = System.currentTimeMillis() - time;
	if (NesnupejteDrozdi.timeLevel1 > time) {
		NesnupejteDrozdi.timeLevel1 = time ;
	}

	System.out.println("Level1 time: " + time );
	/**window.answerPanel.remove(button1);
	window.answerPanel.remove(button2);*/

	System.out.println("Level1 -- END");

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (status != 0) {
			if (snorted >= 100) {
				String question = "Našňupáno prostě hodně droždí !! ";
				String answer = "Jít dál";
				window.setupQuestion( window.answerPanel, window.questionLabel, button1, button2, question, answer, answer);
				status = 2; // aby se zmenily listenery tlacitka na konecnou funkci
			} else {
				snorted = snorted + new Random().nextInt(7);
				window.questionLabel.setText("Našňupáno " + snorted + "% droždí !!");
			}
		}

	}

	void base(Window window) {
		window.defineWindow();
		fileManagerLvl1 = new FileManager_lvl1();

		window.questionLabel.setLayout(null);
		window.questionLabel.setFont(new Font("Consolas", Font.PLAIN, 35));
		window.questionLabel.setForeground(Color.white);
		window.questionLabel.setOpaque(false);
		window.questionLabel.setBorder(new LineBorder(Color.gray, 30));
		window.questionLabel.setVerticalAlignment(JLabel.TOP);

		window.remove(window.hlPanel);
		
		window.hlPanel = new JPanel(){
			@Override
			public void paintComponent(Graphics g) {
				Graphics2D g2d = (Graphics2D)g;
				g2d.fillRect(0,0,100,100);
				g2d.drawImage(FileManager_lvl1.path, 0, 0, getSize().width,getSize().height, null);
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

		JButton drozdiBtn = new JButton();
		drozdiBtn.setBounds(window.hlPanel.getWidth() / 2 - 100, window.hlPanel.getHeight() / 2 - 100, 200, 200);
		drozdiBtn.setIcon(drozdi);
		drozdiBtn.addActionListener(this);
		drozdiBtn.setOpaque(false);
		drozdiBtn.setContentAreaFilled(false);
		drozdiBtn.setFocusable(false);
		drozdiBtn.setBorder(null);
		drozdiBtn.setVisible(true);
		window.hlPanel.add(drozdiBtn);
		
		//reklamni panely
		JLabel adPanel1;
		adPanel1 = new JLabel() {
			@Override
			public void paintComponent(Graphics graphics) {
				paintAd(this, graphics);
			}
		};
		adPanel1.setOpaque(false);
		adPanel1.setBounds(RelativeSize.rectangle(0,0,20,75));
		window.add(adPanel1);
		
		JLabel adPanel2 =  new JLabel() {
			@Override
			public void paintComponent(Graphics graphics) {
				paintAd(this, graphics);
			}
		};
		adPanel2.setOpaque(false);
		adPanel2.setBounds(RelativeSize.rectangle(80,0,20,75));
		window.add(adPanel2);

	}

	private void paintAd(JLabel jLabel, Graphics graphics) {
		Graphics2D g2d = (Graphics2D) graphics;
		g2d.drawImage(FileManager_lvl1.reklamnihoBanner, 0, 0, jLabel.getWidth(),jLabel.getHeight(), null);
	}
}
