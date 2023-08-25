package org.drozdi.levels.level2;

import org.drozdi.game.Window;

import javax.swing.ImageIcon;

public class Vozik {
	public int misto;
	public final int souradniceY;
	public boolean otocenDoPrava = false;
	public int vyska = 100;
	public int sirka = 150;
	public ImageIcon imgR = Window.resizeImage(new ImageIcon("rsc/Level2/vozikR.png"), sirka, vyska);
	public ImageIcon imgL = Window.resizeImage(new ImageIcon("rsc/Level2/vozikL.png"), sirka, vyska);
	public int speed = 4;
	
	public Vozik(int sirkaplochy, int vyskaplochy) {
		misto = sirkaplochy/2-sirka/2;
		souradniceY = vyskaplochy-vyska;
		
	}
}
