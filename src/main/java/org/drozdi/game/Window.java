package org.drozdi.game;

import java.awt.*;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.File;

public class Window extends JFrame {
	public JPanel hlPanel;
	public JPanel questionPanel, answerPanel;
	public JLabel questionLabel;
	public int windowHeight = 500;
	public int windowWidth = 200;
	
	ImageIcon image = FileManager.loadImageIcon("drozdi.png");
	public Window() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setUndecorated(GameSettings.delete_title);
		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				// Update the properties after the frame has been resized
				//TODO
				windowWidth = getWidth();
				windowHeight = getHeight();
				RelativeSize.setMaximum(windowWidth, windowHeight);
				setVisible(true);
				repaint();
				System.out.println("Resized to [" + windowWidth +", "+ windowHeight+"]");
			}
		});
		setVisible(true);
		setExtendedState(Frame.MAXIMIZED_BOTH);
		RelativeSize.setMaximum(windowWidth, windowHeight);
		setTitle("Nešňupejte droždí ");
		setIconImage(image.getImage());
		getContentPane().setBackground(Color.cyan);
	}
	public void defineWindow() {
		questionLabel = new JLabel();
		hlPanel = new JPanel();
		questionPanel = new JPanel();
		answerPanel = new JPanel();

		hlPanel.setBackground(Color.BLUE); //TODO
		questionPanel.setBackground(Color.RED); //TODO

		questionPanel.add(questionLabel);
		add(hlPanel);
		add(questionPanel);
		add(answerPanel);

	}

	// Funkce na zmeneni obrazku (puvodni) na danou size
	public static ImageIcon resizeImage(ImageIcon original, int x, int y) {

		Image img = original.getImage();
		Image imgScale = img.getScaledInstance(x, y, Image.SCALE_SMOOTH);
		return new ImageIcon(imgScale);
	}

	public void setupQuestion(JPanel panel, JLabel label, JButton button1, JButton button2, String question, String answer1, String answer2) {

		label.setText(question);

		button1.setLayout(null);
		button1.setBounds(RelativeSize.rectangle(15,10, 30, 80, RelativeSize.getMaximum().x, RelativeSize.percentageY(15, RelativeSize.getMaximum().y)));
		button1.setFont(new Font("Consolas", Font.PLAIN, 25));
		button1.setText(answer1);
		button1.setFocusable(false);
		button1.setForeground(new Color(250, 250, 250));
		button1.setBackground(new Color(50, 50, 50));
		//TODO  button.pressedBackgroundColor = Color.PINK; i u druheho
		button1.setBorder(null);
		panel.add(button1);

		button2.setLayout(null);
		button2.setBounds(RelativeSize.rectangle(55,10, 30, 80, RelativeSize.getMaximum().x, RelativeSize.percentageY(15, RelativeSize.getMaximum().y)));
		button2.setFont(new Font("Consolas", Font.PLAIN, 25));
		button2.setText(answer2);
		button2.setFocusable(false);
		button2.setForeground(new Color(250, 250, 250));
		button2.setBackground(new Color(50, 50, 50));
		button2.setBorder(null);
		panel.add(button2);
	}
	@Override
	  public synchronized void setExtendedState(final int state) {
	    if ((state & Frame.MAXIMIZED_BOTH) == Frame.MAXIMIZED_BOTH) {
	      final GraphicsConfiguration cfg = getGraphicsConfiguration();
	      final Insets screenInsets = getToolkit().getScreenInsets(cfg);
	      final Rectangle screenBounds = cfg.getBounds();
	      final int x = screenInsets.left;
	      final int y = screenInsets.top;
	      final int w = screenBounds.width - screenInsets.right - screenInsets.left;
	      final int h = screenBounds.height - screenInsets.bottom - screenInsets.top;
	      final Rectangle maximizedBounds = new Rectangle(x, y, w, h);

	      //System.out.println("cfg (" + cfg + ") screen.{bounds: " + screenBounds + ", insets: " + screenInsets + ", maxBounds: " + maximizedBounds);

	      super.setMaximizedBounds(maximizedBounds);
	    }
	    super.setExtendedState(state);
	  }
	public static String getPath(String path) {
		return new File(path).getAbsolutePath().replace("\\", "/");
	}

	public void clean() {
		getContentPane().removeAll();
	}
}
