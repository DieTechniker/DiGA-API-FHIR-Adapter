/*--- (C) 1999-2020 Techniker Krankenkasse ---*/

package de.tk.os.services.leistung.diga.type;

public class Preisinfo {

	public enum Typ {
		brutto, netto
	}

	private double preis;
	private String waehrung;
	private Typ typ;

	public double getPreis() {
		return preis;
	}

	public void setPreis(double preis) {
		this.preis = preis;
	}

	public String getWaehrung() {
		return waehrung;
	}

	public void setWaehrung(String waehrung) {
		this.waehrung = waehrung;
	}

	public Typ getTyp() {
		return typ;
	}

	public void setTyp(Typ typ) {
		this.typ = typ;
	}

}

/*--- Formatiert nach TK Code Konventionen vom 05.03.2002 ---*/
