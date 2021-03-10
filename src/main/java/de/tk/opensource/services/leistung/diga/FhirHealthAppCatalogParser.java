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
import org.hl7.fhir.r4.model.DomainResource;
import org.hl7.fhir.r4.model.Extension;
import org.hl7.fhir.r4.model.Money;
import org.hl7.fhir.r4.model.Organization;
import org.hl7.fhir.r4.model.Resource;
import org.hl7.fhir.r4.model.Type;
import org.hl7.fhir.r4.model.UsageContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tk.opensource.services.leistung.diga.type.AppInfo;
import de.tk.opensource.services.leistung.diga.type.HealthAppCatalog;
import de.tk.opensource.services.leistung.diga.type.ModuleInfo;
import de.tk.opensource.services.leistung.diga.type.OrganizationInfo;
import de.tk.opensource.services.leistung.diga.type.PlainCatalogEntry;
import de.tk.opensource.services.leistung.diga.type.PlatformInfo;
import de.tk.opensource.services.leistung.diga.type.PrescriptionUnitInfo;
import de.tk.opensource.services.leistung.diga.type.PriceInfo;
import de.tk.opensource.services.leistung.diga.type.RecordMetaInfoProvider;
import de.tk.opensource.services.leistung.diga.type.RegistrationInfo;

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

		catalogEntryList.addAll(
			BundleUtil.toListOfResourcesOfType(
				fhirContext,
				parseRessource(inputStream, Bundle.class),
				CatalogEntry.class
			)
		);
		return this;
	}

	public FhirHealthAppCatalogParser withDeviceDefinitionsInput(InputStream inputStream) {

		deviceDefinitionList.addAll(
			BundleUtil.toListOfResourcesOfType(
				fhirContext,
				parseRessource(inputStream, Bundle.class),
				DeviceDefinition.class
			)
		);
		return this;
	}

	public FhirHealthAppCatalogParser withChargeItemsInput(InputStream inputStream) {

		chargeItemDefinitionList.addAll(
			BundleUtil.toListOfResourcesOfType(
				fhirContext,
				parseRessource(inputStream, Bundle.class),
				ChargeItemDefinition.class
			)
		);
		return this;
	}

	public FhirHealthAppCatalogParser withOrganizationsInput(InputStream inputStream) {

		organizationList.addAll(
			BundleUtil.toListOfResourcesOfType(
				fhirContext,
				parseRessource(inputStream, Bundle.class),
				Organization.class
			)
		);
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

	public RegistrationInfo parseCatalogEntry(InputStream inputStream) {
		CatalogEntry resource = parseRessource(inputStream, CatalogEntry.class);
		return mapCatalogEntry(resource);
	}

	public AppInfo parseDeviceDefinitionAsRootDevice(InputStream inputStream) {
		DeviceDefinition resource = parseRessource(inputStream, DeviceDefinition.class);
		return mapDeviceDefinitionAsRootDevice(resource);
	}

	public ModuleInfo parseDeviceDefinitionAsModuleDevice(InputStream inputStream) {
		DeviceDefinition resource = parseRessource(inputStream, DeviceDefinition.class);
		return mapDeviceDefinitionAsModuleDevice(resource);
	}

	public PrescriptionUnitInfo parseChargeItemDefinition(InputStream inputStream) {
		ChargeItemDefinition resource = parseRessource(inputStream, ChargeItemDefinition.class);
		return mapChargeItemDefinitions(resource);
	}

	public OrganizationInfo parseOrganization(InputStream inputStream) {
		Organization resource = parseRessource(inputStream, Organization.class);
		return mapOrganizations(resource);
	}

	private <T extends Resource> T parseRessource(InputStream inputStream, Class<T> type) {
		IParser parser = fhirContext.newXmlParser();
		Reader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);

		return parser.parseResource(type, reader);
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

				plainCatalogEntry.setRegistrationInfo(mapCatalogEntry(catalogEntry));

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

	private RegistrationInfo mapCatalogEntry(CatalogEntry catalogEntry) {

		RegistrationInfo regInfo = new RegistrationInfo();
		readValidityPeriod(catalogEntry, regInfo);
		readMetaInfo(catalogEntry, regInfo);
		regInfo.setAppRegistrationStatus(catalogEntry.getStatus().name());

		return regInfo;
	}

	private void readValidityPeriod(CatalogEntry catalogEntry, RegistrationInfo regInfo) {
		regInfo.setAppRegistrationStart(dateFormat.format(catalogEntry.getValidityPeriod().getStart()));
		if (catalogEntry.getValidityPeriod().getEnd() != null) {
			regInfo.setAppRegistrationEnd(dateFormat.format(catalogEntry.getValidityPeriod().getEnd()));
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

		//For each module:
		for (DeviceDefinition modul : module) {

			//Find refs to chargeItemDefinitions
			List<Type> refValues =
				modul
					.getExtensionsByUrl(FhirHealthAppURIs.HEALTH_APP_MODULE_PRESCRIPTION_UNIT_LINK)
					.stream()
					.map(e -> e.getValue())
					.collect(Collectors.toList());

			//For each prescription Unit (chargeItem)
			for (Type ref : refValues) {

				PlainCatalogEntry plainCatalogEntry = new PlainCatalogEntry();

				//APP
				plainCatalogEntry.setAppInfo(mapDeviceDefinitionAsRootDevice(rootDevice));

				//Verordnungseinheit
				String chargeItemReference = ref.castToReference(ref).getReference();
				Optional<ChargeItemDefinition> chargeItemOptional = findChargeItemDefinition(chargeItemReference);
				if (!chargeItemOptional.isPresent()) {
					throw new IllegalStateException("Kein ChargeItem gefunden für: " + chargeItemReference);
				}
				plainCatalogEntry.setPrescriptionUnitInfo(mapChargeItemDefinitions(chargeItemOptional.get()));

				//Module
				plainCatalogEntry.setModuleInfo(mapDeviceDefinitionAsModuleDevice(modul));

				//Organisation
				String orgReference = rootDevice.getManufacturerReference().getReference();
				Optional<Organization> organizationOptional = findOrganization(orgReference);
				if (!organizationOptional.isPresent()) {
					throw new IllegalStateException("Keine Organization gefunden für: " + orgReference);
				}
				plainCatalogEntry.setOrganizationInfo(mapOrganizations(organizationOptional.get()));

				verordnungseinheiten.add(plainCatalogEntry);
			}

		}
		return verordnungseinheiten;
	}

	private ModuleInfo mapDeviceDefinitionAsModuleDevice(DeviceDefinition modul) {

		ModuleInfo moduleInfo = new ModuleInfo();

		//Modulbezeichnung:
		moduleInfo.setModulBezeichnung(modul.getDeviceNameFirstRep().getName());

		//Plattformen:
		readPlattformen(modul, moduleInfo);

		//Meta-Info:
		readMetaInfo(modul, moduleInfo);

		return moduleInfo;
	}

	private void readPlattformen(DeviceDefinition modul, ModuleInfo moduleInfo) {

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
			moduleInfo.addPlatformInfo(plattform);
		}
	}

	private PrescriptionUnitInfo mapChargeItemDefinitions(ChargeItemDefinition item) {

		PrescriptionUnitInfo pInfo = new PrescriptionUnitInfo();

		pInfo.setVerordnungseinheitBezeichnung(item.getTitle().trim());
		pInfo.setPrescriptionUnitAdmissionStatus(item.getStatus().name());
		pInfo.setPzn(item.getCode().getCodingFirstRep().getCode());
		pInfo.setDigaVeId(item.getIdentifierFirstRep().getValue());

		readAltersgruppen(pInfo, item);
		readAnwendungsdauer(pInfo, item);
		readIndikationen(pInfo, item);
		readKontraIndikationen(pInfo, item);
		readAusschlusskriterien(pInfo, item);

		readNichtErstattungsFaehigeKosten(pInfo, item);
		readVertragsaerztlicheLeistungen(pInfo, item);

		readPreis(pInfo, item);
		readMetaInfo(item, pInfo);

		return pInfo;
	}

	private Optional<ChargeItemDefinition> findChargeItemDefinition(String deviceRef) {

		//Find charge item with ref:
		Optional<ChargeItemDefinition> chargeItemOptional =
			chargeItemDefinitionList.stream().filter(d -> d.getId().contains(deviceRef)).findAny();

		return chargeItemOptional;
	}

	private <I extends RecordMetaInfoProvider> void readMetaInfo(DomainResource item, I info) {
		info.getMetaInfo().setVersion(item.getMeta().getVersionId());
		info.getMetaInfo().setLetzteAenderung(dateTimeFormat.format(item.getMeta().getLastUpdated()));
		info.getMetaInfo().setFhirRessourcePath(item.getId());
		info.getMetaInfo().setFhirRessourceId(item.castToResource(item).getIdElement().getIdPart());
	}

	private void readAltersgruppen(PrescriptionUnitInfo pInfo, ChargeItemDefinition item) {

		List<UsageContext> ageGroups =
			item.getUseContext()
				.stream()
				.filter(uc -> "age".equals(uc.getCode().getCode()))
				.collect(Collectors.toList());

		for (UsageContext ageGroup : ageGroups) {
			pInfo.addAltersgruppe(ageGroup.getValueCodeableConcept().getCodingFirstRep().getCode());
		}

		List<Extension> altersGruppenExtensions =
			item.getExtensionsByUrl(FhirHealthAppURIs.HEALTH_APP_PRESCRIPTION_UNIT_ALTERSGRUPPE);

		for (Extension extension : altersGruppenExtensions) {
			pInfo.addAltersgruppe(extension.getValue().castToCode(extension.getValue()).asStringValue());
		}

	}

	private void readPreis(PrescriptionUnitInfo pInfo, ChargeItemDefinition item) {

		PriceInfo preisinfo = new PriceInfo();

		Money amount = item.getPropertyGroupFirstRep().getPriceComponentFirstRep().getAmount();

		preisinfo.setPreis(amount.getValue().doubleValue());
		preisinfo.setWaehrung(amount.getCurrency());
		preisinfo.setTyp(PriceInfo.Typ.BRUTTO);

		pInfo.setPreisinfo(preisinfo);
	}

	private void readVertragsaerztlicheLeistungen(PrescriptionUnitInfo pInfo, ChargeItemDefinition item) {

		Extension vertragsaerztlicheLeistungenExtension =
			item.getExtensionByUrl(FhirHealthAppURIs.HEALTH_APP_PRESCRIPTION_UNIT_VERTRAGSAERZTLICHE_LEISTUNGEN);

		if (vertragsaerztlicheLeistungenExtension != null) {
			Type required = vertragsaerztlicheLeistungenExtension.getExtensionByUrl("required").getValue();
			pInfo.setVertragsaerztlicheLeistungen(required.castToBoolean(required).booleanValue());
		}

	}

	private void readNichtErstattungsFaehigeKosten(PrescriptionUnitInfo pInfo, ChargeItemDefinition item) {

		Extension nichtErstattungsFaehigeKostenExtension =
			item.getExtensionByUrl(FhirHealthAppURIs.HEALTH_APP_PRESCRIPTION_UNIT_NICHT_ERSTATTUNGSFAEHIGE_KOSTEN);
		if (nichtErstattungsFaehigeKostenExtension != null) {
			Type required = nichtErstattungsFaehigeKostenExtension.getExtensionByUrl("required").getValue();
			/* !! Achtung: Bedeutung muss umgedreht werden: */
			// Laut BfArM ist:
			// -> NichtErstattungsfaehigeKosten required true = Alle Kosten sind erstattungsfähig
			// -> NichtErstattungsfaehigeKosten required false = Es gibt Kosten, die nicht erstattungsfähig sind (es wird zusätzlich eine Beschreibung angegeben)

			pInfo
				.getNichtErstattungsfaehigeKostenHinweis()
				.setNichtErstattungsfaehigeKostenEnthalten(!required.castToBoolean(required).getValue());
		}

		Extension descriptionExtension =
			item.getExtensionByUrl(FhirHealthAppURIs.HEALTH_APP_PRESCRIPTION_UNIT_NICHT_ERSTATTUNGSFAEHIGE_KOSTEN)
				.getExtensionByUrl("description");

		if (descriptionExtension != null) {
			Type desc = descriptionExtension.getValue();
			pInfo.getNichtErstattungsfaehigeKostenHinweis().setHinweis(desc.castToString(desc).asStringValue());
		}

	}

	private void readAusschlusskriterien(PrescriptionUnitInfo pInfo, ChargeItemDefinition item) {

		if (item.getExtensionByUrl(FhirHealthAppURIs.HEALTH_APP_PRESCRIPTION_UNIT_KONTRAINDIKATION) != null) {

			List<Type> ausschlussKriterienTypes =
				item.getExtensionByUrl(FhirHealthAppURIs.HEALTH_APP_PRESCRIPTION_UNIT_KONTRAINDIKATION)
					.getExtensionsByUrl("ausschlusskriterien")
					.stream()
					.map(e -> e.getValue())
					.collect(Collectors.toList());

			for (Type kriterium : ausschlussKriterienTypes) {
				pInfo.getContraIndicationInfo().addDisqualifier(kriterium.castToString(kriterium).getValue());
			}
		}
	}

	private void readKontraIndikationen(PrescriptionUnitInfo pInfo, ChargeItemDefinition item) {

		if (item.getExtensionByUrl(FhirHealthAppURIs.HEALTH_APP_PRESCRIPTION_UNIT_KONTRAINDIKATION) != null) {

			List<Type> indikationenTypes =
				item.getExtensionByUrl(FhirHealthAppURIs.HEALTH_APP_PRESCRIPTION_UNIT_KONTRAINDIKATION)
					.getExtensionsByUrl("diagnose").stream().map(e -> e.getValue()).collect(Collectors.toList());

			for (Type diagnose : indikationenTypes) {
				Optional<Coding> coding = diagnose.castToCodeableConcept(diagnose).getCoding().stream().findFirst();
				if (coding.isPresent()) {
					pInfo.getContraIndicationInfo().addIndication(coding.get().getCode());
				}
			}
		}
	}

	private void readIndikationen(PrescriptionUnitInfo pInfo, ChargeItemDefinition item) {

		if (item.getExtensionByUrl(FhirHealthAppURIs.HEALTH_APP_PRESCRIPTION_UNIT_INDIKATION) != null) {

			List<Type> indikationenTypes =
				item.getExtensionByUrl(FhirHealthAppURIs.HEALTH_APP_PRESCRIPTION_UNIT_INDIKATION)
					.getExtensionsByUrl("diagnose").stream().map(e -> e.getValue()).collect(Collectors.toList());

			for (Type diagnose : indikationenTypes) {
				Optional<Coding> coding = diagnose.castToCodeableConcept(diagnose).getCoding().stream().findFirst();
				if (coding.isPresent()) {
					pInfo.getIndicationInfo().addIndication(coding.get().getCode());
				}
			}
		}
	}

	private void readAnwendungsdauer(PrescriptionUnitInfo pInfo, ChargeItemDefinition item) {
		Type anwendungsdauer =
			item.getExtensionByUrl(FhirHealthAppURIs.HEALTH_APP_PRESCRIPTION_UNIT_ANWENDUNGSDAUER).getValue();
		pInfo.setAnwendungsTage(anwendungsdauer.castToDuration(anwendungsdauer).getValue().intValue());
	}

	private AppInfo mapDeviceDefinitionAsRootDevice(DeviceDefinition rootDevice) {

		AppInfo appInfo = new AppInfo();

		//App-Name:
		appInfo.setAppName(rootDevice.getDeviceNameFirstRep().getName());

		//Höchstdauer:
		Type hoechstDauer =
			rootDevice
				.getExtensionByUrl(FhirHealthAppURIs.HEALTH_APP_NUTZUNGSHINWEIS)
				.getExtensionByUrl("hoechtsdauer")
				.getValue();
		appInfo.setHoechstDauer(hoechstDauer.castToString(hoechstDauer).asStringValue());

		//Mindestdauer:
		Type mindestDauer =
			rootDevice
				.getExtensionByUrl(FhirHealthAppURIs.HEALTH_APP_NUTZUNGSHINWEIS)
				.getExtensionByUrl("mindestdauer")
				.getValue();
		appInfo.setMindestDauer(mindestDauer.castToString(mindestDauer).asStringValue());

		//Zweckbestimmung:
		Type zweckBestimmung = rootDevice.getExtensionByUrl(FhirHealthAppURIs.HEALTH_APP_ZWECKBESTIMMUNG).getValue();
		appInfo.setBeschreibung(zweckBestimmung.castToString(zweckBestimmung).asStringValue());

		//DiGA-ID:
		String digaId =
			rootDevice
				.getIdentifier()
				.stream()
				.filter(i -> FhirHealthAppURIs.DIGA_ID.equals(i.getSystem()))
				.map(i -> i.getValue())
				.findAny()
				.orElse("0");

		appInfo.setDigaId(String.format("%05d", Integer.parseInt(digaId)));

		//Homepage:
		appInfo.setHomepage(rootDevice.getOnlineInformation());

		//MetaInfos:
		readMetaInfo(rootDevice, appInfo);

		return appInfo;
	}

	private OrganizationInfo mapOrganizations(Organization organization) {

		OrganizationInfo orgInfo = new OrganizationInfo();

		orgInfo.setIk(organization.getIdentifierFirstRep().getValue());
		orgInfo.setName(organization.getName());
		orgInfo.setStandort(organization.getAddressFirstRep().getCity());

		readMetaInfo(organization, orgInfo);

		return orgInfo;
	}

}

/*--- Formatiert nach TK Code Konventionen vom 05.03.2002 ---*/
