/*--- (C) 1999-2020 Techniker Krankenkasse ---*/

package de.tk.opensource.services.leistung.diga.type;

public class NichtErstattungsfaehigeKostenHinweis {

	private Boolean nichtErstattungsfaehigeKostenEnthalten;
	private String hinweis;

	public Boolean getNichtErstattungsfaehigeKostenEnthalten() {
		return nichtErstattungsfaehigeKostenEnthalten;
	}

	public void setNichtErstattungsfaehigeKostenEnthalten(Boolean nichtErstattungsfaehigeKostenEnthalten) {
		this.nichtErstattungsfaehigeKostenEnthalten = nichtErstattungsfaehigeKostenEnthalten;
	}

	public String getHinweis() {
		return hinweis;
	}

	public void setHinweis(String hinweis) {
		this.hinweis = hinweis;
	}

}

/*--- Formatiert nach TK Code Konventionen vom 05.03.2002 ---*/
