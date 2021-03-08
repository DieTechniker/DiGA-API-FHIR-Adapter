/*--- (C) 1999-2020 Techniker Krankenkasse ---*/

package de.tk.opensource.services.leistung.diga.type;

public class RegistrationInfo {

	private RecordMetaInfo metaInfo;

	private String appZulassungsbeginn;
	private String appZulassungsende;
	private String appStatus;

	public RegistrationInfo() {
		this.metaInfo = new RecordMetaInfo();
	}

	public RecordMetaInfo getMetaInfo() {
		return metaInfo;
	}

	public void setMetaInfo(RecordMetaInfo metaInfo) {
		this.metaInfo = metaInfo;
	}

	public String getAppZulassungsbeginn() {
		return appZulassungsbeginn;
	}

	public void setAppValidityPeriod(String appZulassungsbeginn) {
		this.appZulassungsbeginn = appZulassungsbeginn;
	}

	public String getAppZulassungsende() {
		return appZulassungsende;
	}

	public void setValidityPeriod(String appZulassungsende) {
		this.appZulassungsende = appZulassungsende;
	}

	public String getAppStatus() {
		return appStatus;
	}

	public void setAppStatus(String appStatus) {
		this.appStatus = appStatus;
	}

}

/*--- Formatiert nach TK Code Konventionen vom 05.03.2002 ---*/
