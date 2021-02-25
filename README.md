# Parser für DiGA-API des BfArM

Dieses Repository beinhaltet ein einfaches Java-Programm, das aus bestimmten FHIR-Json-Dateien des BfArM Informationen zu den DiGA-Verordnungseinheiten parst und diese als flache Liste ausgibt. 

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

Im <input-dir> müssen alle oben genannten Dateien als JSON-Files vorliegen. 
Das sind im Detail folgende Dateien:

- CatalogEntries.json
- DeviceDefinitions.json
- ChargeItemDefinitions.json
- Organizations.json

Die Ressourcen müssen extern beim BfArM über einen REST-Service angeforert werden, für den man sich separat anmelden muss. Die Verwendung des REST-Services ist hier beschrieben: https://simplifier.net/guide/diga/Endpunkte

Usage:

```bash
usage: java -jar diga-api-fhir-adapter.jar [optionen]
 -h,--help               prints the help
 -in,--input-dir <arg>   directory with FHIR json-input files
```

## Ausgabe

Das Programm erstellt nach erfolgreicher Ausführung eine Datei mit dem Namen "DigaVerzeichnis.json" im Projektverzeichnis mit allen Infos zu den DiGA Verordnungseinheiten.

