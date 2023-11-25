package drozdi;

import drozdi.console.ConsoleColors;
import drozdi.console.ConsoleLogger;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.net.URL;

public class FileManager {
	static private final ConsoleLogger log = new ConsoleLogger(ConsoleColors.YELLOW_BRIGHT);

	public static BufferedImage playerRight, playerLeft, playerUp, playerDown;
	public static BufferedImage bullet;
	public static BufferedImage palette;
	public static BufferedImage wall, hedgehog, ladder,tower,slug, checkpoint, key, door, doorOpen;
	public static Point defaultMapPosition;

	public static void loadResources() {
		defaultMapPosition = new Point(13, 10);


		//player position
		if (playerRight == null) {playerRight = FileManager.loadResource("player/right.png");}
		if (playerLeft == null) {playerLeft = FileManager.loadResource("player/left.png");}
		if (playerUp == null) {playerUp = FileManager.loadResource("player/up.png");}
		if (playerDown == null) {playerDown = FileManager.loadResource("player/down.png");}

		// map colors
		palette = FileManager.loadResource("walls/maps/colors.bmp");

		if (hedgehog == null) {hedgehog = FileManager.loadResource("walls/hedgehog.png");}
		if (wall == null) {wall = FileManager.loadResource("walls/wall.png");}
		if (bullet == null) {bullet = FileManager.loadResource("drozdi.png");}
		if (tower == null) {tower = FileManager.loadResource("walls/tower.png");}
		if (slug == null) {slug = FileManager.loadResource("walls/slug.png");}
		if (ladder == null) {ladder = FileManager.loadResource("walls/ladder.png");}
		if (checkpoint == null) {checkpoint = FileManager.loadResource("walls/checkpoint.png");}
		if (key == null) {key = FileManager.loadResource("walls/key.png");}
		if (door == null) {door = FileManager.loadResource("walls/door.png");}
		if (doorOpen == null) {doorOpen = FileManager.loadResource("walls/doorOpen.png");}
	}
	public static BufferedImage loadResource(String imagePath){
		try {
			InputStream imageStream = FileManager.class.getClassLoader().getResourceAsStream(imagePath);
			if (imageStream != null){
				return ImageIO.read(imageStream);
			}
		} catch (Exception e) {
			log.error(" load resource " + imagePath + " - " + e);
			//e.printStackTrace();
		}
		return null;
	}
	public static ImageIcon loadImageIcon(String imagePath) {
		try {
			URL imageStream = FileManager.class.getClassLoader().getResource(imagePath);
			if (imageStream != null){
				return new ImageIcon(imageStream);
			}
		} catch (Exception e) {
			log.error(" load resource " + imagePath + " - " + e);
		}
		return null;
	}

	public static URL getResource(String path) {
		return FileManager.class.getClassLoader().getResource(path);
	}

}
