/*--- (C) 1999-2020 Techniker Krankenkasse ---*/

package de.tk.opensource.services.leistung.diga.type;

public class RecordMetaInfo {

	private String letzteAenderung;
	private String version;
	private String fhirRessourceId;
	private String fhirRessourcePath;

	public String getLetzteAenderung() {
		return letzteAenderung;
	}

	public void setLetzteAenderung(String letzteAenderung) {
		this.letzteAenderung = letzteAenderung;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getFhirRessourceId() {
		return fhirRessourceId;
	}

	public void setFhirRessourceId(String fhirRessourceId) {
		this.fhirRessourceId = fhirRessourceId;
	}

	public String getFhirRessourcePath() {
		return fhirRessourcePath;
	}

	public void setFhirRessourcePath(String fhirRessourcePath) {
		this.fhirRessourcePath = fhirRessourcePath;
	}

}

/*--- Formatiert nach TK Code Konventionen vom 05.03.2002 ---*/
