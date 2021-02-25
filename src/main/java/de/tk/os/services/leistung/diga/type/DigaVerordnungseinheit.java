/*--- (C) 1999-2020 Techniker Krankenkasse ---*/

package de.tk.os.services.leistung.diga.type;

public class DigaVerordnungseinheit {

	private String pzn;
	private String digaId;
	private String digaVeId;
	private String appName;
	private String modulBezeichnung;
	private String verordnungseinheitBezeichnung;
	private String beschreibung;
	private Hersteller hersteller;
	private Indikationen indikationen;
	private Kontraindikationen kontraindikationen;
	private String homepage;
	private Plattformen plattformen;
	private Altersgruppen altersgruppen;
	private int anwendungsTage;
	private Preisinfo preisinfo;
	private String appZulassungsbeginn;
	private String appZulassungsende;
	private MetaInfo metaInfo;
	private String hoechstDauer;
	private String mindestDauer;
	private String status;
	private Boolean vertragsaerztlicheLeistungen;
	private NichtErstattungsfaehigeKostenHinweis nichtErstattungsfaehigeKostenHinweis;

	public DigaVerordnungseinheit() {
		hersteller = new Hersteller();
		indikationen = new Indikationen();
		kontraindikationen = new Kontraindikationen();
		plattformen = new Plattformen();
		altersgruppen = new Altersgruppen();
		metaInfo = new MetaInfo();
		nichtErstattungsfaehigeKostenHinweis = new NichtErstattungsfaehigeKostenHinweis();
	}

	public String getDigaId() {
		return digaId;
	}

	public void setDigaId(String digaId) {
		this.digaId = digaId;
	}

	public String getAppName() {
		return appName;
	}

	public void setAppName(String name) {
		this.appName = name;
	}

	public String getModulBezeichnung() {
		return modulBezeichnung;
	}

	public void setModulBezeichnung(String modulBezeichnung) {
		this.modulBezeichnung = modulBezeichnung;
	}

	public String getVerordnungseinheitBezeichnung() {
		return verordnungseinheitBezeichnung;
	}

	public void setVerordnungseinheitBezeichnung(String verordnungseinheitBezeichnung) {
		this.verordnungseinheitBezeichnung = verordnungseinheitBezeichnung;
	}

	public String getBeschreibung() {
		return beschreibung;
	}

	public void setBeschreibung(String beschreibung) {
		this.beschreibung = beschreibung;
	}

	public Hersteller getHersteller() {
		return hersteller;
	}

	public void setHersteller(Hersteller hersteller) {
		this.hersteller = hersteller;
	}

	public Indikationen getIndikationen() {
		return indikationen;
	}

	public void setIndikationen(Indikationen indikationen) {
		this.indikationen = indikationen;
	}

	public Kontraindikationen getKontraindikationen() {
		return kontraindikationen;
	}

	public void setKontraindikationen(Kontraindikationen kontraindikationen) {
		this.kontraindikationen = kontraindikationen;
	}

	public String getHomepage() {
		return homepage;
	}

	public void setHomepage(String homepage) {
		this.homepage = homepage;
	}

	public Plattformen getPlattformen() {
		return plattformen;
	}

	public void setPlattformen(Plattformen plattformen) {
		this.plattformen = plattformen;
	}

	public int getAnwendungsTage() {
		return anwendungsTage;
	}

	public void setAnwendungsTage(int anwendungsTage) {
		this.anwendungsTage = anwendungsTage;
	}

	public Preisinfo getPreisinfo() {
		return preisinfo;
	}

	public void setPreisinfo(Preisinfo preisinfo) {
		this.preisinfo = preisinfo;
	}

	public Boolean getVertragsaerztlicheLeistungen() {
		return vertragsaerztlicheLeistungen;
	}

	public void setVertragsaerztlicheLeistungen(Boolean vertragsaerztlicheLeistungen) {
		this.vertragsaerztlicheLeistungen = vertragsaerztlicheLeistungen;
	}

	public String getAppZulassungsbeginn() {
		return appZulassungsbeginn;
	}

	public void setAppZulassungsbeginn(String appZulassungsbeginn) {
		this.appZulassungsbeginn = appZulassungsbeginn;
	}

	public String getAppZulassungsende() {
		return appZulassungsende;
	}

	public void setAppZulassungsende(String appZulassungsende) {
		this.appZulassungsende = appZulassungsende;
	}

	public MetaInfo getMetaInfo() {
		return metaInfo;
	}

	public void setMetaInfo(MetaInfo metaInfo) {
		this.metaInfo = metaInfo;
	}

	public String getPzn() {
		return pzn;
	}

	public void setPzn(String pzn) {
		this.pzn = pzn;
	}

	public String getDigaVeId() {
		return digaVeId;
	}

	public void setDigaVeId(String digaVeId) {
		this.digaVeId = digaVeId;
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

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public boolean isVertragsaerztlicheLeistungen() {
		return vertragsaerztlicheLeistungen;
	}

	public void setVertragsaerztlicheLeistungen(boolean vertragsaerztlicheLeistungen) {
		this.vertragsaerztlicheLeistungen = vertragsaerztlicheLeistungen;
	}

	public Altersgruppen getAltersgruppen() {
		return altersgruppen;
	}

	public void setAltersgruppen(Altersgruppen altersgruppen) {
		this.altersgruppen = altersgruppen;
	}

	public NichtErstattungsfaehigeKostenHinweis getNichtErstattungsfaehigeKostenHinweis() {
		return nichtErstattungsfaehigeKostenHinweis;
	}

	public void setNichtErstattungsfaehigeKostenHinweis(
		NichtErstattungsfaehigeKostenHinweis nichtErstattungsfaehigeKostenHinweis
	) {
		this.nichtErstattungsfaehigeKostenHinweis = nichtErstattungsfaehigeKostenHinweis;
	}

}

/*--- Formatiert nach TK Code Konventionen vom 05.03.2002 ---*/
