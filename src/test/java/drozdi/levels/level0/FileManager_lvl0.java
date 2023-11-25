package drozdi.levels.level0;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import drozdi.game.FileManager;

import java.awt.image.BufferedImage;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class FileManager_lvl0 {
	private BufferedImage nesnupejteDrozdi;
	public void load() {
		nesnupejteDrozdi = FileManager.loadResource("Level0/drozdiTitle.png");
	}
}
