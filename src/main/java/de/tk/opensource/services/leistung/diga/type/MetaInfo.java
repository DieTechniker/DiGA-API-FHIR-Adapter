/*--- (C) 1999-2020 Techniker Krankenkasse ---*/

package de.tk.opensource.services.leistung.diga.type;

public class MetaInfo {

	private String letzteAenderung;
	private String organisationVersion;
	private String appVersion;
	private String rootDeviceVersion;
	private String modulVersion;
	private String verordnungseinheitVersion;

	public String getLetzteAenderung() {
		return letzteAenderung;
	}

	public void setLetzteAenderung(String letzteAenderung) {
		this.letzteAenderung = letzteAenderung;
	}

	public String getVersionString() {
		return
			organisationVersion
			+ "."
			+ appVersion
			+ "."
			+ rootDeviceVersion
			+ "."
			+ modulVersion
			+ "."
			+ verordnungseinheitVersion;
	}

	public String getOrganisationVersion() {
		return organisationVersion;
	}

	public void setOrganisationVersion(String organisationVersion) {
		this.organisationVersion = organisationVersion;
	}

	public String getAppVersion() {
		return appVersion;
	}

	public void setAppVersion(String appVersion) {
		this.appVersion = appVersion;
	}

	public String getModulVersion() {
		return modulVersion;
	}

	public void setModulVersion(String modulVersion) {
		this.modulVersion = modulVersion;
	}

	public String getVerordnungseinheitVersion() {
		return verordnungseinheitVersion;
	}

	public void setVerordnungseinheitVersion(String verordnungseinheitVersion) {
		this.verordnungseinheitVersion = verordnungseinheitVersion;
	}

	public String getRootDeviceVersion() {
		return rootDeviceVersion;
	}

	public void setRootDeviceVersion(String rootDeviceVersion) {
		this.rootDeviceVersion = rootDeviceVersion;
	}

}

/*--- Formatiert nach TK Code Konventionen vom 05.03.2002 ---*/
