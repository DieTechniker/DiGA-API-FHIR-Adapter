/*--- (C) 1999-2020 Techniker Krankenkasse ---*/

package de.tk.os.services.leistung.diga.type;

import java.util.HashSet;
import java.util.Set;

public class Altersgruppen {

	private Set<String> altersgruppen = new HashSet<>();

	public Set<String> getAltersgruppen() {
		return altersgruppen;
	}

	public void setAltersgruppen(Set<String> altersgruppen) {
		this.altersgruppen = altersgruppen;
	}

	public void addAltersgruppe(String altersgruppen) {
		if (this.altersgruppen == null) {
			this.altersgruppen = new HashSet<>();
		}
		this.altersgruppen.add(altersgruppen);
	}

}

/*--- Formatiert nach TK Code Konventionen vom 05.03.2002 ---*/
