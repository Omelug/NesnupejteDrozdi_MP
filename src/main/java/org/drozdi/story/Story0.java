package org.drozdi.story;

import org.drozdi.game.FileManager;
import org.drozdi.game.NesnupejteDrozdi;
import org.drozdi.game.Window;
import org.drozdi.game.RelativeSize;
import java.awt.Color;
import java.awt.Font;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.Rectangle;

public class Story0 extends JPanel {
	JLabel textLabel = new JLabel();
	Window window;
	JButton back = new JButton();
	JButton dal = new JButton();

	// zakladni 
	final String quit = "cedulePribehy/quit.png";
	final String sipka = "cedulePribehy/sipka.png";

	public Story0(Window window, String text) {
		this.window = window;
		
		//window.smazat();
		window.defOkno();
		zaklad(text);
	}

	void zaklad(String text) {
		window.hlPanel.setLayout(null);
		window.hlPanel.setBounds(RelativeSize.rectangle(0,0,100,75));
		
		window.answerPanel.setLayout(null);
		window.answerPanel.setBounds(RelativeSize.rectangle(0,75,100,25));

		textLabel.setFont(new Font("Consolas", Font.PLAIN, 35));
		textLabel.setForeground(Color.white);
		textLabel.setBounds(0, 0, window.hlPanel.getWidth(), window.hlPanel.getHeight());
		textLabel.setBackground(Color.black);
		textLabel.setOpaque(false);
		textLabel.setHorizontalAlignment(getWidth() / 2);

		window.hlPanel.add(textLabel);
		setUpColor(new Color(80, 80, 80), new Color(36, 137, 176));
		setUpDownBar();
		
		nastavPribeh(text);
		// zakladni nastaveni
		if (textLabel.getText() == null)
			nastavPribeh("Ticho jako po pěšině...");
	}

	public void nastavPribeh(String textPribeh) {
		textLabel.setText(textPribeh);
	}

	public void setUpColor(Color color1, Color color2) {
		window.hlPanel.setBackground(color1);
		window.answerPanel.setBackground(color2);
	}

	public void setUpDownBar() {

		back.setBounds(new Rectangle(RelativeSize.percentageX(2), RelativeSize.percentageY(10,window.answerPanel.getHeight()), RelativeSize.percentageX(10), RelativeSize.percentageY(80,window.answerPanel.getHeight())));
		back.setIcon(Window.resizeImage(FileManager.loadImageIcon(quit),back.getWidth(), back.getHeight()));
		back.setBorder(null);
		back.setOpaque(false);
		back.addActionListener(e -> {
			synchronized (this) {
				NesnupejteDrozdi.progressContinue = false;
				notify();
			}
		});
		window.answerPanel.add(back);
		
		dal.setBounds(new Rectangle(RelativeSize.percentageX(88), RelativeSize.percentageY(10,window.answerPanel.getHeight()), RelativeSize.percentageX(10), RelativeSize.percentageY(80,window.answerPanel.getHeight())));
		dal.setIcon(Window.resizeImage(FileManager.loadImageIcon(sipka),dal.getWidth(), dal.getHeight()));
		dal.setOpaque(false);
		dal.setContentAreaFilled(false);
		dal.setBorder(null);
		dal.addActionListener(e -> {
			synchronized (this) {
				NesnupejteDrozdi.progressContinue = true;
				notify();
			}
		});
		window.answerPanel.add(dal);
		window.answerPanel.repaint();
	}

	public void waitForInput() {
		synchronized (this) {
			try {
				wait();
			} catch (InterruptedException ex) {
				System.out.println("CHYBA -- Pribeh  " + ex);
			}
		}
		window.hlPanel.remove(textLabel);
		window.answerPanel.remove(back);
		window.answerPanel.remove(dal);
		System.out.println("KONEC -- Pribeh   " + Thread.currentThread());
	}
}
