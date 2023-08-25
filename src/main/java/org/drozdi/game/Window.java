package org.drozdi.game;

import java.awt.*;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.io.File;

public class Window extends JFrame {
	public JPanel hlPanel;
	public JPanel questionPanel, answerPanel;
	public JLabel otazkyLabel;
	public int windowHeight = 500;
	public int windowWidth = 200;
	
	ImageIcon image = FileManager.loadImageIcon("drozdi.png");
	public Window() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setUndecorated(GameSettings.delete_title);
		setExtendedState(JFrame.MAXIMIZED_BOTH);
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int screenWidth = (int) screenSize.getWidth();
		int screenHeight = (int) screenSize.getHeight();
		setSize(screenWidth, screenHeight);
		RelativeSize.setMaximum(screenWidth, screenHeight);

		System.out.println(" "+ getSize().width);
		setTitle("Nešňupejte droždí ");
		setIconImage(image.getImage());
		getContentPane().setBackground(Color.cyan);
	}
	public void smazat() {
		getContentPane().removeAll();
	}
	public void defOkno() {
		otazkyLabel = new JLabel();
		hlPanel = new JPanel();
		questionPanel = new JPanel();
		answerPanel = new JPanel();

		hlPanel.setBackground(Color.BLUE); //TODO
		questionPanel.setBackground(Color.RED); //TODO

		questionPanel.add(otazkyLabel);
		add(hlPanel);
		add(questionPanel);
		add(answerPanel);

	}

	// Funkce na zmeneni obrazku (puvodni) na danou size
	public static ImageIcon resizeImage(ImageIcon puvodni, int x, int y) {

		Image img = puvodni.getImage();
		Image imgScale = img.getScaledInstance(x, y, Image.SCALE_SMOOTH);
		ImageIcon scaledIcon = new ImageIcon(imgScale);
		return scaledIcon;
	}

	public void setUpOtazku(Window window, JPanel panel, JLabel label, JButton tlacitko1, JButton tlacitko2, String otazka, String odpoved1, String odpoved2) {

		label.setText(otazka);

		tlacitko1.setLayout(null);
		//tlacitko1.setBounds(window.windowWidth * 2 / 24, 20, window.windowWidth * 9 / 24, 100);
		tlacitko1.setBounds(RelativeSize.rectangle(15,10, 30, 80, RelativeSize.getMaximum().x, RelativeSize.percentageY(15, RelativeSize.getMaximum().y)));
		tlacitko1.setFont(new Font("Consolas", Font.PLAIN, 25));
		tlacitko1.setText(odpoved1);
		tlacitko1.setFocusable(false);
		tlacitko1.setForeground(new Color(250, 250, 250));
		tlacitko1.setBackground(new Color(50, 50, 50));
		// tlacitko1.pressedBackgroundColor =Color.PINK;
		tlacitko1.setBorder(null);
		panel.add(tlacitko1);

		tlacitko2.setLayout(null);
		//tlacitko2.setBounds(window.windowWidth * 12 / 24, 20, window.windowWidth * 9 / 24, 100);
		tlacitko2.setBounds(RelativeSize.rectangle(55,10, 30, 80, RelativeSize.getMaximum().x, RelativeSize.percentageY(15, RelativeSize.getMaximum().y)));
		tlacitko2.setFont(new Font("Consolas", Font.PLAIN, 25));
		tlacitko2.setText(odpoved2);
		tlacitko2.setFocusable(false);
		tlacitko2.setForeground(new Color(250, 250, 250));
		tlacitko2.setBackground(new Color(50, 50, 50));
		tlacitko2.setBorder(null);
		panel.add(tlacitko2);

		// return 0;
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
	public static String ziskatCestu(String cesta) {
		return new File(cesta).getAbsolutePath().replace("\\", "/");
	}
}
