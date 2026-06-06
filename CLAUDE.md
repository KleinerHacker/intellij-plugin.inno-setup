# Inno Setup IntelliJ Plugin

IntelliJ-Plugin für Inno Setup `.iss`-Skriptdateien.
Inno Setup-Dokumentation: https://jrsoftware.org/ishelp/

> **Dokumentationsabdeckung:** Welche Teile der offiziellen Inno-Setup-Doku bereits im Plugin umgesetzt
> sind und was noch fehlt, ist in [`DOC_STATUS.md`](DOC_STATUS.md) festgehalten. Diese Datei muss bei
> neuen Inno-Setup-Releases oder nach Erweiterungen des Spec-YAMLs aktuell gehalten werden.

## Build

```
./gradlew runIde          # Plugin starten
./gradlew check           # Tests + Verifikation
./gradlew generateSources # alle Lexer + Parser neu generieren (nach BNF/Flex-Änderungen)
./gradlew generateLexers / generateParsers   # nur Lexer bzw. nur Parser generieren
```

**Wichtig:** `build/parsing/gen/` niemals manuell bearbeiten — wird von GrammarKit generiert.

## Namensräume

- **`Iss…`** — gemeinsam genutzte Klassen des Skripts (Host-`IssFile`/`IssLanguage`/`IssFileType`, Icons, `action/`,
  `services/`, `types/`, `PsiUtils`-Brücke).
- **`Ispp…`** — Präprozessor-Parsing (`language/ispp/**`).
- **`Isi…`** (Inno Setup INI) — Section-Parsing und der gesamte Rest (`language/isi/**`); spiegelt strukturell
  `language/ispp/**`.

## Projektstruktur

| Pfad                                                            | Inhalt                                                                              |
|-----------------------------------------------------------------|-------------------------------------------------------------------------------------|
| `src/main/resources/parsing/IsiGrammar.bnf`                     | GrammarKit-Grammatik der Sections (Parser + `Isi…`-PSI)                             |
| `src/main/resources/parsing/IsiLexer.flex`                      | JFlex-Lexer mit Zuständen (`YYINITIAL`, `VALUE`, `IN_STRING`, `IN_STRING_CONSTANT`) |
| `src/main/resources/parsing/IsppGrammar.bnf` / `IsppLexer.flex` | GrammarKit-Grammatik + Lexer des Präprozessors                                      |
| `src/main/resources/spec/`                                      | YAML-Specs für Sections, Attribute, Flags, Konstanten, ISPP-Direktiven              |
| `src/main/kotlin/…/language/`                                   | Host (`IssFile`/`IssLanguage`/`IssFileType`) + geteilte `PsiUtils`-Brücke           |
| `src/main/kotlin/…/language/isi/`                               | Section-Parsing: `PsiUtils` + `parsing/`, `navigation/`, `completion/`, `editor/`   |
| `src/main/kotlin/…/language/isi/parsing/psi/impl/`              | Handgeschriebene Mixins (z. B. `IsiParamPairMixinImpl`, `IsiIsppLineMixinImpl`)     |
| `src/main/kotlin/…/language/ispp/`                              | Präprozessor: spiegelt strukturell `language/isi/` (`IsppDirectiveMixinImpl` usw.)  |
| `src/main/kotlin/…/services/`                                   | Spec-Services (Singleton, lazy-loaded)                                              |

## ISS-Skriptformat (Kurzreferenz)

- **Directive-Sections** (`[Setup]`): `Key=Value`
- **Parameter-Sections** (`[Files]`, `[Registry]`, …): `Key: Value; Key: Value`
- **Konstanten**: `{app}`, `{win}`, `{#IsppName}`, `{%ENV}`, `{reg:…}` usw.
- **ISPP**: `#define Name Value`, `#include "file"`, `{#Name}` zur Inline-Verwendung
- **[Code]**-Section muss die letzte Section im Skript sein

## Lexer-Zustände

| Zustand              | Kontext                                          |
|----------------------|--------------------------------------------------|
| `YYINITIAL`          | Zeilenanfang (Keys, Sectionheader, Präprozessor) |
| `VALUE`              | Nach `=` oder `:` (Wertbereich)                  |
| `IN_STRING`          | Innerhalb `"…"`                                  |
| `IN_STRING_CONSTANT` | Innerhalb `{…}` in einem String                  |

`VALUE_CHAR = [^\r\n{};:=\"()#\t ]` — `#` ist bewusst ausgeschlossen, damit HASH-Token korrekt erkannt werden.
