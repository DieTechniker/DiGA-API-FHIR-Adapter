/*--- (C) 1999-2020 Techniker Krankenkasse ---*/

package de.tk.opensource.services.leistung.diga.type;

import java.util.ArrayList;
import java.util.List;

public class DigaVerzeichnis {
	private List<DigaVerordnungseinheit> digas;
	private String version;
	private String xmlns;
	private String text;

	public List<DigaVerordnungseinheit> getDigas() {
		return digas;
	}

	public void setDiga(List<DigaVerordnungseinheit> digas) {
		this.digas = digas;
	}

	public void addDiga(DigaVerordnungseinheit diga) {

		if (digas == null) {
			digas = new ArrayList<>();
		}
		digas.add(diga);
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getXmlns() {
		return xmlns;
	}

	public void setXmlns(String xmlns) {
		this.xmlns = xmlns;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public void addDigaList(List<DigaVerordnungseinheit> digasList) {

		if (digas == null) {
			digas = new ArrayList<>();
		}
		digas.addAll(digasList);

	}

}

/*--- Formatiert nach TK Code Konventionen vom 05.03.2002 ---*/
