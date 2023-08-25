package org.drozdi.game;

import lombok.Getter;
import lombok.Setter;
import org.drozdi.levels.level0.Level0;
import org.drozdi.levels.level1.Level1;
import org.drozdi.levels.level2.Level2;
import org.drozdi.levels.level3.Level3;
import org.drozdi.story.Pribeh1;
import org.drozdi.story.Pribeh2;
import org.drozdi.story.Pribeh3;


public class NesnupejteDrozdi {
	final static int MAX_TIME = 2147483647;

	public static int progress = 3; //TODO:
	@Setter @Getter
	private static int level3Level = 2; //TODO
	public static boolean jitdal;
	public static long casLevel1= MAX_TIME, casLevel2= MAX_TIME;
	public static long[] mapTimeList= {MAX_TIME, MAX_TIME, MAX_TIME, MAX_TIME, MAX_TIME, MAX_TIME};
	public static String account = "NesnupejteDrozdi";
	public static String accountPath = "login/" + account + ".json";

	public static void main(String[] args) {
		Window window = new Window();
		do {
			switch (progress) {
				case 0 -> {
					new Level0(window);
				}
				case 1 -> {
					new Pribeh1(window);
					if (jitdal) {
						new Level1(window);
						progress++;
					} else {
						progress = 0;
					}
				}
				case 2 -> {
					new Pribeh2(window);
					if (jitdal) {
						new Level2(window);
						progress++;
					} else {
						progress = 0;
					}
				}
				case 3 -> {
					new Pribeh3(window);
					if (jitdal) {
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
