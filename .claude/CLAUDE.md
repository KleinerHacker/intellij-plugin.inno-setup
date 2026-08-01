# Globale Regeln

## Programmierung

* Es wird IMMER Kotlin verwendet
* Es wird IMMER Gradle verwendet

## Architektur

* Es gibt drei Bibliotheken in folgender Struktur und Bedeutung:
  * `plugin` - Das IntelliJ Plugin für Inno Setup, beinhaltet NUR IDE spezifische Erweiterungen
  * `language` - Logischer leerer Knoten
    * `preprocessor` - separates Sprach-Plugin nur für den Teil Präprozessor für Inno Setup Skripte, beinhaltet NUR sprachbezogene Erweiterungen (auch für den Editor)
    * `script` - separates Sprach-Plugin nur für den Teil Skript für Inno Setup (zieht `preprocessor` mit an), beinhaltet NUR sprachbezogene Erweiterungen (auch für den Editor)
  * `buildSrc` - weitere Dinge für Gradle Funktionsweisen

## Struktur

* Root Paket ist IMMER `org.pcsoft.intellij.plugin.inno_setup`
  * Für `plugin` IMMER Root
  * Für `preprocessor` Root + `preprocessor`
  * Für `script` Root + `script`
* Darunter befinden sich jeweils spezialisierte Pakete für:
  * `language` - Spracheinheiten wie Parser, Editor Features, ...
  * `services` - Injectable Services (IntelliJ Platform)
  * `types` - Allgemeine Typen
  * `settings` - Einstellungsfeatures, Einstellungsseiten, ...
  * `build` - Alles rund um das Bauen von Inno Setup, z. B. Run Config, Building, ...
* Bei Abweichungen MUSS der Nutzer gefragt werden

## Bauen

* Es MUSS immer ein Build erfolgen mit dem Gradle Ziel `build` nach jeder Änderung
* Es MUSS immer ein Plugin Verify erfolgen mit dem Gradle Ziel `verifyPlugin` nach jeder Änderung
  * Die Verifizierung MUSS fehlerfrei durchlaufen, DARF aber Warnungen enthalten
  * Wird eine Warnung für DEPRECATION oder REMOVAL erfasst, MUSS eine Anpassung des Codes erfolgen
  * Wird ein Fehler festgestellt, MUSS eine Anpassung des Codes erfolgen
  * Sind die o. g. Fälle nicht reparierbar, MUSS der Benutzer gefragt werden, was zu tun ist, mit einer Liste erarbeiteter Lösungsvorschläge

## Testen

* Es MUSS das IntelliJ Plugin Test System genutzt werden
* Es MUSS jeder Anwendungsfall getestet werden
* Die Code Coverage sollte mindestens 90% erreichen, optimaler Weise 100%, wenn möglich
* Die Paketstruktur aus dem produktiven Code ist zu spiegeln
* Der Test ist in zwei Kategorien zu unterteilen
  * **Developer Tests** - einfache Unit Tests zum Testen einzelner Funktionalitäten
  * **Integrationstests** - Test, die vollumfängliche Funktionen testen oder auf Performance aus sind

## Dokumentation

### Code

* JEDER öffentliche Member in JEDER Quellcode Datei (außer automatisch erzeugter Quellcodedateien) ist mittels KDocs zu dokumentieren
* JEDE Test Methode ist mit einem ausführlichen KDoc zum Anwendungsfall zu dokumentieren

### Readme

* Es MUSS eine Anleitung existieren, wie das Projekt auszuchecken ist und zu bauen und starten ist
* JEDES Feature muss in einem Anstrich dokumentiert sein
* Es MUSS eine Anleitung zur Einbindung der Artefakte existieren
* Es MUSS ein kurzer Abriss über das "WAS" des Projekts existieren
* Es MUSS ein Verweis auf MKDocs Doku (gh-pages), API Doku und Lizenz-Report enthalten sein
* Die Readme MUSS automatisch nach Änderungen geprüft und angepasst werden, wenn erforderlich

### MKDocs

* Es MUSS MKDocs eingebunden sein unter `docs`
* Struktur:
  * `docs/mkdocs.yml` - Stammdatei
  * `docs/docs` - *.MD Dateien
  * `docs/assets` - weitere Asset Dateien
* Die Dokumentation MUSS nach jeder Änderung geprüft und ggf. angepasst werden

### CHANGELOG.md

* Es MUSS eine Änderungsdatei enthalten sein
* Diese MUSS nach einer Änderung mit den erfolgten Änderungen aktualisiert werden
  * Die Änderungen MÜSSEN für den Nutzer ersichtlich sein, sonst DÜRFEN diese NICHT in das Changelog wandern
* Das vorgegebene Format MUSS eingehalten werden
  * Neuerungen MÜSSEN unter `[UNRELEASED]`

### STATUS.md

* Es MUSS der aktuelle Umsetzungsstatus gegenüber der Inno Setup Dokumentation hier festgehalten werden

### Externe Dokumentation

* Jegliche Inno Setup Dokumentation ist hier enthalten: `https://jrsoftware.org/ishelp/`

## Planung

* Bei JEDER Änderung MUSS ein Plan erstellt werden
  * Ein Wechsel zum Plan Modus MUSS erfolgen
* Der PLAN DARF KEINE Zusammenfassung oder Erklärung der Änderungen enthalten
* DieUmsetzungstasks MÜSSEN in kurzen Anstrichen mit nicht mehr als 20 Worten pro Anstrich und max. 10 Anstrichen pro Task erklärt werden
* Der Plan MUSS in das lokale `.claude/plans` Verzeichnis geschrieben werden, zusammen mit einer Statusdatei
  * Namensschema: 
    * Plan: `<Name>.md`
    * Status: `<Name>-status.md`
  * Der Status MUSS IMMER aktuell gehalten werden
* Bei Neustart eines bestehenden Plans nach Unterbrechung MUSS in den Plan Modus gewechselt werden
  * Die noch umzusetzenden Punkte werden nach vorgegeben Schema erneut dargelegt

## Implementierung

* Alle Änderungen in einer einzelnen Datei MÜSSEN mit einem Mal (einem Schreibvorgang) erfolgen

## GIT

* Alle Änderungen erfolgen über GIT:
  * Umbenennung / Verschiebung: `git mv`
  * Löschen: `git rm`
  * Erstellen: nach Erstellung mit `git add` hinzufügen
* Es DÜRFEN NIE Commits, Pushes, Pulls oder sonst welche Aktionen, die mit dem Git Server kommunizieren, aufgerufen werden.
  * Sollte es erforderlich sein, MUSS der Nutzer gefragt werden