package drozdi;



import drozdi.clientside.Panel;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class MoveInput extends KeyAdapter{
	private final Panel panel;
	public MoveInput(Panel panel) {
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
