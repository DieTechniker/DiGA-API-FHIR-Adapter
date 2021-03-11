/*--- (C) 1999-2020 Techniker Krankenkasse ---*/

package de.tk.opensource.services.leistung.diga.type;

import java.util.ArrayList;
import java.util.List;

public class ContraIndicationInfo {

	private List<String> indications = new ArrayList<>();
	private List<String> disqualifiers = new ArrayList<>();

	public List<String> getIndications() {
		return indications;
	}

	public void setIndications(List<String> indikationen) {
		this.indications = indikationen;
	}

	public void addIndication(String indikation) {
		if (indications == null) {
			indications = new ArrayList<>();
		}
		indications.add(indikation);
	}

	public List<String> getDisqualifiers() {
		return disqualifiers;
	}

	public void setDisqualifiers(List<String> ausschlussKriterien) {
		this.disqualifiers = ausschlussKriterien;
	}

	public void addDisqualifier(String ausschlussKriterium) {
		if (disqualifiers == null) {
			disqualifiers = new ArrayList<>();
		}
		disqualifiers.add(ausschlussKriterium);
	}

}

/*--- Formatiert nach TK Code Konventionen vom 05.03.2002 ---*/
