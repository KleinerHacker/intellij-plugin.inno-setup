<!-- Keep a Changelog guide -> https://keepachangelog.com -->

# Inno-setup Changelog

## [Unreleased]

### Neu

- **ISPP-Sprachunterstützung**: Vollständiges Parsing, Syntax-Highlighting, Code-Completion, Navigation und Injection
  des Inno Setup Pre-Processors in ISS-Skripte
- **`#define`-Konstanten**: Referenzauflösung, Code-Completion, Highlighting sowie Umbenennungs-Refactoring für einfache
  und typisierte `#define`-Direktiven
- **Section-Querverweise**: Referenzen zwischen Sections (z. B. `[Files]` → `[Components]`) über mixin-basierte
  Referenzbehandlung
- **Farbeinstellungsseite**: Konfigurierbare Einfärbung von Syntax-Elementen über die IDE-Einstellungen
- **Neue Datei-Aktion**: `.iss`-Dateien direkt aus dem „Neu…"-Menü anlegen
- **Spec-basierte Architektur**: YAML-Spezifikationsdateien für alle Inno Setup Sections, Attribute, Flags, Konstanten
  und ISPP-Direktiven als Grundlage für Completion und Dokumentation
- **Inline-Dokumentation**: Kontextbezogene Dokumentation für alle Inno Setup Sections und deren Schlüssel direkt im
  Editor
- **Boolean-Completion**: Automatische Vervollständigung boolescher Werte (`yes`/`no`) für Direktiven des `[Setup]`
  -Abschnitts

### Verbessert

- **Eingebettete Konstanten in Strings**: Zeichenketten mit `{app}`, `{win}`, `{#Name}` u. a. werden korrekt geparst,
  hervorgehoben und als Konstanten-Referenz behandelt
- **Fließkommazahlen**: Zahlen mit Dezimalpunkt werden in ISS- und ISPP-Grammatik sowie im Lexer erkannt
- **Brace Matching und Klammerunterstützung**: Passende Klammern- und Brace-Paare werden im Editor hervorgehoben
- **Schlüsselpositionserkennung in der Completion**: Die Completion unterscheidet zuverlässig zwischen Schlüssel- und
  Wertposition in Direktiv- und Parameterzeilen
- **Nachgestellte Semikolons**: Abschließende `;` in Parameterzeilen (z. B. `[Files]`) werden akzeptiert und korrekt
  verarbeitet
- **Boolean-Hervorhebung**: Boolesche Werte (`yes`/`no`) werden als eigener Token-Typ hervorgehoben
- **Grammatik-Fehlerwiederherstellung**: Der Parser erholt sich robuster von fehlerhaften Zeilen; verlorene Token (
  Dangling Tokens) in Parameterabschnitten werden gesondert behandelt
- **Fold-Bereiche**: Korrekte Berechnung von Faltbereichen unter Berücksichtigung von CRLF-Zeilenenden
- **Icons**: Überarbeitete und verbesserte Datei- und Element-Icons
- **Commenter**: Zeilenkommentare (`; …`) werden über den Standard-Commenter-Shortcut ein- und ausgeschaltet

### Kompatibilität

- K2-Compiler-Unterstützung (IntelliJ-interne Kotlin-Analyse)
- Aktualisierte JetBrains-IDE-Kompatibilität
