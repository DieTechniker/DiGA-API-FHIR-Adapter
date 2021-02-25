/*--- (C) 1999-2020 Techniker Krankenkasse ---*/

package de.tk.opensource.services.leistung.diga.type;

import java.util.ArrayList;
import java.util.List;

public class Plattformen {
	private List<Plattform> plattformen;

	public List<Plattform> getPlattformen() {
		return plattformen;
	}

	public void setPlattformen(List<Plattform> plattformen) {
		this.plattformen = plattformen;
	}

	public void addPlattform(Plattform plattform) {
		if (plattformen == null) {
			plattformen = new ArrayList<>();
		}
		plattformen.add(plattform);
	}
}

/*--- Formatiert nach TK Code Konventionen vom 05.03.2002 ---*/
