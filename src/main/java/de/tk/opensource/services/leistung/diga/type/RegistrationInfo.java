/*--- (C) 1999-2020 Techniker Krankenkasse ---*/

package de.tk.opensource.services.leistung.diga.type;

public class RegistrationInfo implements RecordMetaInfoProvider {

	private RecordMetaInfo metaInfo;

	private String appRegistrationStart;
	private String appRegistrationEnd;
	private String appRegistrationStatus;

	public RegistrationInfo() {
		this.metaInfo = new RecordMetaInfo();
	}

	@Override
	public RecordMetaInfo getMetaInfo() {
		return metaInfo;
	}

	public void setMetaInfo(RecordMetaInfo metaInfo) {
		this.metaInfo = metaInfo;
	}

	public String getAppRegistrationStart() {
		return appRegistrationStart;
	}

	public void setAppRegistrationStart(String appRegistrationStart) {
		this.appRegistrationStart = appRegistrationStart;
	}

	public String getAppRegistrationEnd() {
		return appRegistrationEnd;
	}

	public void setAppRegistrationEnd(String appRegistrationEnd) {
		this.appRegistrationEnd = appRegistrationEnd;
	}

	public String getAppRegistrationStatus() {
		return appRegistrationStatus;
	}

	public void setAppRegistrationStatus(String appRegistrationStatus) {
		this.appRegistrationStatus = appRegistrationStatus;
	}

}

/*--- Formatiert nach TK Code Konventionen vom 05.03.2002 ---*/
