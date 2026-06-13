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

## i18n

Alle **unmittelbar benutzersichtbaren UI-Texte** laufen über das `PluginBundle`-Objekt
(`PluginBundle.message("key", args…)`, `DynamicBundle`) — **keine** hartkodierten UI-Strings. Bundle:
`src/main/resources/messages/PluginBundle.properties` (en) + `_ja`/`_ko`/`_zh_CN`; neue Keys immer in
**allen vier** Bundles pflegen.

**Nicht in i18n:** Konsolenausgaben, Diagnose-/Debug-Meldungen, Log-Texte und alle anderen Ausgaben, die
nicht direkt in UI-Elementen (Labels, Tooltips, Action-Texte, Dialoge) erscheinen. Diese werden als
hardkodierte englische Strings geschrieben.

**Wichtig — `%key%`-Platzhalter in plugin.xml meiden:** In der Zielplattform (2025.3) werden `%key%` in
`<action text|description>` **nicht** zuverlässig aufgelöst (auch nicht mit `resource-bundle` am
`<actions>`-Tag). Stattdessen:

- **Actions**: Text/Description im Code über den `DumbAwareAction`-Konstruktor mit Suppliern
  (`{ PluginBundle.message(...) }`) bzw. in `update()` setzen — nicht über plugin.xml-Attribute.
- **Configurables**: `key="…" bundle="messages.PluginBundle"` am `<applicationConfigurable>`/
  `<projectConfigurable>` (das funktioniert, anders als `%…%` im `displayName`); `getDisplayName()`
  zusätzlich über `PluginBundle`.
- **`<fileType name>`**: interner Bezeichner, muss **wörtlich** `getName()` entsprechen — kein i18n.

## Namensräume

Drei orthogonale Achsen, am `Is`+Rolle-Klassenprefix und am Paketpfad ablesbar:

- **`IsScript…`** — Script-Dateityp `.iss` + geteilte Host-Basis (`IsScriptFile`/`IsScriptLanguage`/
  `IsScriptFileType`, `IsIcons`), unter `language/file_type/script/`.
- **`IsLanguage…`** — Language-Dateityp `.isl` + Policy (`IsLanguageFile`/`IsLanguageFileType`/
  `IsLanguageSections`/`IsLanguageAnnotator`), unter `language/file_type/lang/`.
- **`IsSection…`** — Section-/INI-Grammatik (geteilt über `.iss` + `.isl`), unter `language/parser/section/`.
- **`IsPreprocessor…`** — Präprozessor-Grammatik (ISPP), unter `language/parser/preprocessor/`.
- **`language/feature/`** — dateityp- & grammatik-übergreifende IDE-Features: `reference/`, `findusage/`,
  `completion/`, `editor/`.
- **`Is…`** (umbrella) — Inno-Setup-weite Infrastruktur in `services/`, `settings/`, `types/`
  (`IsSpecService`, `IsSettingsService`, `IsSectionSpec`/`IsLanguageCodeSpec` usw.).

## Projektstruktur

| Pfad                                                                                | Inhalt                                                                                                                                                     |
|-------------------------------------------------------------------------------------|------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `src/main/resources/parsing/IsSectionGrammar.bnf`                                   | GrammarKit-Grammatik der Sections (Parser + `IsSection…`-PSI). Regeln: `block`/`header`/`title`/`directiveEntry`/`paramPair`/`constant`/`preprocessorLine` |
| `src/main/resources/parsing/IsSectionLexer.flex`                                    | JFlex-Lexer mit Zuständen (`YYINITIAL`, `VALUE`, `IN_STRING`, `IN_STRING_CONSTANT`)                                                                        |
| `src/main/resources/parsing/IsPreprocessorGrammar.bnf` / `IsPreprocessorLexer.flex` | GrammarKit-Grammatik + Lexer des Präprozessors                                                                                                             |
| `src/main/resources/spec/`                                                          | YAML-Specs für Sections, Attribute, Flags, Konstanten, ISPP-Direktiven                                                                                     |
| `src/main/kotlin/…/language/file_type/script/`                                      | Host (`IsScriptFile`/`IsScriptLanguage`/`IsScriptFileType`) + `IsIcons` + geteilte `PsiUtils`, `action/`                                                   |
| `src/main/kotlin/…/language/file_type/lang/`                                        | `.isl`-Dateityp + Policy (`IsLanguageSections`) + `parsing/IsLanguageAnnotator` + `action/`                                                                |
| `src/main/kotlin/…/language/parser/section/`                                        | Section-Parsing: `PsiUtils` + `parsing/` (`psi/impl/`-Mixins, `quickfix/`)                                                                                 |
| `src/main/kotlin/…/language/parser/section/parsing/psi/impl/`                       | Handgeschriebene Mixins (z. B. `IsSectionParamPairMixinImpl`, `IsSectionPreprocessorLineMixinImpl`)                                                        |
| `src/main/kotlin/…/language/parser/preprocessor/`                                   | Präprozessor: `parsing/` + `injection/IsPreprocessorInjector`; spiegelt `parser/section/`                                                                  |
| `src/main/kotlin/…/language/feature/{reference,findusage,completion,editor}/`       | Cross-Cutting: References/Searcher, Find-Usages/Refactoring, Completion, Editor/Structure/Doc                                                              |
| `src/main/kotlin/…/services/`                                                       | Spec-Services (Singleton, lazy-loaded)                                                                                                                     |

> **Hinweis:** Das Section-Token für `#…`-Zeilen heißt `HASH_LINE` (nicht `PREPROCESSOR_LINE`), weil die
> Regel `preprocessorLine` ihre Element-Type-Konstante `PREPROCESSOR_LINE` sonst mit dem Token kollidieren ließe.
> Tests spiegeln die Produktivstruktur 1:1 unter `src/test/kotlin/…`.

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
