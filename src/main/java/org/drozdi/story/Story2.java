package org.drozdi.story;

import org.drozdi.game.Window;

public class Story2 {

	public Story2(Window window) {
		String text = "<html>" + "<body style=\"  border: 0px solid red; size:25\">"
				+ "  <div style='text-align: center;'>"
				+ "  <h1 style=\"color: #B2ADFF; font-family: Lucida Sans Unicode, Lucida Grande, sans-serif;font-size: 60px; \"> Počátek závislosti </h1><br/>"
				+ "  Dal sis první dávku této nebezpečné látky.<br/>\r\n"
				+ "  Nepociťuješ žádné příznaky, ale musíš na to pořád myslet.<br/> Co si třeba zajít do obchodu a dát si ještě jedno droždí? Ale pozor, nepřežeň to."
				+ "<br/><br/><br/> Ovládání: pohyb - AD, konec - T " + "  </div> </body> </html>";
		
		
		Story0 story0 = new Story0(window, text);
		story0.waitForInput();
		System.out.println("END --Story2    " + Thread.currentThread());
	}

}
