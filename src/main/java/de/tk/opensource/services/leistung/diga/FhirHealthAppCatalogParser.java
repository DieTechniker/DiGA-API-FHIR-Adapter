/*--- (C) 1999-2020 Techniker Krankenkasse ---*/

package de.tk.opensource.services.leistung.diga;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import ca.uhn.fhir.util.BundleUtil;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.CatalogEntry;
import org.hl7.fhir.r4.model.ChargeItemDefinition;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.DeviceDefinition;
import org.hl7.fhir.r4.model.DeviceDefinition.DeviceDefinitionSpecializationComponent;
import org.hl7.fhir.r4.model.Extension;
import org.hl7.fhir.r4.model.Money;
import org.hl7.fhir.r4.model.Organization;
import org.hl7.fhir.r4.model.Type;
import org.hl7.fhir.r4.model.UsageContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tk.opensource.services.leistung.diga.type.HealthAppCatalog;
import de.tk.opensource.services.leistung.diga.type.OrganizationInfo;
import de.tk.opensource.services.leistung.diga.type.PlainCatalogEntry;
import de.tk.opensource.services.leistung.diga.type.PlatformInfo;
import de.tk.opensource.services.leistung.diga.type.PriceInfo;

public class FhirHealthAppCatalogParser {

	private static final String DIGA_CATALOG_RESULT_TYPE_VERSION = "1.0.0";
	private static final Logger LOG = LoggerFactory.getLogger(FhirHealthAppCatalogParser.class);

	private FhirContext fhirContext;
	private DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	private DateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss:SSSSSS"); //Date time in 24h format

	private List<Organization> organizationList;
	private List<CatalogEntry> catalogEntryList;
	private List<DeviceDefinition> deviceDefinitionList;
	private List<ChargeItemDefinition> chargeItemDefinitionList;

	public FhirHealthAppCatalogParser() {
		fhirContext = FhirContext.forR4();
		catalogEntryList = new ArrayList<>();
		deviceDefinitionList = new ArrayList<>();
		chargeItemDefinitionList = new ArrayList<>();
		organizationList = new ArrayList<>();
	}

	public int getEntryCount(InputStream inputStream) {
		IParser parser = fhirContext.newXmlParser();
		Reader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
		Bundle bundle = parser.parseResource(Bundle.class, reader);

		return bundle.getEntry().size();
	}

	public FhirHealthAppCatalogParser withCatalogEntriesInput(InputStream inputStream) {

		IParser parser = fhirContext.newXmlParser();
		Reader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
		Bundle bundle = parser.parseResource(Bundle.class, reader);
		catalogEntryList.addAll(BundleUtil.toListOfResourcesOfType(fhirContext, bundle, CatalogEntry.class));

		return this;
	}

	public FhirHealthAppCatalogParser withDeviceDefinitionsInput(InputStream inputStream) {
		IParser parser = fhirContext.newXmlParser();
		Reader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
		Bundle bundle = parser.parseResource(Bundle.class, reader);
		deviceDefinitionList.addAll(BundleUtil.toListOfResourcesOfType(fhirContext, bundle, DeviceDefinition.class));

		return this;
	}

	public FhirHealthAppCatalogParser withChargeItemsInput(InputStream inputStream) {
		IParser parser = fhirContext.newXmlParser();
		Reader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
		Bundle bundle = parser.parseResource(Bundle.class, reader);
		chargeItemDefinitionList.addAll(
			BundleUtil.toListOfResourcesOfType(fhirContext, bundle, ChargeItemDefinition.class)
		);

		return this;
	}

	public FhirHealthAppCatalogParser withOrganizationsInput(InputStream inputStream) {
		IParser parser = fhirContext.newXmlParser();
		Reader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
		Bundle bundle = parser.parseResource(Bundle.class, reader);
		organizationList.addAll(BundleUtil.toListOfResourcesOfType(fhirContext, bundle, Organization.class));

		return this;
	}

	public HealthAppCatalog parse() {

		LOG.info("Start parsing health app fhir ressources... ");
		LOG.info("Creating health app catalog in version: " + DIGA_CATALOG_RESULT_TYPE_VERSION);

		HealthAppCatalog healthAppCatalog = new HealthAppCatalog();
		healthAppCatalog.setCatalogStructureVersion(DIGA_CATALOG_RESULT_TYPE_VERSION);
		healthAppCatalog.setXmlns(FhirHealthAppURIs.DIGA_VZ_NAMESPACE);
		healthAppCatalog.setComment("Created: " + LocalDateTime.now());

		readCatalogEntries(healthAppCatalog);

		LOG.info(
			"Finished parsing: created " + healthAppCatalog.getCatalogEntries().size()
			+ " consolidated entries of type \"PrescriptionUnit\"."
		);

		return healthAppCatalog;

	}

	private Optional<DeviceDefinition> findRootDevice(String rootDeviceId) {

		return deviceDefinitionList.stream().filter(d -> d.getId().contains(rootDeviceId)).findAny();
	}

	private List<DeviceDefinition> findHealthAppModules(String reference) {

		//Find devices with parent-ref to root device (Module):
		List<DeviceDefinition> module =
			deviceDefinitionList
				.stream()
				.filter(d -> reference.equals(d.getParentDevice().getReference()))
				.collect(Collectors.toList());
		return module;
	}

	private Optional<Organization> findOrganization(String reference) {

		return organizationList.stream().filter(d -> d.getId().contains(reference)).findAny();
	}

	private void readCatalogEntries(HealthAppCatalog digaVerzeichnis) {

		for (CatalogEntry catalogEntry : catalogEntryList) {

			Type healthAppLinkValue =
				catalogEntry
					.getReferencedItem()
					.getExtensionByUrl(FhirHealthAppURIs.HEALTH_APP_CATALOG_ENTRY_HEALTH_APP_LINK)
					.getExtensionByUrl("deviceDefinition")
					.getValue();

			String reference = healthAppLinkValue.castToReference(healthAppLinkValue).getReference();

			List<PlainCatalogEntry> plainCatalogEntries = readDeviceDefinitions(reference);

			//Add general app infos (from CatalogEntries):
			for (PlainCatalogEntry plainCatalogEntry : plainCatalogEntries) {

				readValidityPeriod(catalogEntry, plainCatalogEntry);
				readRegistrationMetaInfo(plainCatalogEntry, catalogEntry);

				plainCatalogEntry.getRegistrationInfo().setAppStatus(catalogEntry.getStatus().name());

				LOG.info(
					"Added health app: "
					+ plainCatalogEntry.getAppInfo().getAppName()
					+ " module: "
					+ plainCatalogEntry.getModuleInfo().getModulBezeichnung()
					+ " PU: "
					+ plainCatalogEntry.getPrescriptionUnitInfo().getVerordnungseinheitBezeichnung()
					+ " "
					+ plainCatalogEntry.getPrescriptionUnitInfo().getDigaVeId()
				);
			}

			digaVerzeichnis.addCatalogEntries(plainCatalogEntries);
		}
	}

	private void readValidityPeriod(CatalogEntry catalogEntry, PlainCatalogEntry diga) {
		diga.getRegistrationInfo().setAppValidityPeriod(dateFormat.format(catalogEntry.getValidityPeriod().getStart()));
		if (catalogEntry.getValidityPeriod().getEnd() != null) {
			diga.getRegistrationInfo().setValidityPeriod(dateFormat.format(catalogEntry.getValidityPeriod().getEnd()));
		}
	}

	private List<PlainCatalogEntry> readDeviceDefinitions(String rootDeviceId) {

		//Find Root Device:
		Optional<DeviceDefinition> rootDeviceOpt = findRootDevice(rootDeviceId);
		if (!rootDeviceOpt.isPresent()) {
			throw new IllegalStateException("Kein Root-Device gefunden für: " + rootDeviceId);
		}
		DeviceDefinition rootDevice = rootDeviceOpt.get();

		List<PlainCatalogEntry> verordnungseinheiten = new ArrayList<>();

		List<DeviceDefinition> module = findHealthAppModules(rootDeviceId);
		for (DeviceDefinition modul : module) {

			//Find refs to chargeItemDefinitions
			List<Type> refValues =
				modul
					.getExtensionsByUrl(FhirHealthAppURIs.HEALTH_APP_MODULE_PRESCRIPTION_UNIT_LINK)
					.stream()
					.map(e -> e.getValue())
					.collect(Collectors.toList());

			for (Type ref : refValues) {
				String verordnungsEinheitReference = ref.castToReference(ref).getReference();
				PlainCatalogEntry plainCatalogEntry = readChargeItemDefinitions(verordnungsEinheitReference);

				readRootDeviceInfos(rootDevice, plainCatalogEntry);
				readDeviceDefinitionInfos(modul, plainCatalogEntry);

				verordnungseinheiten.add(plainCatalogEntry);
			}

		}
		return verordnungseinheiten;
	}

	private void readDeviceDefinitionInfos(DeviceDefinition modul, PlainCatalogEntry diga) {

		//Modulbezeichnung:
		diga.getModuleInfo().setModulBezeichnung(modul.getDeviceNameFirstRep().getName());

		//Plattformen:
		readPlattformen(modul, diga);

		//Meta-Info:
		readModulMetaInfo(diga, modul);
	}

	private void readPlattformen(DeviceDefinition modul, PlainCatalogEntry diga) {

		for (DeviceDefinitionSpecializationComponent spec : modul.getSpecialization()) {

			PlatformInfo plattform = new PlatformInfo();
			Extension compatibilityExtension =
				spec.getExtensionByUrl(FhirHealthAppURIs.HEALTH_APP_MODULE_SPECIALIZATION_COMPATIBILITY);

			if (compatibilityExtension != null) {
				List<Extension> hardwareExtensions = compatibilityExtension.getExtensionsByUrl("hardware");
				hardwareExtensions
					.stream()
					.map(e -> e.getValue())
					.forEach(
						v ->
							plattform.getKompatibilitaetsHinweise().add("Hardware: " + v.castToString(v).getValue())
					);

				List<Extension> softwareExtensions = compatibilityExtension.getExtensionsByUrl("software");
				softwareExtensions
					.stream()
					.map(e -> e.getValue())
					.forEach(
						v ->
							plattform.getKompatibilitaetsHinweise().add("Software: " + v.castToString(v).getValue())
					);

			}

			Type value =
				spec.getExtensionByUrl(
						"https://fhir.trustedhealthapps.org/StructureDefinition/HealthAppModuleSpecializationUrl"
					)
					.getValue();

			plattform.setLink(value == null ? "" : value.castToUri(value).asStringValue());
			plattform.setName(spec.getSystemType() + " " + spec.getVersion());
			diga.getModuleInfo().addPlatformInfo(plattform);
		}
	}

	private PlainCatalogEntry readChargeItemDefinitions(String deviceRef) {

		PlainCatalogEntry diga = new PlainCatalogEntry();

		//Find charge item with ref:
		Optional<ChargeItemDefinition> rootDeviceOptional =
			chargeItemDefinitionList.stream().filter(d -> d.getId().contains(deviceRef)).findAny();

		if (!rootDeviceOptional.isPresent()) {
			throw new IllegalStateException("Kein Root-Device gefunden für: " + deviceRef);
		}

		ChargeItemDefinition item = rootDeviceOptional.get();

		diga.getPrescriptionUnitInfo().setVerordnungseinheitBezeichnung(item.getTitle().trim());
		diga.getPrescriptionUnitInfo().setVerordnungsEinheitStatus(item.getStatus().name());
		diga.getPrescriptionUnitInfo().setPzn(item.getCode().getCodingFirstRep().getCode());
		diga.getPrescriptionUnitInfo().setDigaVeId(item.getIdentifierFirstRep().getValue());

		readAltersgruppen(diga, item);
		readAnwendungsdauer(diga, item);
		readIndikationen(diga, item);
		readKontraIndikationen(diga, item);
		readAusschlusskriterien(diga, item);

		readNichtErstattungsFaehigeKosten(diga, item);
		readVertragsaerztlicheLeistungen(diga, item);

		readPreis(diga, item);
		readVerordnungseinheitMetaInfo(diga, item);

		return diga;
	}

	private void readRegistrationMetaInfo(PlainCatalogEntry diga, CatalogEntry item) {
		diga.getRegistrationInfo().getMetaInfo().setVersion(item.getMeta().getVersionId());
		diga.getRegistrationInfo()
			.getMetaInfo()
			.setLetzteAenderung(dateTimeFormat.format(item.getMeta().getLastUpdated()));
	}

	private void readAppMetaInfo(PlainCatalogEntry diga, DeviceDefinition item) {
		diga.getAppInfo().getMetaInfo().setVersion(item.getMeta().getVersionId());
		diga.getAppInfo().getMetaInfo().setLetzteAenderung(dateTimeFormat.format(item.getMeta().getLastUpdated()));
	}

	private void readModulMetaInfo(PlainCatalogEntry diga, DeviceDefinition item) {
		diga.getModuleInfo().getMetaInfo().setVersion(item.getMeta().getVersionId());
		diga.getModuleInfo().getMetaInfo().setLetzteAenderung(dateTimeFormat.format(item.getMeta().getLastUpdated()));
	}

	private void readVerordnungseinheitMetaInfo(PlainCatalogEntry diga, ChargeItemDefinition item) {
		diga.getPrescriptionUnitInfo().getMetaInfo().setVersion(item.getMeta().getVersionId());
		diga.getPrescriptionUnitInfo()
			.getMetaInfo()
			.setLetzteAenderung(dateTimeFormat.format(item.getMeta().getLastUpdated()));
	}

	private void readOrganizationMetaInfo(PlainCatalogEntry diga, Organization item) {
		diga.getOrganizationInfo().getMetaInfo().setVersion(item.getMeta().getVersionId());
		diga.getOrganizationInfo()
			.getMetaInfo()
			.setLetzteAenderung(dateTimeFormat.format(item.getMeta().getLastUpdated()));
	}

	private void readAltersgruppen(PlainCatalogEntry diga, ChargeItemDefinition item) {

		List<UsageContext> ageGroups =
			item.getUseContext()
				.stream()
				.filter(uc -> "age".equals(uc.getCode().getCode()))
				.collect(Collectors.toList());

		for (UsageContext ageGroup : ageGroups) {
			diga.getPrescriptionUnitInfo()
				.addAltersgruppe(ageGroup.getValueCodeableConcept().getCodingFirstRep().getCode());
		}

		List<Extension> altersGruppenExtensions =
			item.getExtensionsByUrl(FhirHealthAppURIs.HEALTH_APP_PRESCRIPTION_UNIT_ALTERSGRUPPE);

		for (Extension extension : altersGruppenExtensions) {
			diga.getPrescriptionUnitInfo()
				.addAltersgruppe(extension.getValue().castToCode(extension.getValue()).asStringValue());
		}

	}

	private void readPreis(PlainCatalogEntry diga, ChargeItemDefinition item) {

		PriceInfo preisinfo = new PriceInfo();

		Money amount = item.getPropertyGroupFirstRep().getPriceComponentFirstRep().getAmount();

		preisinfo.setPreis(amount.getValue().doubleValue());
		preisinfo.setWaehrung(amount.getCurrency());
		preisinfo.setTyp(PriceInfo.Typ.BRUTTO);

		diga.getPrescriptionUnitInfo().setPreisinfo(preisinfo);
	}

	private void readVertragsaerztlicheLeistungen(PlainCatalogEntry diga, ChargeItemDefinition item) {

		Extension vertragsaerztlicheLeistungenExtension =
			item.getExtensionByUrl(FhirHealthAppURIs.HEALTH_APP_PRESCRIPTION_UNIT_VERTRAGSAERZTLICHE_LEISTUNGEN);

		if (vertragsaerztlicheLeistungenExtension != null) {
			Type required = vertragsaerztlicheLeistungenExtension.getExtensionByUrl("required").getValue();
			diga.getPrescriptionUnitInfo()
				.setVertragsaerztlicheLeistungen(required.castToBoolean(required).booleanValue());
		}

	}

	private void readNichtErstattungsFaehigeKosten(PlainCatalogEntry diga, ChargeItemDefinition item) {

		Extension nichtErstattungsFaehigeKostenExtension =
			item.getExtensionByUrl(FhirHealthAppURIs.HEALTH_APP_PRESCRIPTION_UNIT_NICHT_ERSTATTUNGSFAEHIGE_KOSTEN);
		if (nichtErstattungsFaehigeKostenExtension != null) {
			Type required = nichtErstattungsFaehigeKostenExtension.getExtensionByUrl("required").getValue();
			/* !! Achtung: Bedeutung muss umgedreht werden: */
			// Laut BfArM ist:
			// -> NichtErstattungsfaehigeKosten required true = Alle Kosten sind erstattungsfähig
			// -> NichtErstattungsfaehigeKosten required false = Es gibt Kosten, die nicht erstattungsfähig sind (es wird zusätzlich eine Beschreibung angegeben)

			diga.getPrescriptionUnitInfo()
				.getNichtErstattungsfaehigeKostenHinweis()
				.setNichtErstattungsfaehigeKostenEnthalten(!required.castToBoolean(required).getValue());
		}

		Extension descriptionExtension =
			item.getExtensionByUrl(FhirHealthAppURIs.HEALTH_APP_PRESCRIPTION_UNIT_NICHT_ERSTATTUNGSFAEHIGE_KOSTEN)
				.getExtensionByUrl("description");

		if (descriptionExtension != null) {
			Type desc = descriptionExtension.getValue();
			diga.getPrescriptionUnitInfo()
				.getNichtErstattungsfaehigeKostenHinweis()
				.setHinweis(desc.castToString(desc).asStringValue());
		}

	}

	private void readAusschlusskriterien(PlainCatalogEntry diga, ChargeItemDefinition item) {

		if (item.getExtensionByUrl(FhirHealthAppURIs.HEALTH_APP_PRESCRIPTION_UNIT_KONTRAINDIKATION) != null) {

			List<Type> ausschlussKriterienTypes =
				item.getExtensionByUrl(FhirHealthAppURIs.HEALTH_APP_PRESCRIPTION_UNIT_KONTRAINDIKATION)
					.getExtensionsByUrl("ausschlusskriterien")
					.stream()
					.map(e -> e.getValue())
					.collect(Collectors.toList());

			for (Type kriterium : ausschlussKriterienTypes) {
				diga.getPrescriptionUnitInfo()
					.getContraIndicationInfo()
					.addDisqualifier(kriterium.castToString(kriterium).getValue());
			}
		}
	}

	private void readKontraIndikationen(PlainCatalogEntry diga, ChargeItemDefinition item) {

		if (item.getExtensionByUrl(FhirHealthAppURIs.HEALTH_APP_PRESCRIPTION_UNIT_KONTRAINDIKATION) != null) {

			List<Type> indikationenTypes =
				item.getExtensionByUrl(FhirHealthAppURIs.HEALTH_APP_PRESCRIPTION_UNIT_KONTRAINDIKATION)
					.getExtensionsByUrl("diagnose").stream().map(e -> e.getValue()).collect(Collectors.toList());

			for (Type diagnose : indikationenTypes) {
				Optional<Coding> coding = diagnose.castToCodeableConcept(diagnose).getCoding().stream().findFirst();
				if (coding.isPresent()) {
					diga.getPrescriptionUnitInfo().getContraIndicationInfo().addIndication(coding.get().getCode());
				}
			}
		}
	}

	private void readIndikationen(PlainCatalogEntry diga, ChargeItemDefinition item) {

		if (item.getExtensionByUrl(FhirHealthAppURIs.HEALTH_APP_PRESCRIPTION_UNIT_INDIKATION) != null) {

			List<Type> indikationenTypes =
				item.getExtensionByUrl(FhirHealthAppURIs.HEALTH_APP_PRESCRIPTION_UNIT_INDIKATION)
					.getExtensionsByUrl("diagnose").stream().map(e -> e.getValue()).collect(Collectors.toList());

			for (Type diagnose : indikationenTypes) {
				Optional<Coding> coding = diagnose.castToCodeableConcept(diagnose).getCoding().stream().findFirst();
				if (coding.isPresent()) {
					diga.getPrescriptionUnitInfo().getIndicationInfo().addIndication(coding.get().getCode());
				}
			}
		}
	}

	private void readAnwendungsdauer(PlainCatalogEntry diga, ChargeItemDefinition item) {
		Type anwendungsdauer =
			item.getExtensionByUrl(FhirHealthAppURIs.HEALTH_APP_PRESCRIPTION_UNIT_ANWENDUNGSDAUER).getValue();
		diga.getPrescriptionUnitInfo()
			.setAnwendungsTage(anwendungsdauer.castToDuration(anwendungsdauer).getValue().intValue());
	}

	private void readRootDeviceInfos(DeviceDefinition rootDevice, PlainCatalogEntry diga) {

		//App-Name:
		diga.getAppInfo().setAppName(rootDevice.getDeviceNameFirstRep().getName());

		//Höchstdauer:
		Type hoechstDauer =
			rootDevice
				.getExtensionByUrl(FhirHealthAppURIs.HEALTH_APP_NUTZUNGSHINWEIS)
				.getExtensionByUrl("hoechtsdauer")
				.getValue();
		diga.getAppInfo().setHoechstDauer(hoechstDauer.castToString(hoechstDauer).asStringValue());

		//Mindestdauer:
		Type mindestDauer =
			rootDevice
				.getExtensionByUrl(FhirHealthAppURIs.HEALTH_APP_NUTZUNGSHINWEIS)
				.getExtensionByUrl("mindestdauer")
				.getValue();
		diga.getAppInfo().setMindestDauer(mindestDauer.castToString(mindestDauer).asStringValue());

		//Zweckbestimmung:
		Type zweckBestimmung = rootDevice.getExtensionByUrl(FhirHealthAppURIs.HEALTH_APP_ZWECKBESTIMMUNG).getValue();
		diga.getAppInfo().setBeschreibung(zweckBestimmung.castToString(zweckBestimmung).asStringValue());

		//DiGA-ID:
		String digaId =
			rootDevice
				.getIdentifier()
				.stream()
				.filter(i -> FhirHealthAppURIs.DIGA_ID.equals(i.getSystem()))
				.map(i -> i.getValue())
				.findAny()
				.orElse("");
		diga.getAppInfo().setDigaId(digaId);

		//Organizations:
		readAppHersteller(rootDevice, diga);

		//Homepage:
		diga.getAppInfo().setHomepage(rootDevice.getOnlineInformation());

		//MetaInfos:
		readAppMetaInfo(diga, rootDevice);

	}

	private void readAppHersteller(DeviceDefinition rootDevice, PlainCatalogEntry diga) {

		String reference = rootDevice.getManufacturerReference().getReference();
		Optional<Organization> organization = findOrganization(reference);

		if (!organization.isPresent()) {
			throw new IllegalStateException("Keine Organization gefunden für: " + reference);
		}

		OrganizationInfo org = new OrganizationInfo();

		org.setIk(organization.get().getIdentifierFirstRep().getValue());
		org.setName(organization.get().getName());
		org.setStandort(organization.get().getAddressFirstRep().getCity());

		diga.setOrganizationInfo(org);

		readOrganizationMetaInfo(diga, organization.get());
	}

}

/*--- Formatiert nach TK Code Konventionen vom 05.03.2002 ---*/
