package org.drozdi.levels.level3;


import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class MoveInput extends KeyAdapter{
	private Panel_level3 panel;
	public MoveInput(Panel_level3 panel) {
		this.panel = panel;
	}
	@Override
	public void keyPressed(KeyEvent e) {
		panel.keyPressed(e);
	}
	@Override
	public void keyReleased(KeyEvent e) {
		panel.keyReleased(e);
	}
}
