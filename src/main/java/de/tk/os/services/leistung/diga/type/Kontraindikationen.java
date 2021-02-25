/*--- (C) 1999-2020 Techniker Krankenkasse ---*/

package de.tk.os.services.leistung.diga.type;

import java.util.ArrayList;
import java.util.List;

public class Kontraindikationen {

	private List<String> indikationen = new ArrayList<>();
	private List<String> ausschlussKriterien = new ArrayList<>();

	public List<String> getIndikationen() {
		return indikationen;
	}

	public void setIndikationen(List<String> indikationen) {
		this.indikationen = indikationen;
	}

	public void addIndikation(String indikation) {
		if (indikationen == null) {
			indikationen = new ArrayList<>();
		}
		indikationen.add(indikation);
	}

	public List<String> getAusschlussKriterien() {
		return ausschlussKriterien;
	}

	public void setAusschlussKriterien(List<String> ausschlussKriterien) {
		this.ausschlussKriterien = ausschlussKriterien;
	}

	public void addAusschlussKriterium(String ausschlussKriterium) {
		if (ausschlussKriterien == null) {
			ausschlussKriterien = new ArrayList<>();
		}
		ausschlussKriterien.add(ausschlussKriterium);
	}

}

/*--- Formatiert nach TK Code Konventionen vom 05.03.2002 ---*/
