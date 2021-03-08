/*--- (C) 1999-2020 Techniker Krankenkasse ---*/

package de.tk.opensource.services.leistung.diga.type;

public class OrganizationInfo {

	private RecordMetaInfo metaInfo;
	private String ik;
	private String name;
	private String standort;

	public OrganizationInfo() {
		this.metaInfo = new RecordMetaInfo();
	}

	public RecordMetaInfo getMetaInfo() {
		return metaInfo;
	}

	public void setMetaInfo(RecordMetaInfo metaInfo) {
		this.metaInfo = metaInfo;
	}

	public String getIk() {
		return ik;
	}

	public void setIk(String ik) {
		this.ik = ik;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getStandort() {
		return standort;
	}

	public void setStandort(String standort) {
		this.standort = standort;
	}

}

/*--- Formatiert nach TK Code Konventionen vom 05.03.2002 ---*/
