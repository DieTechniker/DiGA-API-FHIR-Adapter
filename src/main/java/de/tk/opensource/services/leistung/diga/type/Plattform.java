/*--- (C) 1999-2020 Techniker Krankenkasse ---*/

package de.tk.opensource.services.leistung.diga.type;

import java.util.ArrayList;
import java.util.List;

public class Plattform {

	private String name;
	private String link;
	private List<String> kompatibilitaetsHinweise = new ArrayList<>();

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public List<String> getKompatibilitaetsHinweise() {
		return kompatibilitaetsHinweise;
	}

	public void setKompatibilitaetsHinweise(List<String> kompatibilitaetsHinweise) {
		this.kompatibilitaetsHinweise = kompatibilitaetsHinweise;
	}

}

/*--- Formatiert nach TK Code Konventionen vom 05.03.2002 ---*/
