package org.drozdi.levels.level2;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class Pohyb implements KeyListener {
	Vozik vozik;
	Panel panel;
	public boolean vozikJede;

	public Pohyb(Vozik vozik, Panel panel) {
		this.vozik = vozik;
		this.panel = panel;
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		pohyb(e);
	}

	@Override
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
		pohyb(e);
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		vozikJede = false;
	}

	public void pohyb(KeyEvent e) {
		
		switch (Character.toLowerCase(e.getKeyChar())) {
		case 'a':
			//System.out.print("<-");
			vozikJede = true;
			vozik.otocenDoPrava = false;
			break;
		case 'd':
			vozikJede = true;
			vozik.otocenDoPrava = true;
			break;
		case 't':
			panel.konec();
			break;
		}
	}
}
