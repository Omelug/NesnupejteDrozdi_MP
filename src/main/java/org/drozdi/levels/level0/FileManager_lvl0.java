package org.drozdi.levels.level0;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.drozdi.game.FileManager;

import java.awt.*;
import java.awt.image.BufferedImage;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class FileManager_lvl0 {
	private BufferedImage nesnupejteDrozdi;
	public void load() {
		nesnupejteDrozdi = FileManager.loadResource("Level0/drozdiNahore.png");
	}
}
