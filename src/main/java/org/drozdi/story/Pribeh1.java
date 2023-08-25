package org.drozdi.story;
import org.drozdi.game.Window;

public class Pribeh1 {
	public Pribeh1(Window window) {
		String text = "<html>" + "<body style=\"  border: 0px solid red; size:25\">"
				+ "  <div style='text-align: center;'>"
				+ "  <h1 style=\"color: red; font-family: Comic Sans MS;font-size: 60px; \"> UPOZORNĚNÍ !!!</h1><br/>"
				+ "  Tato hra je nevhodná pro děti, psychycky slabší jedince a těhotné ženy.<br/>"
				+ "  I u ostatních se ale nezaručuji bezpečnost! <br/> Pokud u vás vznikne závislost v důsledku hraní, nenese za to autor žádnou zodpovědnost..."
				+ "  </div></body></html>";
		
		Pribeh pribeh = new Pribeh(window, text);
		pribeh.cekatNaVstup();
		System.out.println("KONEC --Pribeh1    " + Thread.currentThread());
	}

}
