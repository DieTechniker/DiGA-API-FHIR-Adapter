*english version below*

# Parser für DiGA-API des BfArM

Dieses Repository beinhaltet ein einfaches Java-Programm, das aus bestimmten FHIR-XML-Dateien des BfArM Informationen zu den DiGA-Verordnungseinheiten parst und diese als flache Liste ausgibt. 

Als Parser werden die HAPI-Fire Bibliotheken verwendet: https://hapifhir.io/

## Bauen mit maven

```bash
mvn install
```

## Verwendung

Im Projektverzeichnis kann nach dem Build folgender Befehl ausgeführt werden:

```bash
$  java -jar target/diga-api-fhir-adpater.jar -in <input-dir>
```

Im input-dir müssen alle hier genannten Dateien als JSON-Files vorliegen: 

- CatalogEntries.xml
- DeviceDefinitions.xml
- ChargeItemDefinitions.xml
- Organizations.xml

Die Ressourcen müssen extern beim BfArM über einen REST-Service angeforert werden, für den man sich separat anmelden muss. Die Verwendung des REST-Services ist hier beschrieben: https://simplifier.net/guide/diga/Endpunkte

Usage:

```bash
usage: java -jar diga-api-fhir-adapter.jar [optionen]
 -h,--help               prints the help
 -in,--input-dir <arg>   directory with FHIR json-input files
```

## Ausgabe

Das Programm erstellt nach erfolgreicher Ausführung eine Datei mit dem Namen "DigaVerzeichnis.json" im Projektverzeichnis mit allen Infos zu den DiGA Verordnungseinheiten.


**english version** =====================================================================

# Parser for BfArM's DiGA-API

This repository contains a simple java programm to map specific FHIR XML files of BfArM's DiGA-API to a flat list of "DiGA Verordnungseinheiten". 

The programm uses the HAPI-Fire dependency: https://hapifhir.io/

## Build with maven

```bash
mvn install
```

## Usage

Within the project directory you can start the program with the following command:

```bash
$  java -jar target/diga-api-fhir-adpater.jar -in <input-dir>
```

The input-dir must contain all of the following files: 

- CatalogEntries.xml
- DeviceDefinitions.xml
- ChargeItemDefinitions.xml
- Organizations.xml

Die Ressourcen müssen extern beim BfArM über einen REST-Service angeforert werden, für den man sich separat anmelden muss. Die Verwendung des REST-Services ist hier beschrieben: https://simplifier.net/guide/diga/Endpunkte

Usage:

```bash
usage: java -jar diga-api-fhir-adapter.jar [optionen]
 -h,--help               prints the help
 -in,--input-dir <arg>   directory with FHIR json-input files
```

## Output

The program creates the file "DigaVerzeichnis.json" within the projekt directory.

