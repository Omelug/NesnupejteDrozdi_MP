package org.drozdi.story;

import org.drozdi.game.Window;

public class Story3 {

	public Story3(Window window) {
		
		String text = "<html>" + "<body style=\"  border: 0px solid red; size:25\">"
				+ "  <div style='text-align: center;'>"
				+ "  <h1 style=\"color: #B2ADFF; font-family: Lucida Sans Unicode, Lucida Grande, sans-serif;font-size: 60px; \"> První problémy v důsledku závislosti </h1><br/>"
				+ "  Vyšel jsi z obchodu, po kapsách droždí.<br/> "
				+ "Ušel jsi pár kroků a za rohem sis šňupl. Dostavuje se extáze a ty začínáš vidět svět plný poníků, skřítků a kečupových věží"
				+ "<br/><br/><br/> Ovládání: pohyb - WASD a restart - R, konec - T, strileni - SPACE " + "  </div> </body> </html>";

		
		Story0 story0 = new Story0(window, text);
		
		story0.cekatNaVstup();
		System.out.println("KONEC --Pribeh2    " + Thread.currentThread());
	}

}
