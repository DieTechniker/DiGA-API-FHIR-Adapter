/*--- (C) 1999-2020 Techniker Krankenkasse ---*/

package de.tk.opensource.services.leistung.diga.type;

import java.util.ArrayList;
import java.util.List;

public class IndicationInfo {

	private List<String> indications = new ArrayList<>();

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

}

/*--- Formatiert nach TK Code Konventionen vom 05.03.2002 ---*/
