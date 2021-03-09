/*--- (C) 1999-2020 Techniker Krankenkasse ---*/

package de.tk.opensource.services.leistung.diga.type;

import java.util.HashSet;
import java.util.Set;

public class PrescriptionUnitInfo implements RecordMetaInfoProvider {

	private RecordMetaInfo metaInfo;
	private String pzn;
	private String digaVeId;
	private String verordnungseinheitBezeichnung;
	private IndicationInfo indications;
	private ContraIndicationInfo contraIndications;
	private Set<String> altersgruppen;
	private int anwendungsTage;
	private PriceInfo preisinfo;
	private String verordnungsEinheitStatus;
	private Boolean vertragsaerztlicheLeistungen;
	private NichtErstattungsfaehigeKostenHinweis nichtErstattungsfaehigeKostenHinweis;

	public PrescriptionUnitInfo() {
		this.metaInfo = new RecordMetaInfo();
		this.indications = new IndicationInfo();
		this.contraIndications = new ContraIndicationInfo();
		this.altersgruppen = new HashSet<>();
		this.preisinfo = new PriceInfo();
		this.nichtErstattungsfaehigeKostenHinweis = new NichtErstattungsfaehigeKostenHinweis();
	}

	@Override
	public RecordMetaInfo getMetaInfo() {
		return metaInfo;
	}

	public void setMetaInfo(RecordMetaInfo metaInfo) {
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

	public String getVerordnungseinheitBezeichnung() {
		return verordnungseinheitBezeichnung;
	}

	public void setVerordnungseinheitBezeichnung(String verordnungseinheitBezeichnung) {
		this.verordnungseinheitBezeichnung = verordnungseinheitBezeichnung;
	}

	public IndicationInfo getIndicationInfo() {
		return indications;
	}

	public void setIndicationInfo(IndicationInfo indikationen) {
		this.indications = indikationen;
	}

	public ContraIndicationInfo getContraIndicationInfo() {
		return contraIndications;
	}

	public void setContraIndicationInfo(ContraIndicationInfo contraIndications) {
		this.contraIndications = contraIndications;
	}

	public Set<String> getAltersgruppen() {
		return altersgruppen;
	}

	public void setAltersgruppen(Set<String> altersgruppen) {
		this.altersgruppen = altersgruppen;
	}

	public void addAltersgruppe(String altersgruppen) {
		this.altersgruppen.add(altersgruppen);
	}

	public int getAnwendungsTage() {
		return anwendungsTage;
	}

	public void setAnwendungsTage(int anwendungsTage) {
		this.anwendungsTage = anwendungsTage;
	}

	public PriceInfo getPreisinfo() {
		return preisinfo;
	}

	public void setPreisinfo(PriceInfo preisinfo) {
		this.preisinfo = preisinfo;
	}

	public String getVerordnungsEinheitStatus() {
		return verordnungsEinheitStatus;
	}

	public void setVerordnungsEinheitStatus(String verordnungsEinheitStatus) {
		this.verordnungsEinheitStatus = verordnungsEinheitStatus;
	}

	public Boolean getVertragsaerztlicheLeistungen() {
		return vertragsaerztlicheLeistungen;
	}

	public void setVertragsaerztlicheLeistungen(Boolean vertragsaerztlicheLeistungen) {
		this.vertragsaerztlicheLeistungen = vertragsaerztlicheLeistungen;
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
