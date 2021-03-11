/*--- (C) 1999-2020 Techniker Krankenkasse ---*/

package de.tk.opensource.services.leistung.diga.type;

public class PlainCatalogEntry {

	private RegistrationInfo registrationInfo;
	private AppInfo appInfo;
	private OrganizationInfo organizationInfo;
	private ModuleInfo moduleInfo;
	private PrescriptionUnitInfo prescriptionUnitInfo;

	public PlainCatalogEntry() {
		registrationInfo = new RegistrationInfo();
		appInfo = new AppInfo();
		organizationInfo = new OrganizationInfo();
		moduleInfo = new ModuleInfo();
		prescriptionUnitInfo = new PrescriptionUnitInfo();
	}

	public RegistrationInfo getRegistrationInfo() {
		return registrationInfo;
	}

	public void setRegistrationInfo(RegistrationInfo catalogEntry) {
		this.registrationInfo = catalogEntry;
	}

	public AppInfo getAppInfo() {
		return appInfo;
	}

	public void setAppInfo(AppInfo rootDevice) {
		this.appInfo = rootDevice;
	}

	public OrganizationInfo getOrganizationInfo() {
		return organizationInfo;
	}

	public void setOrganizationInfo(OrganizationInfo organization) {
		this.organizationInfo = organization;
	}

	public ModuleInfo getModuleInfo() {
		return moduleInfo;
	}

	public void setModuleInfo(ModuleInfo deviceDefinition) {
		this.moduleInfo = deviceDefinition;
	}

	public PrescriptionUnitInfo getPrescriptionUnitInfo() {
		return prescriptionUnitInfo;
	}

	public void setPrescriptionUnitInfo(PrescriptionUnitInfo chageItemDefinition) {
		this.prescriptionUnitInfo = chageItemDefinition;
	}

	public String getVersionString() {

		return
			"v"
			+ this.getOrganizationInfo().getMetaInfo().getVersion()
			+ "."
			+ this.getRegistrationInfo().getMetaInfo().getVersion()
			+ "."
			+ this.getAppInfo().getMetaInfo().getVersion()
			+ "."
			+ this.getModuleInfo().getMetaInfo().getVersion()
			+ "."
			+ this.getPrescriptionUnitInfo().getMetaInfo().getVersion();

	}

}

/*--- Formatiert nach TK Code Konventionen vom 05.03.2002 ---*/
