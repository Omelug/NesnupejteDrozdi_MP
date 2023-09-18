package org.drozdi.game;

import lombok.Getter;
import lombok.Setter;

import java.awt.*;

public class RelativeSize {
	@Getter
	@Setter
	private static Point maxWindowSize = new Point();

	public static void setMaximum(int x, int y) {
		maxWindowSize.x = x;
		maxWindowSize.y = y;
	}
	public static Point getMaximum() {
		return maxWindowSize;
	}

	synchronized public static int percentageX(int percentage) {
		return percentage * maxWindowSize.x / 100;
	}

	synchronized public static int percentageY(int percentage) {
		return percentage * maxWindowSize.y / 100;
	}

	synchronized static public Point percentagePoint(int percentageX, int percentageY) {
		return new Point(percentageX(percentageX), percentageY(percentageY));
	}

	synchronized static public Rectangle rectangle(int percentageX, int percentageY, int sizeX, int sizeY) {
		return new Rectangle(percentageX(percentageX), percentageY(percentageY), percentageX(sizeX), percentageY(sizeY));
	}
	//zadane maxima
	synchronized public static int percentageX(int percentage,int max) {
		return percentage * max / 100;
	}
	synchronized public static int percentageY(int percentage, int max) {
		return percentage * max/ 100;
	}
	synchronized static public Point percentagePoint(int percentageX, int percentageY,int max) {
		return new Point(percentageX(percentageX, max), percentageY(percentageY, max));
	}
	/**
	 * @param percentageX  kolik procent z maxX ma byt horni levy roh
	 * @param percentageY  kolik procent z maxY ma byt horni levy roh
	 * @param sizeX  kolik procent z maxX ma byt sirka
	 * @param sizeY  kolik procent z maxY ma byt vyska
	 */
	synchronized static public Rectangle rectangle(int percentageX, int percentageY, int sizeX, int sizeY,int maxX, int maxY) {
		return new Rectangle(percentageX(percentageX, maxX), percentageY(percentageY, maxY), percentageX(sizeX, maxX), percentageY(sizeY, maxY));
	}
	
}
