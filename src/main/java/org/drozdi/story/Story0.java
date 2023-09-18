package org.drozdi.story;

import org.drozdi.game.FileManager;
import org.drozdi.game.NesnupejteDrozdi;
import org.drozdi.game.RelativeSize;
import org.drozdi.game.Window;

import javax.swing.*;
import java.awt.*;

public class Story0 extends JPanel {
	JLabel textLabel = new JLabel();
	Window window;
	JButton back = new JButton();
	JButton dal = new JButton();

	// baseni
	final String quit = "story/quit.png";
	final String arrow = "story/arrow.png";

	public Story0(Window window, String text) {
		this.window = window;
		window.defineWindow();
		base(text);
	}

	void base(String text) {
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
		
		nastatusStory(text);
		// baseni nastatuseni
		if (textLabel.getText() == null)
			nastatusStory("Ticho jako po pěšině...");
	}

	public void nastatusStory(String textStory) {
		textLabel.setText(textStory);
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
		dal.setIcon(Window.resizeImage(FileManager.loadImageIcon(arrow),dal.getWidth(), dal.getHeight()));
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
				System.out.println("ERROR -- Story  " + ex);
			}
		}
		window.hlPanel.remove(textLabel);
		window.answerPanel.remove(back);
		window.answerPanel.remove(dal);
		System.out.println("END -- Story   " + Thread.currentThread());
	}
}
