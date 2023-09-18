package org.drozdi.levels.level3;

import org.drozdi.game.FileManager;

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
		palette = FileManager.loadResource("Level3/maps/colors.bmp");

		//player position
		if (playerRight == null) {playerRight = FileManager.loadResource("player/right.png");}
		if (playerLeft == null) {playerLeft = FileManager.loadResource("player/left.png");}
		if (playerUp == null) {playerUp = FileManager.loadResource("player/up.png");}
		if (playerDown == null) {playerDown = FileManager.loadResource("player/down.png");}

		if (hedgehog == null) {hedgehog = FileManager.loadResource("Level3/hedgehog.png");}
		if (wall == null) {wall = FileManager.loadResource("Level3/wall.png");}
		if (bullet == null) {bullet = FileManager.loadResource("drozdi.png");}
		if (tower == null) {tower = FileManager.loadResource("Level3/tower.png");}
		if (slug == null) {slug = FileManager.loadResource("Level3/slug.png");}
		if (ladder == null) {ladder = FileManager.loadResource("Level3/ladder.png");}
		if (checkpoint == null) {checkpoint = FileManager.loadResource("Level3/checkpoint.png");}
		if (key == null) {key = FileManager.loadResource("Level3/key.png");}
		if (door == null) {door = FileManager.loadResource("Level3/door.png");}
		if (doorOpen == null) {doorOpen = FileManager.loadResource("Level3/doorOpen.png");}
	}

}
