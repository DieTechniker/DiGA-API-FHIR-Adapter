/*--- (C) 1999-2020 Techniker Krankenkasse ---*/

package de.tk.opensource.services.leistung.diga.type;

public class AppInfo {

	private RecordMetaInfo metaInfo;
	private String appName;
	private String hoechstDauer;
	private String mindestDauer;
	private String beschreibung;
	private String digaId;
	private String homepage;

	public AppInfo() {
		this.metaInfo = new RecordMetaInfo();
	}

	public RecordMetaInfo getMetaInfo() {
		return metaInfo;
	}

	public void setMetaInfo(RecordMetaInfo metaInfo) {
		this.metaInfo = metaInfo;
	}

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public String getHoechstDauer() {
		return hoechstDauer;
	}

	public void setHoechstDauer(String hoechstDauer) {
		this.hoechstDauer = hoechstDauer;
	}

	public String getMindestDauer() {
		return mindestDauer;
	}

	public void setMindestDauer(String mindestDauer) {
		this.mindestDauer = mindestDauer;
	}

	public String getBeschreibung() {
		return beschreibung;
	}

	public void setBeschreibung(String beschreibung) {
		this.beschreibung = beschreibung;
	}

	public String getDigaId() {
		return digaId;
	}

	public void setDigaId(String digaId) {
		this.digaId = digaId;
	}

	public String getHomepage() {
		return homepage;
	}

	public void setHomepage(String homepage) {
		this.homepage = homepage;
	}

}

/*--- Formatiert nach TK Code Konventionen vom 05.03.2002 ---*/
