/*--- (C) 1999-2020 Techniker Krankenkasse ---*/

package de.tk.opensource.services.leistung.diga.type;

import java.util.ArrayList;
import java.util.List;

public class Indikationen {

	private List<String> indikationen = new ArrayList<>();

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

}

/*--- Formatiert nach TK Code Konventionen vom 05.03.2002 ---*/
