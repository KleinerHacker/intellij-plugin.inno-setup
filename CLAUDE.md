# Inno Setup IntelliJ Plugin

IntelliJ plugin for Inno Setup `.iss` script files.
Inno Setup documentation: https://jrsoftware.org/ishelp/

## Changelog policy

`CHANGELOG.md` must only contain changes that an **end user of the plugin** can perceive directly — new
features, visible behaviour changes, and user-facing bug fixes. **No internals:** architecture refactors,
package renames, class/namespace changes, build system updates, documentation site changes, test fixes, or
any other implementation detail that has zero user-visible effect must not appear in the changelog.

> **Documentation coverage:** Which parts of the official Inno Setup docs are already implemented in the
> plugin, and what is still missing, is tracked in [`DOC_STATUS.md`](DOC_STATUS.md). This file must be kept
> up to date for new Inno Setup releases or after extensions of the spec YAML.

## Official docs — web directory structure

When researching Inno Setup / ISPP behavior, use these URLs directly (instead of searching). The
`index.php` / root pages are pure framesets without content — **always fetch the individual `topic_*.htm`
pages** (those provide full text and are readable via WebFetch).

| Area | Base URL | Content |
|---|---|---|
| Inno Setup (ISS) | `https://jrsoftware.org/ishelp/topic_<name>.htm` | Sections, constants, Setup directives, `[Code]` (Pascal Script) |
| Preprocessor (ISPP) | `https://jrsoftware.org/ispphelp/topic_<name>.htm` | `#define`/`#include`/`#if`…, expressions, built-in functions |
| Full-text mirror | `https://documentation.help/Inno-Setup-Preprocessor/topic_<name>.htm` | Alternative without frameset (sometimes HTTP 403) |

Frequently used ISPP topics (`topic_<name>`):

- `define` — `#define` syntax incl. scope (`public`/`protected`/`private`) and types
  (`void`/`int`/`str`/`any`); basis for `forbidden_variables_name` in `spec/is-preprocessor.yaml`.
- `expressions` — expression syntax, type system (`void`/`int`/`str`/`any`), operators, `defined`.
- `funcs` / `int` / `str` — built-in functions.
- `ifdef`, `include`, `pragma`, `for`, `sub` — further directives.

## Modules (Gradle multi-module)

The project is a **Gradle multi-module build**. The root is a pure aggregator (no code, no `plugin.xml`):

```
:plugin  →  :language:script  →  :language:preprocessor
```

- **`:language:preprocessor`** (`language/preprocessor/`) — the ISPP preprocessor language: lexer/parser/PSI,
  highlighter, basic annotator + quickfixes, brace matcher, ISPP references, the whole `expression/` engine,
  `IsPreprocessorService` + ISPP spec. The **lowest layer**: depends on no other plugin module and must stay
  free of any section/script type. Host-aware behaviour is reached through the `IsPreprocessorHost` /
  `IsPreprocessorHostLine` interfaces (defined here, implemented in `:language:script`). Also hosts the shared
  `PluginBundle` (+ `.properties`×4), `Generated` annotation and `IsSectionSpecTarget` (lowest common types).
- **`:language:script`** (`language/script/`) — the Inno Setup language: section/INI grammar (shared by
  `.iss`/`.isl`/`.ist`), highlighter, folding, brace matching, commenter, basic annotator +
  quickfixes, section references, include infrastructure, the ISPP **injector**, and `IsSpecService`/
  `IsConstantService`/`IsLanguageDataService`/`IsSettingsService`. Depends on `:language:preprocessor`.
- **`:plugin`** (`plugin/`) — the publishable plugin: all **IDE features** (completion, find-usages, file types,
  refactoring, structure view, documentation, intentions, inlay hints, reference searchers), build/run
  integration, settings **UI**, the main `plugin.xml`, color schemes and icons. Depends on `:language:script`.

**plugin.xml is split per module:** each language module ships a fragment
(`META-INF/inno-setup-preprocessor.xml`, `…-script.xml`) with its language-level registrations; the main
`plugin/…/META-INF/plugin.xml` pulls them in via `<xi:include>`. The language modules are bundled as regular
`implementation(project(...))` libraries (one shared classloader), so there are no split-package/classloader
pitfalls. **Tests are distributed per module:** `:language:preprocessor/src/test/` holds the pure expression-engine
and service tests (plain JUnit, no platform fixture); `:language:script/src/test/` holds all ISS/ISL/IST
grammar, annotator and include-infrastructure tests (they use `IsTimedBasePlatformTestCase` against the module's
own plugin XML fragment); `:plugin/src/test/` holds all IDE-feature tests (completion, references, editor,
build/run, settings) that need the fully assembled `plugin.xml`. The test-infra base classes
(`IsTimedBasePlatformTestCase`, `IsTimedTestCase`, `IsTestMethodTimeout`) are duplicated into each
module's `src/test/kotlin/…/test/` so they require no cross-module test dependencies. Pure helper/shared
build config lives in `buildSrc` (`inno-setup.platform-module` convention).

## Build

```
./gradlew runIde          # Start the plugin
./gradlew check           # Tests + verification
./gradlew generateSources # Root umbrella: regenerate all lexers + parsers across modules (after BNF/Flex changes)
# Per-module GrammarKit tasks: :language:script:generateIsSectionParser / generateIsSectionLexer /
#   generateIsTemplateParser / generateIsTemplateLexer ; :language:preprocessor:generateIsPreprocessorParser /
#   generateIsPreprocessorLexer
./gradlew buildDocs   # Build the MkDocs site (incl. Dokka + licence report) into build/docs; --strict = generation test
```

> **MkDocs tasks** (group `MKDocs`): `buildDocs` builds the site with `mkdocs build --clean --strict` into
> `build/docs` (path via `site_dir` in `docs/mkdocs.yml`) — no serve, no deploy; `--strict` makes the build
> fail on broken links/warnings, so it acts as a generation test. `runDocs` = local `mkdocs serve`,
> `deployDocs` = `gh-deploy` to `gh-pages`. All three first run `copyDokka` + `copyLicenceReport` (copy into
> `docs/docs/dokka` resp. `…/licences`) and clean up again afterwards.

**Important:** never edit the generated sources manually — each module generates into its own
`<module>/build/generated/` via GrammarKit.

## i18n

All **directly user-visible UI texts** go through the `PluginBundle` object
(`PluginBundle.message("key", args…)`, `DynamicBundle`) — **no** hardcoded UI strings. Bundle (in the lowest
module so every module can use it): `language/preprocessor/src/main/resources/messages/PluginBundle.properties`
(en) + `_ja`/`_ko`/`_zh_CN`; always maintain new keys in **all four** bundles.

**Not in i18n:** console output, diagnostic/debug messages, log texts, and all other output that does not
appear directly in UI elements (labels, tooltips, action texts, dialogs). These are written as hardcoded
English strings.

**Important — avoid `%key%` placeholders in plugin.xml:** On the target platform (2025.3), `%key%` in
`<action text|description>` is **not** resolved reliably (not even with `resource-bundle` on the `<actions>`
tag). Instead:

- **Actions**: set text/description in code via the `DumbAwareAction` constructor with suppliers
  (`{ PluginBundle.message(...) }`) resp. in `update()` — not via plugin.xml attributes.
- **Configurables**: `key="…" bundle="messages.PluginBundle"` on the `<applicationConfigurable>`/
  `<projectConfigurable>` (this works, unlike `%…%` in `displayName`); additionally provide
  `getDisplayName()` via `PluginBundle`.
- **`<fileType name>`**: internal identifier, must match `getName()` **literally** — no i18n.

## Namespaces

Three orthogonal axes, recognizable from the `Is`+role class prefix and the package path:

- **`IsScript…`** — script file type `.iss` + shared host base (`IsScriptFile`/`IsScriptLanguage`/
  `IsScriptFileType`, `IsIcons`), under `language/file_type/script/`.
- **`IsLanguage…`** — language file type `.isl` + policy (`IsLanguageFile`/`IsLanguageFileType`/
  `IsLanguageSections`/`IsLanguageAnnotator`), under `language/file_type/lang/`.
- **`IsSection…`** — section/INI grammar (shared across `.iss` + `.isl`), under `language/parser/section/`.
- **`IsPreprocessor…`** — preprocessor grammar (ISPP), under `language/parser/preprocessor/`.
- **`language/feature/`** — file-type- & grammar-spanning IDE features: `reference/`, `findusage/`,
  `completion/`, `editor/`.
- **`Is…`** (umbrella) — Inno-Setup-wide infrastructure in `services/`, `settings/`, `types/`
  (`IsSpecService`, `IsSettingsService`, `IsSectionSpec`/`IsLanguageCodeSpec`, etc.).

## Project structure

> **Paths below are module-relative.** Since the multi-module split, prefix each `src/main/...` with its
> owning module root: section/template grammar, file types, features-(language-core), services and specs live
> under `language/script/`; the ISPP grammar, spec and `expression/` engine under `language/preprocessor/`;
> IDE features, build/run, settings UI, `colorSchemes/`, icons and the main `plugin.xml` under `plugin/`.
> Generated sources are per-module under `<module>/build/generated/` (no longer `build/parsing/gen`).

| Path                                                                                | Content                                                                                                                                                     |
|-------------------------------------------------------------------------------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `src/main/resources/parsing/IsSectionGrammar.bnf`                                   | GrammarKit grammar of the sections (parser + `IsSection…` PSI). Rules: `block`/`header`/`title`/`directiveEntry`/`paramPair`/`constant`/`preprocessorLine` |
| `src/main/resources/parsing/IsSectionLexer.flex`                                    | JFlex lexer with states (`YYINITIAL`, `VALUE`, `IN_STRING`, `IN_STRING_CONSTANT`)                                                                          |
| `src/main/resources/parsing/IsPreprocessorGrammar.bnf` / `IsPreprocessorLexer.flex` | GrammarKit grammar + lexer of the preprocessor                                                                                                              |
| `src/main/resources/spec/`                                                          | YAML specs for sections, attributes, flags, constants, ISPP directives                                                                                      |
| `src/main/kotlin/…/language/file_type/script/`                                      | Host (`IsScriptFile`/`IsScriptLanguage`/`IsScriptFileType`) + `IsIcons` + shared `PsiUtils`, `action/`                                                       |
| `src/main/kotlin/…/language/file_type/lang/`                                        | `.isl` file type + policy (`IsLanguageSections`) + `parsing/IsLanguageAnnotator` + `action/`                                                                 |
| `src/main/kotlin/…/language/parser/section/`                                        | Section parsing (flat, kein `parsing/`-Unterpaket): `PsiUtils`, Lexer-Adapter/ParserDefinition/Highlighting + generierter `IsSectionParser` + `psi/` (`impl/` mixins) + `quickfix/` |
| `src/main/kotlin/…/language/parser/section/psi/impl/`                               | Hand-written mixins (e.g. `IsSectionParamPairMixinImpl`, `IsSectionPreprocessorLineMixinImpl`)                                                               |
| `src/main/kotlin/…/language/parser/preprocessor/`                                   | Preprocessor (flach wie `section/`): Lexer/Parser/Highlighting + `expression/` + `injection/IsPreprocessorInjector` + Host-Dateien                            |
| `src/main/kotlin/…/language/feature/{reference,findusage,completion,editor}/`       | Cross-cutting: references/searcher, find-usages/refactoring, completion, editor/structure/doc                                                                |
| `src/main/kotlin/…/services/`                                                       | Spec services (singleton, lazy-loaded)                                                                                                                       |

> **Note:** the section token for `#…` lines is called `HASH_LINE` (not `PREPROCESSOR_LINE`), because the
> rule `preprocessorLine` would otherwise collide its element-type constant `PREPROCESSOR_LINE` with the token.
> Tests mirror the production structure 1:1 under `src/test/kotlin/…`.

## ISS script format (quick reference)

- **Directive sections** (`[Setup]`): `Key=Value`
- **Parameter sections** (`[Files]`, `[Registry]`, …): `Key: Value; Key: Value`
- **Constants**: `{app}`, `{win}`, `{#IsppName}`, `{%ENV}`, `{reg:…}`, etc.
- **ISPP**: `#define Name Value`, `#include "file"`, `{#Name}` for inline use
- The **[Code]** section must be the last section in the script

## Lexer states

| State                | Context                                          |
|----------------------|--------------------------------------------------|
| `YYINITIAL`          | Start of line (keys, section headers, preprocessor) |
| `VALUE`              | After `=` or `:` (value area)                    |
| `IN_STRING`          | Inside `"…"`                                     |
| `IN_STRING_CONSTANT` | Inside `{…}` within a string                     |

`VALUE_CHAR = [^\r\n{};:=\"()#\t ]` — `#` is deliberately excluded so that HASH tokens are recognized correctly.

**Identifier exception for flags:** identifiers generally start with a letter (`IDENTIFIER`). The only
exception are flag identifiers, which may start with a digit (e.g. `64bit`). Since flags only occur in the
value area, the relaxed form `VALUE_IDENTIFIER` applies in the `VALUE` state
(`({ALPHA} | [0-9]+ {ALPHA}) {IDENT_CHAR}*`); a purely numeric token remains `NUMBER`. In `YYINITIAL`
(keys/headers) and `IN_STRING_CONSTANT` (`{…}` constants) the letter-leading `IDENTIFIER` still applies.
