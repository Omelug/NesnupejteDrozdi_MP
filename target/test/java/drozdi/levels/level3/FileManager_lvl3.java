package drozdi.levels.level3;

import drozdi.game.FileManager;

import java.awt.*;
import java.awt.image.BufferedImage;

public class FileManager_lvl3 {
	public static BufferedImage playerRight, playerLeft, playerUp, playerDown;
	public static BufferedImage bullet;
	public static BufferedImage palette;
	public static BufferedImage wall, hedgehog, ladder,tower,slug, checkpoint, key, door, doorOpen;
	public static Point[] defaultMapPosition;

	public static void loadResources() {
		defaultMapPosition = new Point[]{new Point(13, 10),new Point(13, 10),new Point(13, 10)};

		// map colors
		palette = FileManager.loadResource("walls/maps/colors.bmp");

		//player position
		if (playerRight == null) {playerRight = FileManager.loadResource("player/right.png");}
		if (playerLeft == null) {playerLeft = FileManager.loadResource("player/left.png");}
		if (playerUp == null) {playerUp = FileManager.loadResource("player/up.png");}
		if (playerDown == null) {playerDown = FileManager.loadResource("player/down.png");}

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

}
