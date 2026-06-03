# Inno Setup IntelliJ Plugin

IntelliJ-Plugin für Inno Setup `.iss`-Skriptdateien.
Inno Setup-Dokumentation: https://jrsoftware.org/ishelp/

## Build

```
./gradlew runIde          # Plugin starten
./gradlew check           # Tests + Verifikation
./gradlew generateIssLexer / generateIssParser   # nach BNF/Flex-Änderungen ausführen
```

**Wichtig:** `build/parsing/gen/` niemals manuell bearbeiten — wird von GrammarKit generiert.

## Projektstruktur

| Pfad | Inhalt |
|---|---|
| `src/main/resources/parsing/IssGrammar.bnf` | GrammarKit-Grammatik (Parser + PSI-Klassen) |
| `src/main/resources/parsing/IssLexer.flex` | JFlex-Lexer mit Zuständen (`YYINITIAL`, `VALUE`, `IN_STRING`, `IN_STRING_CONSTANT`) |
| `src/main/resources/spec/` | YAML-Specs für Sections, Attribute, Flags, Konstanten, ISPP-Direktiven |
| `src/main/kotlin/…/language/` | PSI-Utilities, File/Language-Definitionen |
| `src/main/kotlin/…/language/parsing/psi/impl/` | Handgeschriebene Mixins (z. B. `IssParamPairMixinImpl`, `IssPreprocessorDirectiveMixinImpl`) |
| `src/main/kotlin/…/language/navigation/` | Referenzen, Find Usages, Go to Declaration |
| `src/main/kotlin/…/language/completion/` | Code-Completion-Provider |
| `src/main/kotlin/…/language/parsing/` | Annotator, Highlighting |
| `src/main/kotlin/…/services/` | Spec-Services (Singleton, lazy-loaded) |

## ISS-Skriptformat (Kurzreferenz)

- **Directive-Sections** (`[Setup]`): `Key=Value`
- **Parameter-Sections** (`[Files]`, `[Registry]`, …): `Key: Value; Key: Value`
- **Konstanten**: `{app}`, `{win}`, `{#IsppName}`, `{%ENV}`, `{reg:…}` usw.
- **ISPP**: `#define Name Value`, `#include "file"`, `{#Name}` zur Inline-Verwendung
- **[Code]**-Section muss die letzte Section im Skript sein

## Lexer-Zustände

| Zustand | Kontext |
|---|---|
| `YYINITIAL` | Zeilenanfang (Keys, Sectionheader, Präprozessor) |
| `VALUE` | Nach `=` oder `:` (Wertbereich) |
| `IN_STRING` | Innerhalb `"…"` |
| `IN_STRING_CONSTANT` | Innerhalb `{…}` in einem String |

`VALUE_CHAR = [^\r\n{};:=\"()#\t ]` — `#` ist bewusst ausgeschlossen, damit HASH-Token korrekt erkannt werden.
