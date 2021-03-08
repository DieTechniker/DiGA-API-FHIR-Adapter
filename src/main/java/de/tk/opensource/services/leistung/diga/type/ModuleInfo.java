/*--- (C) 1999-2020 Techniker Krankenkasse ---*/

package de.tk.opensource.services.leistung.diga.type;

import java.util.ArrayList;
import java.util.List;

public class ModuleInfo {

	private RecordMetaInfo metaInfo;
	private String modulBezeichnung;
	private List<PlatformInfo> platformInfo;

	public ModuleInfo() {
		this.metaInfo = new RecordMetaInfo();
		this.platformInfo = new ArrayList<>();
	}

	public RecordMetaInfo getMetaInfo() {
		return metaInfo;
	}

	public void setMetaInfo(RecordMetaInfo metaInfo) {
		this.metaInfo = metaInfo;
	}

	public String getModulBezeichnung() {
		return modulBezeichnung;
	}

	public void setModulBezeichnung(String modulBezeichnung) {
		this.modulBezeichnung = modulBezeichnung;
	}

	public List<PlatformInfo> getPlatformInfo() {
		return platformInfo;
	}

	public void setPlatformInfo(List<PlatformInfo> plattformen) {
		this.platformInfo = plattformen;
	}

	public void addPlatformInfo(PlatformInfo plattform) {
		platformInfo.add(plattform);
	}

}

/*--- Formatiert nach TK Code Konventionen vom 05.03.2002 ---*/
