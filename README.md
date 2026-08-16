<p align="center">
  <img src="docs/docs/assets/images/inno-setup-logo.png" alt="Inno Setup Logo" width="256"/>
</p>

# Inno Setup – JetBrains Plugin

A JetBrains IDE plugin that brings first-class language support for [Inno Setup](https://jrsoftware.org/isinfo.php)
scripts (`.iss`) to the entire IntelliJ platform family.

The plugin turns any IntelliJ-platform IDE into a full editor for Inno Setup installer scripts — with syntax
highlighting, context-aware completion, inline documentation, reference resolution and validation for `.iss`, `.isl`
and preprocessor (ISPP) code. It also integrates the Inno Setup compiler, so scripts can be built and run straight
from the IDE.

> [!NOTE]
> **AI transparency notice:** The code and documentation of this project were largely created with AI assistance
> (transparency notice pursuant to the EU AI Act).

---

## Documentation

[Full documentation](https://kleinerhacker.github.io/intellij-plugin.inno-setup/) — including a complete reference for
every Inno Setup section and its parameters — is available at
the project's MkDocs site, hosted on GitHub Pages.

To run the documentation site locally:

```bash
# Install dependencies (once)
cd docs
pip install mkdocs mkdocs-material

# Serve locally
mkdocs serve
```

Then open [http://127.0.0.1:8000](http://127.0.0.1:8000) in your browser.

> [API Documentation](https://kleinerhacker.github.io/intellij-plugin.inno-setup/latest/dokka/html/index.html) is
> available, too.

---

## About

[Inno Setup](https://jrsoftware.org/isinfo.php) is a widely-used, free Windows installer builder by Jordan Russell and
Martijn Laan (first released 1997). Its scripts (`.iss`) describe the full installer — files, registry keys, shortcuts,
and optional Pascal scripting — but until now had no dedicated editor support inside JetBrains IDEs.

This plugin closes that gap. The goal is a complete editing experience for `.iss` files: correct highlighting,
context-aware completion, inline documentation, and validated references, regardless of which JetBrains IDE you are
using.

### Features

| Feature                    | Description                                                                                                                                                                           |
|----------------------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| **Syntax highlighting**    | Sections, directives, parameters, constants (`{app}`, `{autopf}`, …), and Pascal code blocks are coloured distinctly                                                                  |
| **Code completion**        | Section names, directive keys, parameter keys, and known flag values are suggested as you type                                                                                        |
| **Inline documentation**   | Hover over any directive or parameter to read its description without leaving the IDE                                                                                                 |
| **Reference resolution**   | Navigate between `Name:` declarations and their usages in `Tasks:`, `Components:`, and `Types:` parameters                                                                            |
| **Structure view**         | Bird's-eye overview of all sections and their entries                                                                                                                                 |
| **Constant validation**    | Built-in constants are recognised and validated, including those embedded inside quoted strings                                                                                       |
| **Brace / quote matching** | Auto-closes `{`, `[`, and `"`                                                                                                                                                         |
| **Code folding**           | Sections, long parameter entries, and `#if … #endif` blocks fold independently                                                                                                        |
| **Code formatting**        | Reformat Code normalises spacing around `=` / `:` / `;` and `[ ]`, keeps one blank line between sections, and spaces preprocessor arithmetic operators; keeps preprocessor line continuations intact and indents their continued lines; configurable under Code Style  |
| **Inlay hints**            | Language flag icons are shown inline next to `Languages:` parameter values                                                                                                            |
| **ISPP computed values**   | A `#define` assembled from other macros and operators shows its statically computed value as an inlay hint at the end of the line, calls to own function-like macros included; plain literals, built-in calls and other non-computable expressions stay unannotated |
| **Build integration**      | Compile `.iss` scripts directly via a context-menu action; optionally run ISCC automatically on project build                                                                         |
| **Build configurations**   | Named sets of compile options (preprocessor symbols, output directory, extra ISCC options) stored one file each in the project's `.build` directory; selectable per run, from the gutter icon and from the context menu, and part of the rebuild decision |
| **Language file support**  | `.isl` language files are recognised, highlighted, and validated alongside `.iss` scripts                                                                                             |
| **ISPP support**           | Preprocessor directives (`#define`/`#undef` with scope keywords, `#include`, `#if`/`#elif`/`#else`/`#endif`, …) are parsed, highlighted, completed, validated, and reference-resolved |
| **ISPP function checking** | Calls to built-in preprocessor functions are checked against their signature: argument count (optional parameters included), argument types, by-reference parameters, and results that carry no value |
| **ISPP line continuation** | A `#…` line ending with a backslash continues on the next line and is treated as one directive throughout the editor |
| **ISPP macro parameters**  | A function-like macro declares its parameters like ISPP does (`#define M(int A, str *S, B = 10)`); calls are checked against that declaration, and the parameters are completed, resolvable and renameable inside the macro body |
| **ISPP build symbols**     | The `/D` symbols of the selected build configuration count as defined everywhere: conditions, `{#Name}` emission, expressions and completion |
| **ISPP parameter info**    | `Ctrl+P` inside a call shows the parameter list and highlights the current argument — for built-ins from their signature, for function-like macros from their parameter declaration resp. the types inferred from the macro body |

### IDE Compatibility

The plugin targets `com.intellij.modules.lang` — available in every full IntelliJ-platform IDE — and bundles its own
runtime dependencies, so it has no hidden requirements on the host IDE.

Works in: **IntelliJ IDEA**, **PyCharm**, **CLion / CLion Nova**, **Rider**, **WebStorm**, **GoLand**, **RubyMine**, *
*DataGrip**, and all other IntelliJ-platform IDEs.

---

## Implementation Status

This section states exactly which parts of Inno Setup the plugin supports today, which parts are only partially
covered, and which are still missing or planned.

> **Reference version:** Inno Setup 7.0.1-beta · **Official docs:** <https://jrsoftware.org/ishelp/> ·
> **Last checked:** 2026-08-04
>
> **Legend:** ✅ implemented · ⚠️ partial · ❌ not implemented / planned · 🗑️ obsolete in Inno Setup
> (intentionally skipped)

### File Types

| Extension | Status | What you get                                                                                                                                                                                                                        |
|-----------|--------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `.iss`    | ✅      | Inno Setup script — full parsing, highlighting, completion, documentation, references and validation                                                                                                                                |
| `.isl`    | ✅      | Inno Setup language file — same language and tooling as `.iss`, but only `[LangOptions]`, `[Messages]` and `[CustomMessages]` are allowed; `[Setup]` is not required, instead `[LangOptions]` with `LanguageName` + `LanguageID` is |

Whether a rule (`required`, `deprecated`, `since`/`until`) applies is decided per file type, so a directive can be
required in a script, in a language file, in both, or in neither.

### Script Sections

| Section             | Entry syntax | Status | Coverage                                                                                            |
|---------------------|--------------|--------|-----------------------------------------------------------------------------------------------------|
| `[Setup]`           | `Key=Value`  | ✅      | All ~158 current directives (166 spec entries incl. legacy `EncryptionKey*` aliases)                |
| `[Types]`           | `Key: Value` | ✅      | Attributes, `iscustom` flag, common parameters                                                      |
| `[Components]`      | `Key: Value` | ✅      | Attributes, flags, common parameters                                                                |
| `[Tasks]`           | `Key: Value` | ✅      | Attributes, flags, common parameters                                                                |
| `[Dirs]`            | `Key: Value` | ✅      | All attributes and flags                                                                            |
| `[Files]`           | `Key: Value` | ✅      | All attributes and flags                                                                            |
| `[Icons]`           | `Key: Value` | ✅      | All attributes and flags                                                                            |
| `[INI]`             | `Key: Value` | ✅      | `Filename` (defaults to `WIN.INI`), `Section`, `Key`, `String` plus all `uninsdelete*` flags        |
| `[Registry]`        | `Key: Value` | ✅      | All attributes and flags                                                                            |
| `[Run]`             | `Key: Value` | ✅      | All attributes and flags                                                                            |
| `[UninstallRun]`    | `Key: Value` | ✅      | All attributes and flags                                                                            |
| `[Languages]`       | `Key: Value` | ✅      | `Name`, `MessagesFile`, `LicenseFile`, `InfoBeforeFile`, `InfoAfterFile`                            |
| `[Messages]`        | `Key=Value`  | ✅      | Full standard `Default.isl` message set (~273 ids) as known keys, plus `lang.` prefix completion    |
| `[CustomMessages]`  | `Key=Value`  | ✅      | Free key names, `lang.` prefix completion, and `{cm:…}` reference / find usages / rename            |
| `[LangOptions]`     | `Key=Value`  | ✅      | All directives incl. `LanguageID` completion; the fonts removed in 6.4 are marked as such           |
| `[InstallDelete]`   | `Key: Value` | ✅      | All attributes                                                                                      |
| `[UninstallDelete]` | `Key: Value` | ✅      | All attributes                                                                                      |
| `[ISSigKeys]`       | `Key: Value` | ✅      | `Name`, `KeyFile`, `PublicX`, `PublicY`, `KeyID`, `Group`, `RuntimeID` (Inno Setup 6.5+)            |
| `[Code]`            | Pascal       | ⚠️     | Recognised and left untouched — no Pascal intellisense, see [Not implemented](#not-implemented-yet) |

#### Cross-section parameters

Five parameters are not section-specific; the plugin models their applicability exactly as the official docs
define it:

| Parameter(s)                                  | Supported by                                                       |
|-----------------------------------------------|--------------------------------------------------------------------|
| `Languages`, `MinVersion`, `OnlyBelowVersion` | all parameter sections                                             |
| `Check`                                       | all parameter sections                                             |
| `Components`                                  | all **except** `[Types]`, `[Components]`                           |
| `Tasks`                                       | all **except** `[Types]`, `[Components]`, `[Tasks]`                |
| `BeforeInstall`, `AfterInstall`               | all **except** `[Languages]`, `[Types]`, `[Components]`, `[Tasks]` |

(`[Languages]` itself takes none of them — only its own five attributes.)

#### Obsolete `[Setup]` directives (intentionally skipped)

🗑️ `AlwaysCreateUninstallIcon`, `BackColor`, `BackColor2`, `BackColorDirection`, `BackSolid`,
`DisableAppendDir`, `DontMergeDuplicateFiles`, `MessagesFile`, `UninstallIconFile`, `UninstallIconName`,
`UninstallStyle`, `WindowResizable`, `WindowShowCaption`, `WindowStartMaximized`, `WindowVisible`,
`WizardResizable`

### Constants

✅ Complete for every constant documented at the time of the last check (58 entries). Constants removed or
deprecated by Inno Setup are annotated as such and struck through in completion: `{hwnd}` (removed in 6.4) and
the deprecated `{pf}`, `{pf32}`, `{pf64}`, `{cf}`, `{cf32}`, `{cf64}`, `{fonts}`, `{sendto}`.

### Preprocessor (ISPP)

✅ The specification is fully covered: 24 directives, 13+ predefined variables and the complete built-in
function set (~104 functions with signature, result type and description).

| Directive                                 | Parsing | Validation                                                                                 | Completion                                      | References                                                  |
|-------------------------------------------|:-------:|--------------------------------------------------------------------------------------------|-------------------------------------------------|-------------------------------------------------------------|
| `#define`                                 |    ✅    | ✅ expression + types + unused + scope keyword                                              | ✅ keyword + scope keyword + names + functions   | ✅                                                           |
| `#undef`                                  |    ✅    | ✅ keyword + scope keyword + no-match warning                                               | ✅ keyword + scope keyword + `#define` names     | ✅ → `#define`                                               |
| `#dim` / `#redim`                         |    ✅    | ✅ name + size/index type + bounds + scope keyword + inline init + `#redim` needs `#dim`    | ✅ keyword + scope keyword + array names         | ✅ `#dim` ↔ `arr[i]` / `#redim` / `#define arr[i]` / `DimOf` |
| `#include` / `#file`                      |    ✅    | ✅ path + effective script                                                                  | ✅ keyword + file path                           | ✅ file                                                      |
| `#emit` / `#expr` / `#insert` / `#append` |    ✅    | ✅ keyword                                                                                  | ✅ keyword                                       | —                                                            |
| `#if` / `#elif`                           |    ✅    | ✅ expression + types + structure + boolean literal                                         | ✅ keyword                                       | ✅ → `#define`                                               |
| `#else` / `#endif`                        |    ✅    | ✅ keyword + structure (opener ↔ `#endif`)                                                  | ✅ keyword                                       | —                                                            |
| `#ifdef` / `#ifndef`                      |    ✅    | ✅ name + structure (undefined names are not an error)                                      | ✅ keyword + name                                | ✅ → `#define`                                               |
| `#ifexist` / `#ifnexist`                  |    ✅    | ✅ string expression + type + structure + file existence                                    | ✅ keyword + expression                          | ✅ → `#define` + file                                        |
| `#for`                                    |    ✅    | ✅ structure (`{init;cond;incr} body`) + loop variable + condition type + slot expression   | ✅ keyword + loop variable + names/subs          | ✅ loop variable (local) + `#define` / `#dim` / `#sub`       |
| `#sub` / `#endsub`                        |    ✅    | ✅ name + structure (`#sub` ↔ `#endsub`)                                                    | ✅ keyword + sub names                           | ✅ name ↔ `#for` body / expression                           |
| `#pragma`                                 |    ✅    | ✅ sub-command + option flags + expression/type                                             | ✅ keyword + sub-command + flags                 | ✅ inside expression arguments                               |
| `#error`                                  |    ✅    | ✅ keyword                                                                                  | ✅ keyword                                       | —                                                            |

An unknown `#…` directive keyword is reported as an error (case-insensitive), like an unknown section, flag or
constant.

What the preprocessor support means in practice:

- **Expression analysis:** the value of a `#define` and of a macro body is parsed as a real expression. Missing
  operators, unbalanced parentheses and type violations (`"a" * "b"`, `1 + "s"`, `"a" < 1`, …) are reported as
  errors at the exact offending token; references to other `#define`s are resolved recursively (with a cycle
  guard). An operand whose type cannot be determined statically is treated as "any" and never produces a false
  error.
- **Call checking:** built-in calls and calls to function-like macros are validated for argument count (optional
  parameters included), argument types, by-reference parameters (`str*`/`int*`, which need a bare macro name),
  symbol parameters (`Defined`, `TypeOf`, `DimOf`) and results that carry no value.
- **Macro parameters:** declared like in ISPP (`#define M(int A, str *S, B = 10)`) — typed for validation,
  offered in completion, and resolvable/renameable inside the macro body only.
- **Conditional compilation:** `#if` branches are evaluated three-valued. Provably dead branches are dimmed and
  report no problems, an undecidable condition keeps both branches and gets a blue underline, and the effective
  script prunes accordingly. Integer *and* string comparisons (case-insensitive, as ISPP does it),
  `#ifexist`/`#ifnexist` and the `/D` symbols of the selected build configuration are taken into account.
- **Inline emission `{#…}`:** value-bearing predefined variables (`{#__LINE__}`, `{#SourcePath}`, `{#Ver}`, …),
  your own `#define`s and the build configuration's `/D` symbols are valid there. The valueless symbols
  (`__WIN32__`, `ISPP_INVOKED`, `ISCC_INVOKED`, `WINDOWS`, `UNICODE`) exist only for conditions and are
  deliberately excluded from `{#…}`.
- **Line continuation:** a `#…` line ending with `\` is treated as a single directive everywhere — parsing,
  validation, completion and formatting.

The MkDocs site carries a dedicated **Inno Setup Preprocessor** chapter with one page per directive
(`define`, `undef`, arrays, `include`, output, conditionals, `for`, `sub`, `pragma`, `error`) in all four
locales.

### Editor Features

| Feature                                         | Status | Notes                                                                                                                                                                                                         |
|-------------------------------------------------|--------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Syntax highlighting (script)                    | ✅      |                                                                                                                                                                                                               |
| Syntax highlighting (ISPP)                       | ✅      |                                                                                                                                                                                                               |
| Code completion — sections                       | ✅      |                                                                                                                                                                                                               |
| Code completion — attributes                     | ✅      |                                                                                                                                                                                                               |
| Code completion — flags                          | ✅      |                                                                                                                                                                                                               |
| Code completion — constants                      | ✅      |                                                                                                                                                                                                               |
| Code completion — ISPP directives                | ✅      |                                                                                                                                                                                                               |
| Code completion — `{#…}` ISPP variables          | ✅      | After `{` and `{#`: own `#define`s, value-bearing predefined variables and the build configuration's `/D` symbols; valueless symbols excluded                                                                 |
| Code completion — `[Languages]`                  | ✅      | Built-in language names + `MessagesFile`, with flag icons                                                                                                                                                     |
| Code completion — `LanguageID`                   | ✅      | Windows LCIDs: name + flag + greyed `$hex` id, built-in languages first                                                                                                                                       |
| Code completion — message language prefix        | ✅      | `[Messages]`/`[CustomMessages]`: `lang.` prefix list (declared `[Languages]` only, with flag + name) followed by the message ids                                                                              |
| Code completion — `{cm:…}`                       | ✅      | Declared custom message names                                                                                                                                                                                 |
| `{cm:…}` reference / find usages / rename        | ✅      | Resolves to `[CustomMessages]`, red when unresolved; rename keeps all language variants in sync                                                                                                               |
| `lang.` prefix reference / find usages / rename  | ✅      | Prefix resolves to a `[Languages] Name`, red when undeclared; renaming the name updates every prefix                                                                                                          |
| Inlay hints — computed `#define` value           | ✅      | End-of-line hint with the statically computed value, calls to own function-like macros included. Excluded: plain literals, function-like macros themselves, array element defines, value-less defines and everything not computable (built-in calls among them). Toggle under *Editor \| Inlay Hints* |
| Inlay hints — language flags                     | ✅      | Flag + English name before `[Languages] MessagesFile` and `[LangOptions] LanguageID`, and the flag of the referenced language before a `lang.` message key                                                    |
| Inlay hints — section type                       | ✅      | Icon behind the `[` of a section header showing its entry syntax (`=`, `:` or Pascal)                                                                                                                         |
| Brace matching `[]`, `{}`, `()`                  | ✅      |                                                                                                                                                                                                               |
| Code folding                                     | ✅      | Sections, multi-pair entries, and `#if … #endif` / `#sub … #endsub` blocks                                                                                                                                    |
| Structure view                                   | ✅      | Own icons per section and entry kind                                                                                                                                                                          |
| Quick documentation (script)                     | ✅      | Sections, attributes, flags and constants: description, type, `required`/`deprecated` markers and `since`/`until` range                                                                                       |
| Quick documentation (ISPP)                       | ✅      | Directive keyword → description + syntax + version range; predefined variable → type + description; built-in function → description, result type, signature, per-parameter details and a side-effect note; own `#define`/macro → inferred type and computed value |
| Parameter info (ISPP calls)                      | ✅      | `Ctrl+P` shows the parameter list of the enclosing built-in or macro call and highlights the current argument                                                                                                 |
| Conditional compilation (`#if` branches)         | ✅      | See [Preprocessor](#preprocessor-ispp)                                                                                                                                                                        |
| ISPP line continuation (trailing `\`)            | ✅      | Parsing, injection and formatting treat it as one directive                                                                                                                                                   |
| Find usages                                      | ✅      | ISPP `#define` references (`#undef` included) and macro parameters inside their macro                                                                                                                         |
| Rename refactoring                               | ✅      | ISPP identifiers, incl. function-like macro parameters (scoped to their `#define`)                                                                                                                            |
| Commenter (`Ctrl+/`)                             | ✅      |                                                                                                                                                                                                               |
| Quote handler                                    | ✅      |                                                                                                                                                                                                               |
| Code formatting                                  | ✅      | Spacing around `=` / `:` / `;` and `[ ]`, blank lines between sections, preprocessor operators and line continuations; configurable under Code Style                                                          |
| ISPP language injection                          | ✅      | Preprocessor lines are injected into the script, `[Code]` included                                                                                                                                            |
| Semantic annotations / errors                    | ✅      | Unknown sections, attributes, flags, constants and `#…` directives                                                                                                                                            |
| `{#…}` in `MessagesFile` path resolution         | ✅      | Path-relevant predefined variables (`{#SourcePath}`, `{#__DIR__}`, `{#CompilerPath}`, `{#SysPath}`) are expanded; dynamic ones simply stay unresolved instead of producing a false error                      |
| Deprecated members struck through in completion   | ✅      | Deprecated sections, attributes, flags, constants and message keys, evaluated per file type                                                                                                                   |
| Build integration                                | ✅      | Compile `.iss` via context menu or gutter icon, optionally on project build; named build configurations in the project's `.build` directory                                                                    |
| `[Code]` Pascal intellisense                     | ❌      | See below                                                                                                                                                                                                     |

### Not implemented yet

| Topic                                | Status | Details                                                                                                                                                                                                                                                                     |
|--------------------------------------|--------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `[Code]` Pascal Script intellisense  | ❌      | The body of `[Code]` is recognised and handed to the parser as opaque lines: no syntax errors, but also no completion, navigation or analysis for Pascal itself. `//` comments and preprocessor lines inside `[Code]` do keep their own tooling. Planned as a follow-up step. |
| Very large scripts with many `#if`   | ⚠️     | Collecting the preprocessor directives of a file grows faster than linearly with file size — roughly 1500 conditional blocks take about 25 s. Irrelevant for realistic script sizes; a performance test guards the behaviour.                                                 |

### Validated against the official examples

Every release is checked against the complete `Examples/` directory of `jrsoftware/issrc` (a pinned tag). Each
example script must

1. parse without a single error element,
2. resolve every hard reference,
3. highlight without any error or warning annotation,
4. produce the recorded effective script for every relevant combination of externally supplied preprocessor
   symbols, and
5. compile end-to-end with the real `ISCC.exe` through the plugin's build pipeline.

The example sources are downloaded on demand and are never part of this repository.

---

## Getting Started (Development)

### Prerequisites

| Tool          | Version                                              |
|---------------|------------------------------------------------------|
| JDK           | 21 or later                                          |
| IntelliJ IDEA | 2024.1 or later (for IDE-assisted development)       |
| Gradle        | Provided via Gradle Wrapper — no installation needed |

### Build

```bash
# Clone the repository
git clone https://github.com/KleinerHacker/inno-setup.git
cd inno-setup

# Generate parsers/lexers and compile every module
./gradlew assemble

# Run all tests (every module carries the tests for its own code)
./gradlew test

# …or run a single suite. A test class whose name ends in `IT` is an integration test,
# everything else is a developer test — the fast inner-loop suite.
./gradlew developerTest
./gradlew integrationTest
```

> [!NOTE]
> `integrationTest` (and `test`, which includes it) validates the plugin against the **official Inno Setup
> example scripts**. Those are third-party sources and are not part of this repository: they are downloaded
> from a pinned `jrsoftware/issrc` tag into `language/script/build/inno-setup-examples` before the run and
> removed again afterwards, so the first run of these suites needs network access. `developerTest` stays
> fully offline.
>
> Only source-free fingerprints of the expected effective script are committed, under
> `language/script/src/test/resources/examples`. After an intentional change to the effective-script
> computation, refresh them with
> `./gradlew :language:script:integrationTest -PupdateExampleFingerprints` and review the diff.

> [!IMPORTANT]
> `:plugin:integrationTest` additionally **compiles** every official example through the plugin's build
> pipeline with the real `ISCC.exe`, so it requires a locally installed Inno Setup (Windows). The default
> location is `C:\Program Files (x86)\Inno Setup 6`; point the build elsewhere with
> `./gradlew integrationTest -PinnoSetupHome="D:\Tools\Inno Setup 6"` or the `INNO_SETUP_HOME` environment
> variable. Examples whose payload is not part of the downloaded corpus are excluded — with a reason — in
> `plugin/src/test/resources/examples/build-skip.yaml`.

```bash
# Build the distributable plugin ZIP
./gradlew :plugin:buildPlugin
# → plugin/build/distributions/inno-setup-<version>.zip
```

### Run in a sandboxed IDE

```bash
./gradlew runIde
```

This launches a fresh IntelliJ IDEA instance with the plugin loaded, isolated from your regular IDE installation. Open
or create any `.iss` file to try the plugin live.

### Run / Debug from IntelliJ IDEA

Preconfigured run configurations are included in `.run/`:

| Configuration         | What it does                                             |
|-----------------------|----------------------------------------------------------|
| **Run Plugin**        | Launches `:runIde` — opens a sandbox IDE with the plugin |
| **Run Tests**         | Runs `:test`                                             |
| **Run Verifications** | Runs `:verifyPlugin` to check compatibility              |

### Project Structure

A **Gradle multi-module** build with the dependency chain `:plugin → :language:script → :language:preprocessor`.
The root project is a pure aggregator (no code, no `plugin.xml`).

```
.
├── language/
│   ├── preprocessor/        ISPP preprocessor language (lexer/parser/PSI, highlighter, annotator,
│   │                        brace matcher, references, expression engine, ISPP spec, PluginBundle)
│   │   └── src/main/{kotlin, resources/{META-INF, parsing, spec, messages}}
│   └── script/              Inno Setup language: section/INI grammar (.iss/.isl/.ist), file types,
│       │                    highlighter, folding, annotator, references, include infra, ISPP injector,
│       │                    spec/settings services
│       └── src/main/{kotlin, resources/{META-INF, parsing, spec, icons}}
├── plugin/                  Publishable plugin: IDE features, build/run, settings UI, main plugin.xml,
│   │                        color schemes, icons
│   └── src/{main, test}/
├── buildSrc/                Shared Gradle convention (inno-setup.platform-module)
├── <module>/build/generated/  Generated parser/lexer/PSI per module (auto-generated)
├── docs/                    MkDocs documentation site
├── build.gradle.kts         Root aggregator (Dokka over all modules, kover merge, MkDocs, generateSources)
└── settings.gradle.kts
```

> **Note:** Generated sources live per-module under `<module>/build/generated/`. Regenerate them via
`./gradlew generateSources` (root umbrella) or the per-module `generateIs*Parser`/`generateIs*Lexer` tasks.
> Never edit them by hand — they are overwritten on every build.

---

## Manual Installation

The plugin is **not yet available on the JetBrains Marketplace**. Install it manually from the built ZIP:

### Step 1 — Build the plugin ZIP

```bash
./gradlew buildPlugin
```

The output is written to `build/distributions/inno-setup-<version>.zip`.

### Step 2 — Install in your IDE

1. Open your JetBrains IDE and go to **Settings / Preferences → Plugins**
2. Click the **⚙ gear icon** in the top-right corner of the Plugins panel
3. Choose **Install Plugin from Disk…**
4. Navigate to `build/distributions/` and select the `.zip` file
5. Click **OK**, then **Restart IDE** when prompted

After the restart, any file with the `.iss` extension will be handled by the plugin automatically.

---

## Contributing

[Bug reports](https://github.com/KleinerHacker/intellij-plugin.inno-setup/issues)
and [pull requests](https://github.com/KleinerHacker/intellij-plugin.inno-setup/pulls) are welcome. Please open an issue
first to discuss larger changes.

---

## Trademarks & Disclaimer

This is an unofficial, community-developed plugin. It is **not** affiliated with, endorsed by, or sponsored by Jordan
Russell, Martijn Laan or jrsoftware.org. "Inno Setup" and the Inno Setup logo are the property of their respective
owners and are used here only to identify the software this plugin supports.

Inno Setup itself is not distributed with this plugin — it has to be installed separately. The section, parameter,
constant and preprocessor descriptions shipped with the plugin are derived from the official
[Inno Setup documentation](https://jrsoftware.org/ishelp/), Copyright © 1997-2026 Jordan Russell, portions
Copyright © 2000-2026 Martijn Laan.

---

## Licenses

This plugin is licensed under the [Apache License, Version 2.0](LICENSE).

See the [licensing overview](https://kleinerhacker.github.io/intellij-plugin.inno-setup/latest/licensing/) for
third-party attribution and the
[dependency report](https://kleinerhacker.github.io/intellij-plugin.inno-setup/latest/licences/) for the licenses of
all bundled dependencies.
