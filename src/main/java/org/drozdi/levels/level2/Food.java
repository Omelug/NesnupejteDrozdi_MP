package org.drozdi.levels.level2;

public class Food {
	public String name;
	public static double speed = 2;
	public int x;
	public int y;
	public static int size = 50;

	public Food(String name, int x, int y) {
		this.name = name;
		this.x = x;
		this.y = y;
	}

	public boolean collision(Vozik vozik) {
		boolean controlX = isBetween( x, vozik.misto, vozik.misto + vozik.sirka)|| isBetween(x + size, vozik.misto, vozik.misto + vozik.sirka);
		boolean controlY = isBetween(y, vozik.souradniceY, vozik.souradniceY + vozik.vyska/2)|| isBetween(y + size, vozik.souradniceY, vozik.souradniceY + vozik.vyska/2);
		return (controlX && controlY);
	}

	private boolean isBetween(int n, int min, int max) {
		if (n <= min) {
			return max <= n;
		}else {
			return max >= n;
		}
	}
}
