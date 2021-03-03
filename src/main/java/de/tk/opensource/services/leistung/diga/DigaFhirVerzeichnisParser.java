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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
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

import de.tk.opensource.services.leistung.diga.type.DigaVerordnungseinheit;
import de.tk.opensource.services.leistung.diga.type.DigaVerzeichnis;
import de.tk.opensource.services.leistung.diga.type.Hersteller;
import de.tk.opensource.services.leistung.diga.type.Plattform;
import de.tk.opensource.services.leistung.diga.type.Preisinfo;

public class DigaFhirVerzeichnisParser {

	private static final String DIGA_VERZEICHNIS_RESULT_TYPE_VERSION = "1.0.0";
	private static final Logger LOG = LoggerFactory.getLogger(DigaFhirVerzeichnisRequester.class);

	private FhirContext fhirContext;
	private DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	private DateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss"); //Date time in 24h format

	private List<Organization> organizationList;
	private List<CatalogEntry> catalogEntryList;
	private List<DeviceDefinition> deviceDefinitionList;
	private List<ChargeItemDefinition> chargeItemDefinitionList;

	public DigaFhirVerzeichnisParser() {
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

	public DigaFhirVerzeichnisParser withCatalogEntriesInput(InputStream inputStream) {

		IParser parser = fhirContext.newXmlParser();
		Reader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
		Bundle bundle = parser.parseResource(Bundle.class, reader);
		catalogEntryList.addAll(BundleUtil.toListOfResourcesOfType(fhirContext, bundle, CatalogEntry.class));

		return this;
	}

	public DigaFhirVerzeichnisParser withDeviceDefinitionsInput(InputStream inputStream) {
		IParser parser = fhirContext.newXmlParser();
		Reader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
		Bundle bundle = parser.parseResource(Bundle.class, reader);
		deviceDefinitionList.addAll(BundleUtil.toListOfResourcesOfType(fhirContext, bundle, DeviceDefinition.class));

		return this;
	}

	public DigaFhirVerzeichnisParser withChargeItemsInput(InputStream inputStream) {
		IParser parser = fhirContext.newXmlParser();
		Reader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
		Bundle bundle = parser.parseResource(Bundle.class, reader);
		chargeItemDefinitionList.addAll(
			BundleUtil.toListOfResourcesOfType(fhirContext, bundle, ChargeItemDefinition.class)
		);

		return this;
	}

	public DigaFhirVerzeichnisParser withOrganizationsInput(InputStream inputStream) {
		IParser parser = fhirContext.newXmlParser();
		Reader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
		Bundle bundle = parser.parseResource(Bundle.class, reader);
		organizationList.addAll(BundleUtil.toListOfResourcesOfType(fhirContext, bundle, Organization.class));

		return this;
	}

	public DigaVerzeichnis parse() {

		LOG.info("Start parsing diga fhir ressources... ");
		LOG.info("Creating DiGA-Verzeichnis in Version: " + DIGA_VERZEICHNIS_RESULT_TYPE_VERSION);

		DigaVerzeichnis digaVerzeichnis = new DigaVerzeichnis();
		digaVerzeichnis.setVersion(DIGA_VERZEICHNIS_RESULT_TYPE_VERSION);
		digaVerzeichnis.setXmlns(FhirDigaURIs.DIGA_VZ_NAMESPACE);
		digaVerzeichnis.setText("Erstellt am: " + LocalDateTime.now());

		readDigaCatalogEntries(digaVerzeichnis);

		LOG.info(
			"Finished parsing: created " + digaVerzeichnis.getDigas().size()
			+ " Entries of Type \"DiGA-Verordnungseinheit\"."
		);

		return digaVerzeichnis;

	}

	private Optional<DeviceDefinition> findRootDevice(String rootDeviceId) {

		return deviceDefinitionList.stream().filter(d -> d.getId().contains(rootDeviceId)).findAny();
	}

	private List<DeviceDefinition> findDigaModule(String reference) {

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

	private void readDigaCatalogEntries(DigaVerzeichnis digaVerzeichnis) {

		for (CatalogEntry catalogEntry : catalogEntryList) {

			Type healthAppLinkValue =
				catalogEntry
					.getReferencedItem()
					.getExtensionByUrl(FhirDigaURIs.HEALTH_APP_CATALOG_ENTRY_HEALTH_APP_LINK)
					.getExtensionByUrl("deviceDefinition")
					.getValue();
			String display = healthAppLinkValue.castToReference(healthAppLinkValue).getDisplay();
			String reference = healthAppLinkValue.castToReference(healthAppLinkValue).getReference();

			List<DigaVerordnungseinheit> digas = readDeviceDefinitions(reference);

			//Add general app infos (from CatalogEntries):
			for (DigaVerordnungseinheit diga : digas) {
				readZulassungsZeitraum(catalogEntry, diga);
				readAppMetaInfo(diga, catalogEntry);

				diga.setAppName(display);
				diga.setAppStatus(catalogEntry.getStatus().name());

				LOG.info(
					"Added DiGA: "
					+ diga.getAppName()
					+ " Modul: "
					+ diga.getModulBezeichnung()
					+ " VE: "
					+ diga.getVerordnungseinheitBezeichnung()
					+ " "
					+ diga.getDigaVeId()
				);
			}

			digaVerzeichnis.addDigaList(digas);
		}
	}

	private void readZulassungsZeitraum(CatalogEntry catalogEntry, DigaVerordnungseinheit diga) {
		diga.setAppZulassungsbeginn(dateFormat.format(catalogEntry.getValidityPeriod().getStart()));
		if (catalogEntry.getValidityPeriod().getEnd() != null) {
			diga.setAppZulassungsende(dateFormat.format(catalogEntry.getValidityPeriod().getEnd()));
		}
	}

	private List<DigaVerordnungseinheit> readDeviceDefinitions(String rootDeviceId) {

		Optional<DeviceDefinition> rootDevice = findRootDevice(rootDeviceId);

		if (!rootDevice.isPresent()) {
			throw new IllegalStateException("Kein Root-Device gefunden für: " + rootDeviceId);
		}

		return readDigaModule(rootDeviceId, rootDevice.get());
	}

	private List<DigaVerordnungseinheit> readDigaModule(String reference, DeviceDefinition rootDevice) {

		List<DigaVerordnungseinheit> verordnungseinheiten = new ArrayList<>();

		List<DeviceDefinition> module = findDigaModule(reference);
		for (DeviceDefinition modul : module) {

			//Find refs to chargeItemDefinitions
			List<Type> refValues =
				modul
					.getExtensionsByUrl(FhirDigaURIs.HEALTH_APP_MODULE_PRESCRIPTION_UNIT_LINK)
					.stream()
					.map(e -> e.getValue())
					.collect(Collectors.toList());

			for (Type ref : refValues) {
				String verordnungsEinheitReference = ref.castToReference(ref).getReference();
				DigaVerordnungseinheit diga = readDigaVerordnungseinheiten(verordnungsEinheitReference);

				readRootDeviceInfos(rootDevice, diga);
				readModulInfos(modul, diga);

				verordnungseinheiten.add(diga);
			}

		}
		return verordnungseinheiten;
	}

	private void readModulInfos(DeviceDefinition modul, DigaVerordnungseinheit diga) {

		//Modulbezeichnung:
		diga.setModulBezeichnung(modul.getDeviceNameFirstRep().getName());

		//Plattformen:
		readPlattformen(modul, diga);

		//Meta-Info:
		readModulMetaInfo(diga, modul);
	}

	private void readPlattformen(DeviceDefinition modul, DigaVerordnungseinheit diga) {

		for (DeviceDefinitionSpecializationComponent spec : modul.getSpecialization()) {

			Plattform plattform = new Plattform();
			Extension compatibilityExtension =
				spec.getExtensionByUrl(FhirDigaURIs.HEALTH_APP_MODULE_SPECIALIZATION_COMPATIBILITY);

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
			diga.getPlattformen().addPlattform(plattform);
		}
	}

	private DigaVerordnungseinheit readDigaVerordnungseinheiten(String ref) {

		DigaVerordnungseinheit diga = new DigaVerordnungseinheit();

		//Find charge item with ref:
		Optional<ChargeItemDefinition> rootDeviceOptional =
			chargeItemDefinitionList.stream().filter(d -> d.getId().contains(ref)).findAny();

		if (!rootDeviceOptional.isPresent()) {
			throw new IllegalStateException("Kein Root-Device gefunden für: " + ref);
		}

		ChargeItemDefinition item = rootDeviceOptional.get();

		diga.setVerordnungseinheitBezeichnung(item.getTitle().trim());
		diga.setVerordnungsEinheitStatus(item.getStatus().name());
		diga.setPzn(item.getCode().getCodingFirstRep().getCode());
		diga.setDigaVeId(item.getIdentifierFirstRep().getValue());

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

	private void readVerordnungseinheitMetaInfo(DigaVerordnungseinheit diga, ChargeItemDefinition item) {
		diga.getMetaInfo().setVerordnungseinheitVersion(item.getMeta().getVersionId());
		updateLetzteAenderung(diga, item.getMeta().getLastUpdated());
	}

	private void readModulMetaInfo(DigaVerordnungseinheit diga, DeviceDefinition item) {
		diga.getMetaInfo().setModulVersion(item.getMeta().getVersionId());
		updateLetzteAenderung(diga, item.getMeta().getLastUpdated());
	}

	private void readRootDeviceMetaInfo(DigaVerordnungseinheit diga, DeviceDefinition item) {
		diga.getMetaInfo().setRootDeviceVersion(item.getMeta().getVersionId());
		updateLetzteAenderung(diga, item.getMeta().getLastUpdated());
	}

	private void readAppMetaInfo(DigaVerordnungseinheit diga, CatalogEntry item) {
		diga.getMetaInfo().setAppVersion(item.getMeta().getVersionId());
		updateLetzteAenderung(diga, item.getMeta().getLastUpdated());
	}

	private void readOrganizationMetaInfo(DigaVerordnungseinheit diga, Organization item) {
		diga.getMetaInfo().setOrganisationVersion(item.getMeta().getVersionId());
		updateLetzteAenderung(diga, item.getMeta().getLastUpdated());
	}

	private void updateLetzteAenderung(DigaVerordnungseinheit diga, Date date) {

		if (diga.getMetaInfo().getLetzteAenderung() == null || diga.getMetaInfo().getLetzteAenderung().isEmpty()) {
			diga.getMetaInfo().setLetzteAenderung(dateTimeFormat.format(date));
			return;
		}

		try {
			Date currentDate = dateTimeFormat.parse(diga.getMetaInfo().getLetzteAenderung());

			if (date.after(currentDate)) {
				diga.getMetaInfo().setLetzteAenderung(dateTimeFormat.format(date));
			}

		} catch (ParseException e) {
			throw new IllegalStateException(e.getMessage(), e);
		}

	}

	private void readAltersgruppen(DigaVerordnungseinheit diga, ChargeItemDefinition item) {

		List<UsageContext> ageGroups =
			item.getUseContext()
				.stream()
				.filter(uc -> "age".equals(uc.getCode().getCode()))
				.collect(Collectors.toList());

		for (UsageContext ageGroup : ageGroups) {
			diga.getAltersgruppen().addAltersgruppe(ageGroup.getValueCodeableConcept().getCodingFirstRep().getCode());
		}

		List<Extension> altersGruppenExtensions =
			item.getExtensionsByUrl(FhirDigaURIs.HEALTH_APP_PRESCRIPTION_UNIT_ALTERSGRUPPE);

		for (Extension extension : altersGruppenExtensions) {
			diga.getAltersgruppen()
				.addAltersgruppe(extension.getValue().castToCode(extension.getValue()).asStringValue());
		}

	}

	private void readPreis(DigaVerordnungseinheit diga, ChargeItemDefinition item) {

		Preisinfo preisinfo = new Preisinfo();

		Money amount = item.getPropertyGroupFirstRep().getPriceComponentFirstRep().getAmount();

		preisinfo.setPreis(amount.getValue().doubleValue());
		preisinfo.setWaehrung(amount.getCurrency());
		preisinfo.setTyp(Preisinfo.Typ.brutto);

		diga.setPreisinfo(preisinfo);
	}

	private void readVertragsaerztlicheLeistungen(DigaVerordnungseinheit diga, ChargeItemDefinition item) {

		Extension vertragsaerztlicheLeistungenExtension =
			item.getExtensionByUrl(FhirDigaURIs.HEALTH_APP_PRESCRIPTION_UNIT_VERTRAGSAERZTLICHE_LEISTUNGEN);

		if (vertragsaerztlicheLeistungenExtension != null) {
			Type required = vertragsaerztlicheLeistungenExtension.getExtensionByUrl("required").getValue();
			diga.setVertragsaerztlicheLeistungen(required.castToBoolean(required).booleanValue());
		}

	}

	private void readNichtErstattungsFaehigeKosten(DigaVerordnungseinheit diga, ChargeItemDefinition item) {

		Extension nichtErstattungsFaehigeKostenExtension =
			item.getExtensionByUrl(FhirDigaURIs.HEALTH_APP_PRESCRIPTION_UNIT_NICHT_ERSTATTUNGSFAEHIGE_KOSTEN);
		if (nichtErstattungsFaehigeKostenExtension != null) {
			Type required = nichtErstattungsFaehigeKostenExtension.getExtensionByUrl("required").getValue();
			/* !! Achtung: Bedeutung muss umgedreht werden: */
			// Laut BfArM ist:
			// -> NichtErstattungsfaehigeKosten required true = Alle Kosten sind erstattungsfähig
			// -> NichtErstattungsfaehigeKosten required false = Es gibt Kosten, die nicht erstattungsfähig sind (es wird zusätzlich eine Beschreibung angegeben)

			diga.getNichtErstattungsfaehigeKostenHinweis()
				.setNichtErstattungsfaehigeKostenEnthalten(!required.castToBoolean(required).getValue());
		}

		Extension descriptionExtension =
			item.getExtensionByUrl(FhirDigaURIs.HEALTH_APP_PRESCRIPTION_UNIT_NICHT_ERSTATTUNGSFAEHIGE_KOSTEN)
				.getExtensionByUrl("description");

		if (descriptionExtension != null) {
			Type desc = descriptionExtension.getValue();
			diga.getNichtErstattungsfaehigeKostenHinweis().setHinweis(desc.castToString(desc).asStringValue());
		}

	}

	private void readAusschlusskriterien(DigaVerordnungseinheit diga, ChargeItemDefinition item) {

		if (item.getExtensionByUrl(FhirDigaURIs.HEALTH_APP_PRESCRIPTION_UNIT_KONTRAINDIKATION) != null) {

			List<Type> ausschlussKriterienTypes =
				item.getExtensionByUrl(FhirDigaURIs.HEALTH_APP_PRESCRIPTION_UNIT_KONTRAINDIKATION)
					.getExtensionsByUrl("ausschlusskriterien")
					.stream()
					.map(e -> e.getValue())
					.collect(Collectors.toList());

			for (Type kriterium : ausschlussKriterienTypes) {
				diga.getKontraindikationen().addAusschlussKriterium(kriterium.castToString(kriterium).getValue());
			}
		}
	}

	private void readKontraIndikationen(DigaVerordnungseinheit diga, ChargeItemDefinition item) {

		if (item.getExtensionByUrl(FhirDigaURIs.HEALTH_APP_PRESCRIPTION_UNIT_KONTRAINDIKATION) != null) {

			List<Type> indikationenTypes =
				item.getExtensionByUrl(FhirDigaURIs.HEALTH_APP_PRESCRIPTION_UNIT_KONTRAINDIKATION)
					.getExtensionsByUrl("diagnose").stream().map(e -> e.getValue()).collect(Collectors.toList());

			for (Type diagnose : indikationenTypes) {
				Optional<Coding> coding = diagnose.castToCodeableConcept(diagnose).getCoding().stream().findFirst();
				if (coding.isPresent()) {
					diga.getKontraindikationen().addIndikation(coding.get().getCode());
				}
			}
		}
	}

	private void readIndikationen(DigaVerordnungseinheit diga, ChargeItemDefinition item) {

		if (item.getExtensionByUrl(FhirDigaURIs.HEALTH_APP_PRESCRIPTION_UNIT_INDIKATION) != null) {

			List<Type> indikationenTypes =
				item.getExtensionByUrl(FhirDigaURIs.HEALTH_APP_PRESCRIPTION_UNIT_INDIKATION)
					.getExtensionsByUrl("diagnose").stream().map(e -> e.getValue()).collect(Collectors.toList());

			for (Type diagnose : indikationenTypes) {
				Optional<Coding> coding = diagnose.castToCodeableConcept(diagnose).getCoding().stream().findFirst();
				if (coding.isPresent()) {
					diga.getIndikationen().addIndikation(coding.get().getCode());
				}
			}
		}
	}

	private void readAnwendungsdauer(DigaVerordnungseinheit diga, ChargeItemDefinition item) {
		Type anwendungsdauer =
			item.getExtensionByUrl(FhirDigaURIs.HEALTH_APP_PRESCRIPTION_UNIT_ANWENDUNGSDAUER).getValue();
		diga.setAnwendungsTage(anwendungsdauer.castToDuration(anwendungsdauer).getValue().intValue());
	}

	private void readRootDeviceInfos(DeviceDefinition rootDevice, DigaVerordnungseinheit diga) {

		//Höchstdauer:
		Type hoechstDauer =
			rootDevice
				.getExtensionByUrl(FhirDigaURIs.HEALTH_APP_NUTZUNGSHINWEIS)
				.getExtensionByUrl("hoechtsdauer")
				.getValue();
		diga.setHoechstDauer(hoechstDauer.castToString(hoechstDauer).asStringValue());

		//Mindestdauer:
		Type mindestDauer =
			rootDevice
				.getExtensionByUrl(FhirDigaURIs.HEALTH_APP_NUTZUNGSHINWEIS)
				.getExtensionByUrl("mindestdauer")
				.getValue();
		diga.setMindestDauer(mindestDauer.castToString(mindestDauer).asStringValue());

		//Zweckbestimmung:
		Type zweckBestimmung = rootDevice.getExtensionByUrl(FhirDigaURIs.HEALTH_APP_ZWECKBESTIMMUNG).getValue();
		diga.setBeschreibung(zweckBestimmung.castToString(zweckBestimmung).asStringValue());

		//DiGA-ID:
		String digaId =
			rootDevice
				.getIdentifier()
				.stream()
				.filter(i -> FhirDigaURIs.DIGA_ID.equals(i.getSystem()))
				.map(i -> i.getValue())
				.findAny()
				.orElse("");
		diga.setDigaId(digaId);

		//Organizations:
		readAppHersteller(rootDevice, diga);

		//Homepage:
		diga.setHomepage(rootDevice.getOnlineInformation());

		//MetaInfos:
		readRootDeviceMetaInfo(diga, rootDevice);

	}

	private void readAppHersteller(DeviceDefinition rootDevice, DigaVerordnungseinheit diga) {

		String reference = rootDevice.getManufacturerReference().getReference();
		Optional<Organization> organization = findOrganization(reference);

		if (!organization.isPresent()) {
			throw new IllegalStateException("Keine Organization gefunden für: " + reference);
		}

		Hersteller hersteller = new Hersteller();

		hersteller.setIk(organization.get().getIdentifierFirstRep().getValue());
		hersteller.setName(organization.get().getName());
		hersteller.setStandort(organization.get().getAddressFirstRep().getCity());

		diga.setHersteller(hersteller);

		readOrganizationMetaInfo(diga, organization.get());
	}

}

/*--- Formatiert nach TK Code Konventionen vom 05.03.2002 ---*/
