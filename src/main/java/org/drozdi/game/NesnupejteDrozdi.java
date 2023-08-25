package org.drozdi.game;

import lombok.Getter;
import lombok.Setter;
import org.drozdi.levels.level0.Level0;
import org.drozdi.levels.level1.Level1;
import org.drozdi.levels.level2.Level2;
import org.drozdi.levels.level3.Level3;
import org.drozdi.story.Story1;
import org.drozdi.story.Story2;
import org.drozdi.story.Story3;


public class NesnupejteDrozdi {
	final static int MAX_TIME = 2147483647;

	public static int progress = 0;

	@Setter @Getter
	private static int level3Level = 0;
	public static boolean progressContinue;
	public static long casLevel1= MAX_TIME, casLevel2= MAX_TIME;
	public static long[] mapTimeList= {MAX_TIME, MAX_TIME, MAX_TIME, MAX_TIME, MAX_TIME, MAX_TIME};
	public static String account = "NesnupejteDrozdi";
	public static String accountPath = "login/" + account + ".json";

	public static void main(String[] args) {
		Window window = new Window();
		do {
			switch (progress) {
				case 0 -> new Level0(window);
				case 1 -> {
					new Story1(window);
					if (progressContinue) {
						new Level1(window);
						progress++;
					} else {
						progress = 0;
					}
				}
				case 2 -> {
					new Story2(window);
					if (progressContinue) {
						new Level2(window);
						progress++;
					} else {
						progress = 0;
					}
				}
				case 3 -> {
					new Story3(window);
					if (progressContinue) {
						new Level3(window);
					}
					progress = 0;
				}
			}
		} while (0 <= progress && progress < 4);

		System.out.println("KONEC Main");
		System.exit(0);
	}
}
