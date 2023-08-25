package org.drozdi.levels.level1;

import org.drozdi.game.FileManager;
import java.awt.image.BufferedImage;

public class FileManager_lvl1 {
	public static BufferedImage cesta, reklamnihoBanner;
	public FileManager_lvl1() {
		cesta = FileManager.loadResource("Level1/cesta.jpg");
		reklamnihoBanner = FileManager.loadResource( "Level1/reklamniBanner1.png");
	}
}
