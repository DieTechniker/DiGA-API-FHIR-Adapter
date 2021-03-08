/*--- (C) 1999-2020 Techniker Krankenkasse ---*/

package de.tk.opensource.services.leistung.diga.type;

import java.util.ArrayList;
import java.util.List;

public class HealthAppCatalog {

	private List<PlainCatalogEntry> catalogEntries;
	private String catalogStructureVersion;
	private String xmlns;
	private String comment;

	public HealthAppCatalog() {
		catalogEntries = new ArrayList<>();
	}

	public List<PlainCatalogEntry> getCatalogEntries() {
		return catalogEntries;
	}

	public void setCatalogEntries(List<PlainCatalogEntry> entries) {
		this.catalogEntries = entries;
	}

	public void addCatalogEntry(PlainCatalogEntry entry) {
		this.catalogEntries.add(entry);
	}

	public void addCatalogEntries(List<PlainCatalogEntry> healthAppPrescriptionUnits) {
		this.catalogEntries.addAll(healthAppPrescriptionUnits);
	}

	public String getCatalogStructureVersion() {
		return catalogStructureVersion;
	}

	public void setCatalogStructureVersion(String catalogStructureVersion) {
		this.catalogStructureVersion = catalogStructureVersion;
	}

	public String getXmlns() {
		return xmlns;
	}

	public void setXmlns(String xmlns) {
		this.xmlns = xmlns;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

}

/*--- Formatiert nach TK Code Konventionen vom 05.03.2002 ---*/
