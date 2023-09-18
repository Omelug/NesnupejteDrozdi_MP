package org.drozdi.levels.level1;

import org.drozdi.game.FileManager;

import java.awt.image.BufferedImage;

public class FileManager_lvl1 {
	public static BufferedImage path, reklamnihoBanner;
	public FileManager_lvl1() {
		path = FileManager.loadResource("Level1/path.jpg");
		reklamnihoBanner = FileManager.loadResource( "Level1/reklamniBanner1.png");
	}
}
