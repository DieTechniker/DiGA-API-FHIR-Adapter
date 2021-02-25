/*--- (C) 1999-2020 Techniker Krankenkasse ---*/

package de.tk.opensource.services.leistung.diga;

public class FhirDigaURIs {

	/**
	 * Namespace, der XML Zielstruktur des Diga-Verzeichnisses.
	 */
	public static final String DIGA_VZ_NAMESPACE = "https://xml.diga-ready.de/xsd/dvz0/v1";

	/**
	 * Der Link von CatalogEntries (App) auf das Root-Device in den DeviceDefinitions.
	 */
	public static final String HEALTH_APP_CATALOG_ENTRY_HEALTH_APP_LINK =
		"https://fhir.trustedhealthapps.org/StructureDefinition/HealthAppCatalogEntryHealthAppLink";

	/**
	 * Die DiGA-ID als eindeutiges Kennzeichen einer DiGA (5-stellig).
	 */
	public static final String DIGA_ID = "https://fhir.trustedhealthapps.org/NamingSystem/DigaId";

	/**
	 * Zweckbestimmung einer DiGA.
	 */
	public static final String HEALTH_APP_ZWECKBESTIMMUNG =
		"https://fhir.trustedhealthapps.org/StructureDefinition/HealthAppZweckbestimmung";

	/**
	 * Nutzungshinweis einer DiGA.
	 */
	public static final String HEALTH_APP_NUTZUNGSHINWEIS =
		"https://fhir.trustedhealthapps.org/StructureDefinition/HealthAppNutzungshinweis";

	/**
	 * Der Link von DeviceDefinitions (Modul) auf die zugehörigen Verordnungseinheiten
	 */
	public static final String HEALTH_APP_MODULE_PRESCRIPTION_UNIT_LINK =
		"https://fhir.trustedhealthapps.org/StructureDefinition/HealthAppModulePrescriptionUnitLink";

	/**
	 * Kontraindikationen.
	 */
	public static final String HEALTH_APP_PRESCRIPTION_UNIT_KONTRAINDIKATION =
		"https://fhir.trustedhealthapps.org/StructureDefinition/HealthAppPrescriptionUnitKontraindikation";

	/**
	 * Indikationen.
	 */
	public static final String HEALTH_APP_PRESCRIPTION_UNIT_INDIKATION =
		"https://fhir.trustedhealthapps.org/StructureDefinition/HealthAppPrescriptionUnitIndikation";

	/**
	 * Anwendungsdauer.
	 */
	public static final String HEALTH_APP_PRESCRIPTION_UNIT_ANWENDUNGSDAUER =
		"https://fhir.trustedhealthapps.org/StructureDefinition/HealthAppPrescriptionUnitAnwendungsdauer";

	/**
	 * Kompatibilitätsinformationen je Plattform.
	 */
	public static final String HEALTH_APP_MODULE_SPECIALIZATION_COMPATIBILITY =
		"https://fhir.trustedhealthapps.org/StructureDefinition/HealthAppModuleSpecializationCompatibility";

	/**
	 * Altergruppe als SNOMED-Codes.
	 */
	public static final String HEALTH_APP_PRESCRIPTION_UNIT_ALTERSGRUPPE =
		"https://fhir.trustedhealthapps.org/StructureDefinition/HealthAppPrescriptionUnitAltersgruppe";

	/**
	 * Flag, ob es sich um eine vertragsärtzliche Leistung handelt.
	 */
	public static final String HEALTH_APP_PRESCRIPTION_UNIT_VERTRAGSAERZTLICHE_LEISTUNGEN =
		"https://fhir.trustedhealthapps.org/StructureDefinition/HealthAppPrescriptionUnitVertragsaerztlicheLeistungen";

	/**
	 * Flag mit Beschreibung welche der Kosten nicht erstattungsfähig sind.
	 */
	public static final String HEALTH_APP_PRESCRIPTION_UNIT_NICHT_ERSTATTUNGSFAEHIGE_KOSTEN =
		"https://fhir.trustedhealthapps.org/StructureDefinition/HealthAppPrescriptionUnitNichtErstattungsfaehigeKosten";

}

/*--- Formatiert nach TK Code Konventionen vom 05.03.2002 ---*/
